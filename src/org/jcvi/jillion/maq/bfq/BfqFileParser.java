/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.trace.fastq.FastqParser;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqSingleVisitIterator;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;

/**
 * {@code BfqFileParser} is a {@link FastqParser}
 * implementation that can read 
 * MAQ's binary fastq file format ({@literal .bfq} files).
 * @author dkatzel
 *
 */
public abstract class BfqFileParser implements FastqParser{

	
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
	 * used by the {@link BfqFileWriterBuilder} )
	 * that produced the file.
	 * @return a new {@link FastqParser} instance;
	 * will never be null.
	 * @throws IOException if there is a problem file does not exist.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static FastqParser create(File bfqFile, ByteOrder endian) throws IOException{
		return new BfqFileBasedParser(bfqFile, endian);
	}
	
	/**
	 * Create a new {@link FastqParser} instance
	 * to parse the given binary fastq (bfq) enoded inputStream using
	 * the system endian.
	 * This is the same as calling: 
	 * {@link #create(InputStream, ByteOrder) create(bfqFileStream, ByteOrder.nativeOrder())}
	 * @param bfqFileStream the binary fastq encoded {@link InputStream}to parse.
	 * @return a new {@link FastqParser} instance;
	 * will never be null.
	 * @throws IOException if the file does not exist.
	 * @throws NullPointerException if bfqFile is null.
	 */
	public static FastqParser create(InputStream bfqFileStream) throws IOException{
		return create(bfqFileStream, ByteOrder.nativeOrder());
	}
	/**
	 * Create a new {@link FastqParser} instance
	 * to parse the given binary fastq (bfq) enoded inputStream using
	 * the given {@link ByteOrder}.
	 * @param bfqFileStream the binary fastq encoded {@link InputStream}to parse.
	 * @param endian the {@link ByteOrder} to use to parse the file.
	 * Make sure the endian matches the endian of the machine that 
	 * Maq was run on (or matches the {@link ByteOrder}
	 * used by the {@link BfqFileWriterBuilder} )
	 * that produced the file.
	 * @return a new {@link FastqParser} instance;
	 * will never be null.
	 * @throws IOException if there is a problem file does not exist.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static FastqParser create(InputStream bfqFileStream, ByteOrder endian) throws IOException{
		return new BfqInputStreamParser(bfqFileStream, endian);
	}
	
	
	private BfqFileParser(ByteOrder endian) throws IOException {	
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
	}

	
	@Override
	public boolean canCreateMemento() {
		return true;
	}
	
	@Override
	public boolean isReadOnceOnly() {
		return false;
	}
	@Override
	public boolean canParse() {
		return true;
	}

	@Override
	public void parse(FastqVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		try(OpenAwareInputStream in = createInputStream()){
			
			parseBqfData(visitor, in, 0);
		}
		
	}

	
	private class BfqIterator implements FastqSingleVisitIterator{

		private final OpenAwareInputStream in;
		long currentOffset;
		private final Callback callback;
		
		BfqIterator(OpenAwareInputStream in, long currentOffset){
			callback = createCallback(currentOffset);
			this.in = in;
			this.currentOffset = currentOffset;
		}
		@Override
		public void close() throws IOException {
			in.close();
			
		}

		@Override
		public boolean hasNext() {
			return callback.keepParsing() && in.isOpen();
		}

		@Override
		public void next(FastqVisitor visitor) {
			while(in.isOpen() && callback.keepParsing()){
				try {
					currentOffset = parseSingleRecord(visitor, in, currentOffset, callback);
				} catch (IOException e) {
					Sneak.sneakyThrow(e);
				}
			}
			if(!callback.keepParsing()){
				
				visitor.halted();
			}
			visitor.visitEnd();
			
		}
		
	}
	private void parseBqfData(FastqVisitor visitor, OpenAwareInputStream in, long offset) throws IOException {
		
		long currentOffset = offset;
		Callback callback = createCallback(currentOffset);
		while(in.isOpen() && callback.keepParsing()){
			currentOffset = parseSingleRecord(visitor, in, currentOffset, callback);
		}
		if(!callback.keepParsing()){
			
			visitor.halted();
		}
		visitor.visitEnd();
	}
	private long parseSingleRecord(FastqVisitor visitor, OpenAwareInputStream in, long currentOffset, Callback callback)
			throws IOException {
		callback.updateCurrentOffset(currentOffset);
		int nameLength =IOUtil.readSignedInt(in, endian);
		String name = readNullTerminatedString(in, nameLength);
		int numBases =IOUtil.readSignedInt(in, endian);
		FastqRecordVisitor recordVisitor =visitor.visitDefline(callback, name, null);
		//each record is 8 bytes for the length fields
		//plus the number of bases 
		//plus the name length (which includes null terminal)
		currentOffset += 8 + numBases + nameLength;
		if(recordVisitor ==null){
			//skip
			IOUtil.blockingSkip(in, numBases);
		}else{				
			byte[] basesAndQualities= IOUtil.readByteArray(in, numBases);
			
			StringBuilder basesBuilder = new StringBuilder(numBases);
			QualitySequenceBuilder qualitiesBuilder = new QualitySequenceBuilder(numBases)
															.turnOffDataCompression(true);
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
			
			recordVisitor.visitNucleotides(basesBuilder.toString());
			if(callback.keepParsing()){
				recordVisitor.visitQualities(qualitiesBuilder.build());
			}
			if(callback.keepParsing()){
				recordVisitor.visitEnd();
			}else {
				recordVisitor.halted();
			}
		}
		return currentOffset;
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
		try(OpenAwareInputStream in =createInputStream()){
			long startOffset = bfqMemento.startOffset;
			IOUtil.blockingSkip(in, startOffset);			
			
			parseBqfData(visitor, in, startOffset);
		}
		
	}
	
	
	
	@Override
	public FastqSingleVisitIterator iterator() throws IOException {
		return new BfqIterator(createInputStream(), 0);
	}
	@Override
	public FastqSingleVisitIterator iterator(FastqVisitorMemento memento) throws IOException {
		if(!(memento instanceof BfqMemento)){
			throw new IllegalArgumentException("invalid memento type, must be created by this class");
		}
		BfqMemento bfqMemento = (BfqMemento) memento;
		if(bfqMemento.parserInstance != this){
			throw new IllegalArgumentException("invalid memento, must be created by this parser instance");
		}
		OpenAwareInputStream in =createInputStream();
		long startOffset = bfqMemento.startOffset;
		try {
			IOUtil.blockingSkip(in, startOffset);	
		}catch(IOException e) {
			IOUtil.closeAndIgnoreErrors(in);
			throw e;
		}
		return new BfqIterator(createInputStream(), startOffset);
	}
	abstract OpenAwareInputStream createInputStream() throws IOException;
	
	abstract Callback createCallback(long startOffset);
	
	
	private class Callback implements FastqVisitorCallback{
		private final AtomicBoolean keepParsing;
		private long currentOffset;
		
		
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
			return new BfqMemento(BfqFileParser.this, currentOffset);
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
		private final BfqFileParser parserInstance;
		private final long startOffset;
		
		public BfqMemento(BfqFileParser parserInstance, long startOffset) {
			this.parserInstance = parserInstance;
			this.startOffset = startOffset;
		}
		
		
	}
	
	
	
	private static class BfqFileBasedParser extends BfqFileParser{
		private final File bfqFile;
		
		private BfqFileBasedParser(File bfqFile, ByteOrder endian) throws IOException {
			super(endian);
			this.bfqFile = bfqFile;
		}

		@Override
		protected OpenAwareInputStream createInputStream() throws IOException {
			return new OpenAwareInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(bfqFile))));
		}

		@Override
		protected Callback createCallback(long startOffset) {
			return new Callback(startOffset);
		}

        @Override
        public Optional<File> getFile() {
            return Optional.of(bfqFile);
        }

		
	}
	
	private static class BfqInputStreamParser extends BfqFileParser{
		

		private final OpenAwareInputStream in;
		private boolean hasBeenReadBefore=false;
		private BfqInputStreamParser(InputStream in, ByteOrder endian) throws IOException {
			super(endian);
			this.in = new OpenAwareInputStream(in);
		}

		@Override
		protected synchronized OpenAwareInputStream createInputStream() throws IOException {
			if(!hasBeenReadBefore){
				hasBeenReadBefore = true;
				return in;
			}
			throw new IOException("already read");
			
		}

		@Override
        public Optional<File> getFile() {
            return Optional.empty();
        }

        @Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public boolean isReadOnceOnly() {
			return true;
		}

		@Override
		public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
		}

		@Override
		protected Callback createCallback(long startOffset) {
			return new NoMementoCallback(startOffset);
		}

		@Override
		public FastqSingleVisitIterator iterator(FastqVisitorMemento memento) throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
		}
		
		

		
	}
	
	private final class NoMementoCallback extends Callback {
		private NoMementoCallback(long startOffset) {
			super(startOffset);
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create mementos");
		}
	}

}
