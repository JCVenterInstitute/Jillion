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
package org.jcvi.jillion.internal.core.io;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.FIFOQueue;
/**
 * {@code TextLineParser} can read lines from on {@link InputStream}.  The main
 * difference between TextLineParser and other similar JDK classes is TextLineParser
 * will include the end of line characters in the {@link #nextLine()}
 * methods.  Most JDK classes chop off these characters and the few classes
 * that could could be configured to include these characters are slow.
 * This class considers a line to be terminated by either '\n',
 * (UNIX format) or '\r\n' (Windows/DOS) or '\r' (Apple family until Mac OS 9). 
 * <p/>
 * This class is not Thread-safe
 * @author dkatzel
 *
 *
 */
public final class TextLineParser implements Closeable{
	/**
	 * {@value} chars, is the initial
	 * capacity since this is probably
	 * going to be mostly used by 
	 * human readable text files and
	 * genomic file formats which often
	 * are < 100 characters per line.
	 * If the line is longer, then the buffer
	 * will grow accordingly.
	 */
	private static final int INITIAL_LINE_CAPACITY = 200;
	
	/**
	 * Our pushed back byte is not set.
	 */
	private static final int NOT_SET = -2;
	/**
	 * End of File.
	 */
	private static final int EOF = -1;
	/**
	 * Line feed.
	 */
	private static final char LF = '\n';
	/**
	 * Carriage return.
	 */
	private static final char CR = '\r';
	
	private final InputStream in;
	private final Object endOfFile = new Object();
	private final FIFOQueue<Object> nextQueue = new FIFOQueue<Object>();
	boolean doneFile = false;
	
	private long position;
	private int numberOfBytesInNextLine;
	/**
	 * This is an extra byte we read from
	 * the previous line that we
	 * want to "unread" so we can read it again
	 * in the next line.
	 * 
	 * This is used to distinguish from
	 * some older operating systems which use 
	 * '\r\n' from '\r' as end of line strings.
	 * If it's the later, the byte after 
	 * '\r' is "unread" so it can be read again
	 * as the beginning of the next line.
	 * 
	 *  This is used instead of a PushBackInputStream
	 *  because it is faster to only check
	 *  the unread byte once per
	 *  getLine() call instead of every read().
	 */
	private int pushedBackValue=NOT_SET;
	
	public TextLineParser(File f) throws IOException{
		this(new BufferedInputStream(new FileInputStream(f)));
	}
	public TextLineParser(InputStream in) throws IOException{
		this(in, 0L);
	}
	
	public TextLineParser(InputStream in, long initialPosition) throws IOException{
		if(in ==null){
			throw new NullPointerException("inputStream can not be null");
		}
		if(initialPosition <0){
			throw new IllegalArgumentException("initial position must be >=0");
		}
		this.position = initialPosition;
		this.in = in;
		getNextLine();
	}
	
	private void getNextLine() throws IOException{
		if(doneFile){
			return;
		}
		StringBuilder builder = new StringBuilder(INITIAL_LINE_CAPACITY);
		int value;
		if(pushedBackValue ==NOT_SET){
			value = in.read();
		}else{			
			value = pushedBackValue;
			pushedBackValue=NOT_SET;
		}
		numberOfBytesInNextLine=0;
		
		
		while(true){	
			if(value == EOF){
				doneFile =true;
				close();
				break;
			}
			numberOfBytesInNextLine++;
			builder.append((char)value);
			if(value == CR){
				//check if next value is LF
				//since CR+LF is how Windows represents an end of line
				int nextChar = in.read();
				if(nextChar == LF){
					numberOfBytesInNextLine++;
					builder.append(LF);
				}else if(nextChar !=EOF){
					//not windows formatted line
					//could be Mac 0S 9 which only uses '\r'
					//put that value back
					pushedBackValue =nextChar;
				}
				
				break;
			}
			if(value == LF){
				break;
			}
			value = in.read();
		}
		if(builder.length()>0){
			nextQueue.add(builder.toString());
		}
		if(doneFile){
			nextQueue.add(endOfFile);
		}
		
	}
	
	/**
	 * Get the number of bytes returned by
	 * {@link #nextLine()} so far.
	 * The value returned is not affected
	 * by how much looking ahead or 
	 * buffering has been done to the
	 * underlying input stream.
	 * @return a number >=0.
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * Does the inputStream have another line
	 * to read.  If there are no more lines to read,
	 * then {@link #nextLine()} will return {@code null}.
	 * @return {@code true} if there are more lines to be read;
	 * {@code false} otherwise.
	 * @see #nextLine()
	 */
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public boolean hasNextLine(){
		Object next = nextQueue.peek();
		
		return next!= endOfFile;
	}
	/**
	 * Get the next line (including end of line characters)
	 * but without advancing the position into the 
	 * stream.  Calling this method multiple times without
	 * calling {@link #nextLine()} in between will
	 * return the same String.
	 * @return the String that will be returned by 
	 * {@link #nextLine()} without actually advancing
	 * to the next line.
	 * 
	 */
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public String peekLine(){
		Object next= nextQueue.peek();
		if(next == endOfFile){
			return null;
		}
		return (String)next;
	}
	/**
	 * Get the next line (including end of line characters)
	 * as a String.
	 * @return a the next line; or {@code null} if there are no
	 * more lines.
	 * @throws IOException if there is a problem reading the next
	 * line.
	 */
	@SuppressWarnings("PMD.CompareObjectsWithEquals")
	public String nextLine() throws IOException{
		Object next= nextQueue.poll();
		if(next == endOfFile){
			return null;
		}
		position+=numberOfBytesInNextLine;
		getNextLine();
		return (String)next;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		IOUtil.closeAndIgnoreErrors(in);
		nextQueue.clear();
		
	}
	
}
