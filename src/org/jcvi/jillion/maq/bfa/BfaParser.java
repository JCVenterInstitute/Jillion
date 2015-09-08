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
package org.jcvi.jillion.maq.bfa;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.FastaRecordVisitor;
import org.jcvi.jillion.fasta.FastaVisitor;
import org.jcvi.jillion.fasta.FastaVisitorCallback;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;

/**
 * {@code BfaParser} is a {@link FastaParser}
 * implementation that can read 
 * MAQ's binary fasta file format ({@literal .bfa} files).
 * @author dkatzel
 *
 */
public abstract class BfaParser implements FastaParser{


	
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
		return new BfaFileParser(bfaFile, endian);
	}
	
	/**
	 * Create a new {@link FastaParser} instance
	 * to parse the given {@link InputStream} 
	 * which contains encoded data in binary fasta file (bfa)
	 * format using
	 * the system endian.
	 * This is the same as calling: 
	 * {@link #create(File, ByteOrder) create(bfaFileStream, ByteOrder.nativeOrder())}
	 * @param bfaFileStream the {@link InputStream}  to parse;
	 * can not be null.
	 * @return a new {@link FastaParser} instance;
	 * will never be null.
	 * @throws NullPointerException if inputstream is null.
	 */
	public static FastaParser create(InputStream bfaFileStream){
		return create(bfaFileStream, ByteOrder.nativeOrder());
	}
	/**
	 * Create a new {@link FastaParser} instance
	 * to parse the given {@link InputStream} 
	 * which contains encoded data in binary fasta file (bfa)
	 * format using the given {@link ByteOrder}.
	 * @param bfaFileStream the {@link InputStream}  to parse;
	 * can not be null.
	 * @param endian the {@link ByteOrder} to use to parse the file.
	 * Make sure the endian matches the endian of the machine that 
	 * Maq was run on (or matches the {@link ByteOrder}
	 * used by the {@link BinaryfastaFileWriterBuilder} )
	 * that produced the file.
	 * @return a new {@link FastaParser} instance;
	 * will never be null.
	 * @throws NullPointerException if either parameter is null.
	 */
	public static FastaParser create(InputStream bfaFileStream, ByteOrder endian){
		return new BfaInputStreamParser(bfaFileStream, endian);
	}
	
	private BfaParser(ByteOrder endian) {
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
	}
	
	protected abstract OpenAwareInputStream createInputStream() throws IOException;
	

	

	

	protected final void parseBfaData(FastaVisitor visitor, OpenAwareInputStream in, long offset) throws IOException {
		FastaRecordVisitor recordVisitor =null;
		long currentOffset = offset;
		Callback callback = createCallback(currentOffset);
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
	protected abstract Callback createCallback(long currentOffset);
	
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
	public void parse(FastaVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		OpenAwareInputStream in =null;
		try{
			in = createInputStream();		
			parseBfaData(visitor, in, 0);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
	
	private static final class BfaMemento implements FastaVisitorMemento{
		private final BfaParser parserInstance;
		private final long startOffset;
		
		public BfaMemento(BfaParser parserInstance, long startOffset) {
			this.parserInstance = parserInstance;
			this.startOffset = startOffset;
		}
		
		
	}

	private interface Callback extends FastaVisitorCallback{
		public boolean keepParsing();
		
		public void updateCurrentOffset(long offset);
	}
	
	private static final class BfaFileParser extends BfaParser{
		private final File bfaFile;
		
		protected BfaFileParser(File bfaFile, ByteOrder endian) throws IOException {
			super(endian);
			if(!bfaFile.exists()){
				throw new FileNotFoundException(bfaFile.getAbsolutePath());
			}		

			this.bfaFile = bfaFile;
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
		protected Callback createCallback(long currentOffset) {
			return new MementoedCallback(currentOffset);
		}
		
		@Override
		public boolean isReadOnceOnly() {
			//can read multiple times
			//since we have the 
			//reference to the File
			return false;
		}

		@Override
		protected OpenAwareInputStream createInputStream() throws IOException{
			return new OpenAwareInputStream(new BufferedInputStream(new FileInputStream(bfaFile)));
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
			long startOffset = bfaMemento.startOffset;

			OpenAwareInputStream in =null;
			try{
				//the file isn't compressed
				//so we can seek right to the offset
				//before reading anything.
				in = new OpenAwareInputStream(new BufferedInputStream(
						new RandomAccessFileInputStream(bfaFile, startOffset)));
				parseBfaData(visitor, in, startOffset);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}
		
		private final class MementoedCallback implements Callback{
			private final AtomicBoolean keepParsing;
			private long currentOffset;
			
			private MementoedCallback(){
				this(0);
			}
			private MementoedCallback(long startOffset){
				this.currentOffset = startOffset;
				keepParsing = new AtomicBoolean(true);
			}
			@Override
			public boolean canCreateMemento() {
				return true;
			}

			@Override
			public FastaVisitorMemento createMemento() {
				return new BfaMemento(BfaFileParser.this, currentOffset);
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
	}
	
	private static final class NotMementoedCallback implements Callback{
		private final AtomicBoolean keepParsing;
		
		private NotMementoedCallback(){
			keepParsing = new AtomicBoolean(true);
		}
		
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			throw new UnsupportedOperationException("mementos not supported");
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		public void updateCurrentOffset(long offset){
			//no-op
		}
	}
	
	private static final class BfaInputStreamParser extends BfaParser{
		private final OpenAwareInputStream inputStream;
		private boolean hasParsedBefore= false;
		
		public BfaInputStreamParser(InputStream inputStream, ByteOrder endian) {
			super(endian);
			this.inputStream = new OpenAwareInputStream(inputStream);
		}
		
		
		@Override
		public boolean isReadOnceOnly() {
			//can only parse Stream once
			return true;
		}
		@Override
		public synchronized void parse(FastaVisitor visitor) throws IOException {
			//wrap in synchronized block so we only
			//can parse one visitor at a time (probably at all)
			super.parse(visitor);
		}

		@Override
		public void parse(FastaVisitor visitor, FastaVisitorMemento memento)
				throws IOException {
			//we probably will never see this in real usage
			//since inputstream implementation can't make mementors...
			throw new UnsupportedOperationException("can not use mementos with inputstream");
		}

		@Override
		protected OpenAwareInputStream createInputStream() throws IOException {
			//this is a work around to fix a regression
			//where we give an empty stream
			//first time should not throw an error
			//even if there is nothing to parse.
			if(!hasParsedBefore){
				return inputStream;
			}
			hasParsedBefore = true;
			if(canParse()){
				return inputStream;
			}
			throw new IllegalStateException("can not accept visitor - inputstream is closed");			
		}
		@Override
		public boolean canParse() {
			return inputStream.isOpen();
		}
		@Override
		public boolean canCreateMemento() {
			return false;
		}


		@Override
		protected Callback createCallback(long currentOffset) {
			return new NotMementoedCallback();
	}
	}
}
