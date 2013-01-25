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
package org.jcvi.jillion.internal.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.FIFOQueue;
/**
 * {@code TextLineParser} can read lines from on {@link InputStream}.  The main
 * difference between TextLineParser and other similar JDK classes is TextLineParser
 * will include the end of line characters in the {@link #getNextLine()}
 * methods.  Most JDK classes chop off these characters and the few classes
 * that could could be configured to include these characters are slow.
 * This class considers a line to be terminated by either '\n',
 * (UNIX format) or '\r\n' (Windows/DOS) or '\r' (Apple family until Mac OS 9). 
 * <p/>This class is not Thread-safe
 * @author dkatzel
 *
 *
 */
public final class TextLineParser implements Closeable{

	private static final char LF = '\n';
	private static final char CR = '\r';
	
	private PushbackInputStream in;
	private final Object endOfFile = new Object();
	private final FIFOQueue<Object> nextQueue = new FIFOQueue<Object>();
	boolean doneFile = false;
	
	public TextLineParser(InputStream in) throws IOException{
		if(in ==null){
			throw new NullPointerException("inputStream can not be null");
		}
		this.in = new PushbackInputStream(in);
		getNextLine();
	}
	
	private void getNextLine() throws IOException{
		if(doneFile){
			return;
		}
		StringBuilder builder = new StringBuilder(200);
		
		int value;
		value = in.read();
		while(true){	
			if(value == -1){
				doneFile =true;
				close();
				break;
			}
			builder.append((char)value);
			if(value == CR){
				//check if next value is LF
				//since CR+LF is how Windows represents an end of line
				int nextChar = in.read();
				if(nextChar == LF){
					builder.append(LF);
				}else if(nextChar !=-1){
					//not windows formatted line
					//could be Mac 0S 9 which only uses '\r'
					//put that value back
					in.unread(nextChar);
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
	 * Does the inputStream have another line
	 * to read.  If there are no more lines to read,
	 * then {@link #nextLine()} will return {@code null}.
	 * @return {@code true} if there are more lines to be read;
	 * {@code false} otherwise.
	 * @see #nextLine()
	 */
	public boolean hasNextLine(){
		Object next = nextQueue.peek();
		
		return next!= endOfFile;
	}
	/**
	 * Get the next line (including end of line characters)
	 * as a String.
	 * @return a the next line; or {@code null} if there are no
	 * more lines.
	 * @throws IOException if there is a problem reading the next
	 * line.
	 */
	public String nextLine() throws IOException{
		Object next= nextQueue.poll();
		if(next == endOfFile){
			return null;
		}
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
