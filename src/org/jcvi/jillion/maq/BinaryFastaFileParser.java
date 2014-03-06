package org.jcvi.jillion.maq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;

/**
 * {@code BinaryFastaFileParser} is a {@link FastaParser}
 * implementation that can read 
 * MAQ's binary fasta file format ({@literal .bfa} files).
 * @author dkatzel
 *
 */
public final class BinaryFastaFileParser implements FastaParser{


	private final File bfaFile;
	private final ByteOrder endian;
	/**
	 * Create a new {@link FastaParser} instance
	 * to parse the given binary fasta file (bfa) using
	 * the system endian.
	 * This is the same as calling: 
	 * {@link #create(File, ByteOrder) create(bfaFile, ByteOrder.nativeOrder())}
	 * @param bfaFile the binary fasta file to parse.
	 * @return a new {@link FastaParser} instance;
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if bfaFile is null.
	 */
	public static FastaParser create(File bfaFile) throws IOException{
		return create(bfaFile, ByteOrder.nativeOrder());
	}
	/**
	 * Create a new {@link FastaParser} instance
	 * to parse the given binary fasta file (bfq) using
	 * the given {@link ByteOrder}.
	 * @param bfaFile the binary fasta file to parse.
	 * @param endian the {@link ByteOrder} to use to parse the file.
	 * Make sure the endian matches the endian of the machine that 
	 * Maq was run on (or matches the {@link ByteOrder}
	 * used by the {@link BinaryfastaFileWriterBuilder} )
	 * that produced the file.
	 * @return a new {@link FastaParser} instance;
	 * will never be null.
	 * @throws IOException if there is a problem file does not exist.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static FastaParser create(File bfaFile, ByteOrder endian) throws IOException{
		return new BinaryFastaFileParser(bfaFile, endian);
	}
	
	
	private BinaryFastaFileParser(File bfaFile, ByteOrder endian) throws IOException {
		if(!bfaFile.exists()){
			throw new FileNotFoundException(bfaFile.getAbsolutePath());
		}		
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.bfaFile = bfaFile;
		this.endian = endian;
	}

	
	@Override
	public boolean canCreateMemento() {
		return true;
	}
	@Override
	public boolean canParse() {
		return true;
	}

	@Override
	public void parse(FastaVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		OpenAwareInputStream in =null;
		try{
			in = new OpenAwareInputStream(new BufferedInputStream(new FileInputStream(bfaFile)));		
			parseBfaData(visitor, in, 0);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}

	private void parseBfaData(FastaVisitor visitor, OpenAwareInputStream in, long offset) throws IOException {
		FastaRecordVisitor recordVisitor =null;
		long currentOffset = offset;
		Callback callback = new Callback(currentOffset);
		while(in.isOpen() && callback.keepParsing()){
			callback.updateCurrentOffset(currentOffset);
			int nameLength =IOUtil.readSignedInt(in, endian);
			String name = readNullTerminatedString(in, nameLength);
			int numBases =IOUtil.readSignedInt(in, endian);
			int numCompactedLongs = IOUtil.readSignedInt(in, endian);
			recordVisitor =visitor.visitDefline(callback, name, null);
			//record length is 3 ints for lengths
			currentOffset += 12 + numCompactedLongs*16 + nameLength;
			if(recordVisitor ==null){
				//skip
				//each record is 2 * number of Compacted longs
				//once array for the bases, one array for the mask
				//and each long is 4 bytes.
				IOUtil.blockingSkip(in, numCompactedLongs *16);
			}else{				
				long[] bases =IOUtil.readLongArray(in, numCompactedLongs, endian);
				long[] masks =IOUtil.readLongArray(in, numCompactedLongs, endian);
				
				StringBuilder basesBuilder = new StringBuilder(numBases);
				//use 2 loops
				//one for [i .. length -1)
				//and another loop for the last offset
				//so we don't have to do this extra shifting and conditional checks
				//every time!
				for(int i=0; i<bases.length-1; i++){
					long maskValue = masks[i];
					long basesValue = bases[i];
					for(int j=31; j >=0; j--){						
						int baseOffset = (i<<5) | (31 - j);
						if(baseOffset >= numBases){
							break;
						}
						//shifting by 1 is like 
						//multiplying by 2
						int shiftAmount = j*2;
						//order of operations 
						//should make equations worth without
						//parenthesis but it makes it easier
						//for a human to read.
						long m = (maskValue >> shiftAmount) &3;
						long n = (basesValue>> shiftAmount) &3;
						char base =m ==0 ? 'N' : getBaseFromInt((int)n);
						basesBuilder.append(base);
					}
					
				}
				int lastOffset = bases.length-1;
				long maskValue = masks[lastOffset];
				long basesValue = bases[lastOffset];
				for(int j=31; j >=0; j--){					
					int baseOffset = (lastOffset<<5) | (31 - j);
					if(baseOffset >= numBases){
						break;
					}
					//shifting by 1 is like 
					//multiplying by 2
					int shiftAmount = j*2;
					//order of operations 
					//should make equations worth without
					//parenthesis but it makes it easier
					//for a human to read.
					long m = (maskValue >> shiftAmount) &3;
					long n = (basesValue>> shiftAmount) &3;
					char base =m ==0 ? 'N' : getBaseFromInt((int)n);
					basesBuilder.append(base);
				}
				
				recordVisitor.visitBodyLine(basesBuilder.toString());
				
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
	
	private static char getBaseFromInt(int b){
		switch(b){
			case 0 : return 'A';
			case 1 : return 'C';
			case 2: return 'G';
			case 3 : return 'T';
			//can't happen since b has to be between 0-3
			default: throw new IllegalStateException("invalid byte value : " + b);
		}
	}

	private String readNullTerminatedString(OpenAwareInputStream in,
			int nameLength) throws IOException {
		byte[] b= IOUtil.readByteArray(in, nameLength);
		//last byte should be null so don't need to read that
		return new String(b, 0, nameLength-1, IOUtil.UTF_8);
	}

	@Override
	public void parse(FastaVisitor visitor, FastaVisitorMemento memento)
			throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		if(memento == null){
			throw new NullPointerException("memento can not be null");
		}
		if(!(memento instanceof BfaMemento)){
			throw new IllegalArgumentException("invalid memento type, must be created by this class");
		}
		BfaMemento bfaMemento = (BfaMemento) memento;
		if(bfaMemento.parserInstance != this){
			throw new IllegalArgumentException("invalid memento, must be created by this parser instance");
		}
		OpenAwareInputStream in =null;
		try{
			in = new OpenAwareInputStream(new BufferedInputStream(new FileInputStream(bfaFile)));
			long startOffset = bfaMemento.startOffset;
			IOUtil.blockingSkip(in, startOffset);
			
			
			parseBfaData(visitor, in, startOffset);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
	
	private final class Callback implements FastaVisitorCallback{
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
		public FastaVisitorMemento createMemento() {
			return new BfaMemento(BinaryFastaFileParser.this, currentOffset);
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
	
	private static final class BfaMemento implements FastaVisitorMemento{
		private final BinaryFastaFileParser parserInstance;
		private final long startOffset;
		
		public BfaMemento(BinaryFastaFileParser parserInstance, long startOffset) {
			this.parserInstance = parserInstance;
			this.startOffset = startOffset;
		}
		
		
	}

}
