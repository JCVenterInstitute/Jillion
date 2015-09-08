/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
			in = createInputStream();		
			parseBqfData(visitor, in, 0);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}

	private void parseBqfData(FastqVisitor visitor, OpenAwareInputStream in, long offset) throws IOException {
		FastqRecordVisitor recordVisitor =null;
		long currentOffset = offset;
		Callback callback = createCallback(currentOffset);
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
				boolean turnOffCompression = callback.turnOffCompression;
				
				NucleotideSequenceBuilder basesBuilder = new NucleotideSequenceBuilder(numBases)
																.turnOffDataCompression(turnOffCompression);
				QualitySequenceBuilder qualitiesBuilder = new QualitySequenceBuilder(numBases)
																.turnOffDataCompression(turnOffCompression);
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
			in = createInputStream();
			long startOffset = bfqMemento.startOffset;
			IOUtil.blockingSkip(in, startOffset);
			
			
			parseBqfData(visitor, in, startOffset);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
	
	protected abstract OpenAwareInputStream createInputStream() throws IOException;
	
	protected abstract Callback createCallback(long startOffset);
	
	
	private class Callback implements FastqVisitorCallback{
		private final AtomicBoolean keepParsing;
		private long currentOffset;
		private volatile boolean turnOffCompression;
		
		
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
		@Override
		public void turnOffDataCompression(boolean turnOffDataCompression) {
			this.turnOffCompression = turnOffDataCompression;
			
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
