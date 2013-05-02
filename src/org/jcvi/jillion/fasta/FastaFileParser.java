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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaVisitorCallback.FastaVisitorMemento;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
/**
 * {@code FastaFileParser} will parse a single 
 * fasta encoded file and call the appropriate
 * visitXXX methods on the given {@link FastaVisitor}.
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
	 * or use {@link #accept(FastaVisitor, FastaVisitorMemento)}
	 * method.
	 * @param inputStream the {@link InputStream} to parse.
	 * @throws NullPointerException if inputStream is null.
	 */
	public static FastaFileParser create(InputStream inputStream){
		return new InputStreamFastaParser(inputStream);
	}
	/**
	 * Parse the fasta file starting from the beginning 
	 * of the file (or {@link InputStream}) and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
	 * visit methods on; can not be null;
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if visitor is null.
	 */
	public void accept(FastaVisitor visitor) throws IOException{
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
	
	protected void checkNotNull(FastaVisitor visitor) {
		if(visitor==null){
			throw new NullPointerException("visitor can not be null");
		}
	}
	/**
	 * Parse the fasta file starting from the beginning 
	 * of the file (or {@link InputStream}) and call the appropriate
	 * visit methods on the given {@link FastaVisitor}.
	 * @param visitor the {@link FastaVisitor} instance to call
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
	public abstract void accept(FastaVisitor visitor, FastaVisitorMemento memento) throws IOException;
	
	protected final void parseFile(TextLineParser parser, long startOffset,
			FastaVisitor visitor) throws IOException {
		AtomicBoolean keepParsing=new AtomicBoolean(true);
		FastaRecordVisitor recordVisitor =null;
		AbstractFastaVisitorCallback callback = createNewCallback(startOffset, keepParsing);
		long currentOffset=startOffset;
		while(keepParsing.get() && parser.hasNextLine()){
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
						if(!keepParsing.get()){
							//need to set recordVisitor to null
							//so we don't call visitEnd() again
							recordVisitor=null;
							continue;
						}
					}
					String id = matcher.group(1);
		            String comment = matcher.group(3);		            
		            callback = createNewCallback(currentOffset, keepParsing);
		            recordVisitor = visitor.visitDefline(callback, id, comment);
				}else{
					//not a defline use current record visitor
					if(recordVisitor !=null){
						recordVisitor.visitBodyLine(line);
					}
				}
			}
			currentOffset +=line.length();
		}
		
		handleEndOfFile(visitor, keepParsing, recordVisitor);

	}
	protected void handleEndOfFile(FastaVisitor visitor,
			AtomicBoolean keepParsing, FastaRecordVisitor recordVisitor) {
		if(recordVisitor !=null){
			if(keepParsing.get()){
				recordVisitor.visitEnd();
			}else{
				recordVisitor.halted();
			}
		}
		//need to check keep parsing flag
		//for record visitor and visitor
		//separately in case the recordVisitor.visitEnd()
		//calls haltParsing
		if(keepParsing.get()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}

	protected abstract AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing);
	
	private abstract static class AbstractFastaVisitorCallback implements FastaVisitorCallback{
		private final AtomicBoolean keepParsing;
		
		public AbstractFastaVisitorCallback(AtomicBoolean keepParsing) {
			this.keepParsing = keepParsing;
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
			
		}

	}
	
	private static class NoMementoCallback extends AbstractFastaVisitorCallback{

		
		
		public NoMementoCallback(AtomicBoolean keepParsing) {
			super(keepParsing);
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastaVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private static class MementoCallback extends AbstractFastaVisitorCallback{

		private final long offset;
		
		public MementoCallback(long offset, AtomicBoolean keepParsing){
			super(keepParsing);
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
		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing) {
			return new MementoCallback(currentOffset, keepParsing);
		}
		public void accept(FastaVisitor visitor, FastaVisitorMemento memento) throws IOException{
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalStateException("unknown memento instance : "+memento);
			}
			
			long startOffset = ((OffsetMemento)memento).getOffset();
			InputStream inputStream=null;
			
			try{
				inputStream = new BufferedInputStream(new RandomAccessFileInputStream(fastaFile, startOffset));
				TextLineParser parser = new TextLineParser(inputStream);
				parseFile(parser, startOffset, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(inputStream);
			}
		}
		@Override
		protected InputStream getInputStream() throws IOException {
			//start parsing from beginning of file.
			return new BufferedInputStream(new FileInputStream(fastaFile));
		}
		
		
	}
	private static class InputStreamFastaParser extends FastaFileParser{
		private final OpenAwareInputStream inputStream;

		public InputStreamFastaParser(InputStream inputStream) {
			this.inputStream = new OpenAwareInputStream(inputStream);
		}
		protected AbstractFastaVisitorCallback createNewCallback(long currentOffset, AtomicBoolean keepParsing) {
			return new NoMementoCallback(keepParsing);
		}
		@Override
		public synchronized void accept(FastaVisitor visitor) throws IOException {
			//wrap in synchronized block so we only
			//can parse one visitor at a time (probably at all)
			super.accept(visitor);
		}

		@Override
		public void accept(FastaVisitor visitor, FastaVisitorMemento memento)
				throws IOException {
			//we probably will never see this in real usage
			//since inputstream implementation can't make mementors...
			throw new UnsupportedOperationException("can not use mementos with inputstream");
		}

		@Override
		protected InputStream getInputStream() throws IOException {
			if(inputStream.isOpen()){
				return inputStream;
			}
			throw new IOException("inputstream is closed");			
		}
	}
}
