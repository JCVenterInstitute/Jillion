/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code FastaFileParser} will parse a single 
 * fasta encoded file and call the appropriate
 * visitXXX methods on the given {@link FastaFileVisitor}.
 * @author dkatzel
 *
 */
public abstract class FastaFileParser {
	/**
	 * Pattern to match to find the defline for each record in
	 * the fasta file.  Group 1 is the id and Group 3 is the optional
	 * comment which will return null if there is no comment. (Group 2 is not to be used)
	 */
	private static final Pattern DEFLINE_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");

	/**
	 * Create a new {@link FastaFileParser} instance
	 * that will parse the given fasta encoded
	 * file.
	 * @param fastaFile the file to parse.
	 * @throws IOException if there is a problem opening the file.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public static FastaFileParser create(File fastaFile) throws IOException{
		return new FileFastaParser(fastaFile);
	}
	/**
	 * Create a new {@link FastaFileParser} instance
	 * that will parse the given fasta encoded
	 * inputStream.  Please Note that inputStream implementations
	 * of the FastaFileParser can not create {@link FastaVisitorMemento}s
	 * or use {@link #accept(FastaFileVisitor, FastaVisitorMemento)}
	 * method.
	 * @param fastaFile the file to parse.
	 * @throws IOException if there is a problem opening the file.
	 * @throws NullPointerException if fastaFile is null.
	 * @see #accept(FastaFileVisitor, FastaVisitorMemento).
	 */
	public static FastaFileParser create(InputStream inputStream) throws IOException{
		return new InputStreamFastaParser(inputStream);
	}
	/**
	 * Parse the fasta file starting from the beginning 
	 * of the file (or {@link InputStream}) and call the appropriate
	 * visit methods on the given {@link FastaFileVisitor}.
	 * @param visitor the {@link FastaFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if visitor is null.
	 */
	public void accept(FastaFileVisitor visitor) throws IOException{
		checkNotNull(visitor);
		InputStream in = null;		
		try{
			in = getInputStream();			
			TextLineParser parser = new TextLineParser(in);
			parseFile(parser, 0L, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	/**
	 * Create an inputStream that starts reading at the beginning
	 * of the fasta file.
	 * @return an {@link InputStream} can not be null.
	 * @throws IOException if there is a problem creating the {@link InputStream}.
	 */
	protected abstract InputStream getInputStream()  throws IOException;
	
	protected void checkNotNull(FastaFileVisitor visitor) {
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
	}
	/**
	 * Parse the fasta file starting from the beginning 
	 * of the file (or {@link InputStream}) and call the appropriate
	 * visit methods on the given {@link FastaFileVisitor}.
	 * @param visitor the {@link FastaFileVisitor} instance to call
	 * visit methods on; can not be null;
	 * @param memento the {@link FastaVisitorMemento} instance
	 * to tell the parser where to start parsing from;
	 * can not be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if either the visitor or memento are null.
	 * @throws UnsupportedOperationException if mementos are not supported by this
	 * parser implementation (for example when parsing an {@link InputStream}
	 * instead of a {@link File}).
	 */
	public abstract void accept(FastaFileVisitor visitor, FastaVisitorMemento memento) throws IOException;
	
	protected final void parseFile(TextLineParser parser, long currentOffset,
			FastaFileVisitor visitor) throws IOException {
		boolean keepParsing=true;
		FastaRecordVisitor recordVisitor =null;
		AbstractFastaVisitorCallback callback = createNewCallback(currentOffset);
		while(keepParsing && parser.hasNextLine()){
			String line=parser.nextLine();
			String trimmedLine = line.trim();
			if(!trimmedLine.isEmpty()){
				Matcher matcher = DEFLINE_LINE_PATTERN.matcher(trimmedLine);
				if(matcher.find()){
					if(recordVisitor !=null){
						recordVisitor.visitEnd();
						//need to check again the keep parsing flag 
						//incase the callback was used to stop in the previous
						//called to visitEnd()
						keepParsing=callback.keepParsing();
						if(!keepParsing){
							//need to set recordVisitor to null
							//so we don't call visitEnd() again
							recordVisitor=null;
							continue;
						}
					}
					String id = matcher.group(1);
		            String comment = matcher.group(3);		            
		            callback = createNewCallback(currentOffset);
		            recordVisitor = visitor.visitDefline(callback, id, comment);
		            keepParsing=callback.keepParsing();
				}else{
					//not a defline use current record visitor
					if(recordVisitor !=null){
						recordVisitor.visitBodyLine(line);
					}
				}
			}
			currentOffset +=line.length();
		}
		if(recordVisitor !=null){
			recordVisitor.visitEnd();
		}
		visitor.visitEnd();
	}

	protected abstract AbstractFastaVisitorCallback createNewCallback(long currentOffset);
	
	private static abstract class AbstractFastaVisitorCallback implements FastaVisitorCallback{
		private volatile boolean keepParsing=true;
		
		@Override
		public void stopParsing() {
			keepParsing=false;
			
		}

		public final boolean keepParsing() {
			return keepParsing;
		}
	}
	
	private static class NoMementoCallback extends AbstractFastaVisitorCallback{

		private static NoMementoCallback INSTANCE = new NoMementoCallback();
		
		
		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private class MementoCallback extends AbstractFastaVisitorCallback{

		private final long offset;
		
		public MementoCallback(long offset){
			this.offset = offset;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			return new OffsetMemento(offset);
		}
		
	}
	
	private static class OffsetMemento implements FastaVisitorMemento{
		private final long offset;

		public OffsetMemento(long offset) {
			this.offset = offset;
		}

		public final long getOffset() {
			return offset;
		}
		
	}
	
	private static class FileFastaParser extends FastaFileParser{
		private final File fastaFile;
		
		public FileFastaParser(File fastaFile) throws FileNotFoundException{
			if(!fastaFile.exists()){
				throw new FileNotFoundException(
						String.format("fasta file %s does not exist", fastaFile.getAbsolutePath()));
			}
			this.fastaFile = fastaFile;
		}
		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset) {
			return new MementoCallback(currentOffset);
		}
		public void accept(FastaFileVisitor visitor, FastaVisitorMemento memento) throws IOException{
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalStateException("unknown memento instance : "+memento);
			}
			
			long startOffset = ((OffsetMemento)memento).getOffset();
			RandomAccessFile randomAccessFile = null;
			InputStream inputStream=null;
			
			try{
				randomAccessFile = new RandomAccessFile(fastaFile, "r");
				randomAccessFile.seek(startOffset);
				inputStream = new BufferedInputStream(new RandomAccessFileInputStream(randomAccessFile));
				TextLineParser parser = new TextLineParser(inputStream);
				parseFile(parser, startOffset, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(inputStream,randomAccessFile);
			}
		}
		@Override
		protected InputStream getInputStream() throws IOException {
			//start parsing from beginning of file.
			return new BufferedInputStream(new FileInputStream(fastaFile));
		}
		
		
	}
	private static class InputStreamFastaParser extends FastaFileParser{
		private PushbackInputStream inputStream;

		public InputStreamFastaParser(InputStream inputStream) {
			if(inputStream==null){
				throw new NullPointerException("inputStream can not be null");
			}
			this.inputStream = new PushbackInputStream(inputStream);
		}
		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset) {
			return NoMementoCallback.INSTANCE;
		}
		@Override
		public synchronized void accept(FastaFileVisitor visitor) throws IOException {
			//wrap in synchronized block so we only
			//can parse one visitor at a time (probably at all)
			super.accept(visitor);
		}

		@Override
		public synchronized void accept(FastaFileVisitor visitor, FastaVisitorMemento memento)
				throws IOException {
			//we probably will never see this in real usage
			//since inputstream implementation can't make mementors...
			throw new UnsupportedOperationException("can not use mementos with inputstream");
		}

		@Override
		protected InputStream getInputStream() throws IOException {
			//this is a hack to see if we 
			//have data left in the inputstream
			//inputStream#available is not guaranteed
			//to return a valid answer
			//so we need to actually read a byte
			//then push it back if we get something
			try {
				int value =inputStream.read();
				//put it back so we can read it again later
				inputStream.unread(value);
				return inputStream;
			} catch (IOException e) {
				//error reading inputstream so it must
				//be closed
				throw new IOException("inputstream is closed?",e);
			}
		}
	}
}
