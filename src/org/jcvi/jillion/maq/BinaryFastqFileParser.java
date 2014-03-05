package org.jcvi.jillion.maq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.trace.fastq.FastqParser;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;

/**
 * {@code BinaryFastqFileParser} is a {@link FastqParser}
 * implementation that can read 
 * MAQ's binary fastq file format ({@literal .bfq} files).
 * @author dkatzel
 *
 */
public final class BinaryFastqFileParser implements FastqParser{

	private final File bfqFile;
	private final ByteOrder endian;
	/**
	 * Create a new {@link FastqParser} instance
	 * to parse the given binary fastq file (bfq) using
	 * the system endian.
	 * This is the same as calling: 
	 * {@link #create(File, ByteOrder) create(bfqFile, ByteOrder.nativeOrder())}
	 * @param bfqFile the binary fastq file to parse.
	 * @return a new {@link FastqParser} instance;
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if bfqFile is null.
	 */
	public static FastqParser create(File bfqFile) throws IOException{
		return create(bfqFile, ByteOrder.nativeOrder());
	}
	/**
	 * Create a new {@link FastqParser} instance
	 * to parse the given binary fastq file (bfq) using
	 * the given {@link ByteOrder}.
	 * @param bfqFile the binary fastq file to parse.
	 * @param endian the {@link ByteOrder} to use to parse the file.
	 * Make sure the endian matches the endian of the machine that 
	 * Maq was run on (or matches the {@link ByteOrder}
	 * used by the {@link BinaryFastqFileWriterBuilder} )
	 * that produced the file.
	 * @return a new {@link FastqParser} instance;
	 * will never be null.
	 * @throws IOException if there is a problem file does not exist.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static FastqParser create(File bfqFile, ByteOrder endian) throws IOException{
		return new BinaryFastqFileParser(bfqFile, endian);
	}
	
	
	private BinaryFastqFileParser(File bfqFile, ByteOrder endian) throws IOException {
		if(!bfqFile.exists()){
			throw new FileNotFoundException(bfqFile.getAbsolutePath());
		}		
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.bfqFile = bfqFile;
		this.endian = endian;
	}

	@Override
	public boolean canAccept() {
		return true;
	}

	@Override
	public void parse(FastqVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		OpenAwareInputStream in =null;
		try{
			in = new OpenAwareInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(bfqFile))));		
			parseBqfData(visitor, in, 0);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}

	private void parseBqfData(FastqVisitor visitor, OpenAwareInputStream in, long offset) throws IOException {
		FastqRecordVisitor recordVisitor =null;
		long currentOffset = offset;
		Callback callback = new Callback(currentOffset);
		while(in.isOpen() && callback.keepParsing()){
			callback.updateCurrentOffset(currentOffset);
			int nameLength =IOUtil.readSignedInt(in, endian);
			String name = readNullTerminatedString(in, nameLength);
			int numBases =IOUtil.readSignedInt(in, endian);
			recordVisitor =visitor.visitDefline(callback, name, null);
			//each record is 8 bytes for the length fields
			//plus the number of bases 
			//plus the name length (which includes null terminal)
			currentOffset += 8 + numBases + nameLength;
			if(recordVisitor ==null){
				//skip
				IOUtil.blockingSkip(in, numBases);
			}else{				
				byte[] basesAndQualities= IOUtil.readByteArray(in, numBases);
				NucleotideSequenceBuilder basesBuilder = new NucleotideSequenceBuilder(numBases);
				QualitySequenceBuilder qualitiesBuilder = new QualitySequenceBuilder(numBases);
				for(int i=0; i<basesAndQualities.length; i++){
					int value = basesAndQualities[i];
					if(value ==0){
						//this is how MAQ encodes
						//not an ACGT 
						//should be converted into an N
						//with quality 0
						basesBuilder.append(Nucleotide.Unknown);
						qualitiesBuilder.append(0);
					}else{
						int qv = value & 0x3F;
						int base = value>>6 & 0x3;
						basesBuilder.append(getBaseFromInt(base));
						qualitiesBuilder.append(qv);
					}
				}
				
				recordVisitor.visitNucleotides(basesBuilder.build());
				if(callback.keepParsing()){
					recordVisitor.visitQualities(qualitiesBuilder.build());
				}
				if(callback.keepParsing()){
					recordVisitor.visitEnd();
				}
			}
		}
		if(!callback.keepParsing()){
			if(recordVisitor !=null){
				recordVisitor.halted();
			}
			visitor.halted();
		}
		visitor.visitEnd();
	}
	
	private static Nucleotide getBaseFromInt(int b){
		switch(b){
			case 0 : return Nucleotide.Adenine;
			case 1 : return Nucleotide.Cytosine;
			case 2: return Nucleotide.Guanine;
			case 3 : return Nucleotide.Thymine;
			//can't happen since b has to be between 0-3
			default: throw new IllegalStateException("invalid byte value");
		}
	}

	private String readNullTerminatedString(OpenAwareInputStream in,
			int nameLength) throws IOException {
		byte[] b= IOUtil.readByteArray(in, nameLength);
		//last byte should be null so don't need to read that
		return new String(b, 0, nameLength-1, IOUtil.UTF_8);
	}

	@Override
	public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
			throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		if(memento == null){
			throw new NullPointerException("memento can not be null");
		}
		if(!(memento instanceof BfqMemento)){
			throw new IllegalArgumentException("invalid memento type, must be created by this class");
		}
		BfqMemento bfqMemento = (BfqMemento) memento;
		if(bfqMemento.parserInstance != this){
			throw new IllegalArgumentException("invalid memento, must be created by this parser instance");
		}
		OpenAwareInputStream in =null;
		try{
			in = new OpenAwareInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(bfqFile))));
			long startOffset = bfqMemento.startOffset;
			IOUtil.blockingSkip(in, startOffset);
			
			
			parseBqfData(visitor, in, startOffset);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
	
	private final class Callback implements FastqVisitorCallback{
		private final AtomicBoolean keepParsing;
		private long currentOffset;
		
		private Callback(){
			this(0);
		}
		private Callback(long startOffset){
			this.currentOffset = startOffset;
			keepParsing = new AtomicBoolean(true);
		}
		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			return new BfqMemento(BinaryFastqFileParser.this, currentOffset);
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		public void updateCurrentOffset(long offset){
			this.currentOffset = offset;
		}
	}
	
	private static final class BfqMemento implements FastqVisitorMemento{
		private final BinaryFastqFileParser parserInstance;
		private final long startOffset;
		
		public BfqMemento(BinaryFastqFileParser parserInstance, long startOffset) {
			this.parserInstance = parserInstance;
			this.startOffset = startOffset;
		}
		
		
	}

}
