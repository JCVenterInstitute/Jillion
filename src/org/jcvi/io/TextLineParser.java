/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.util.FIFOQueue;
/**
 * {@code TextLineParser} can read lines from on {@link InputStream}.  The main
 * difference between TextLineParser and other similar JDK classes is TextLineParser
 * will include the end of line characters in the {@link #getNextLine()}
 * methods.  Most JDK classes chop off these characters and the few classes
 * that could could be configured to include these characters are slow.
 * @author dkatzel
 *
 *
 */
public class TextLineParser implements Closeable{

	private int endOfLine;
	private InputStream in;
	private Object endOfFile = new Object();
	private FIFOQueue<Object> nextQueue = new FIFOQueue<Object>();
	boolean doneFile = false;
	public TextLineParser(InputStream in) throws IOException{
		this(in,'\n');
	}
	public TextLineParser(InputStream in,char endOfLine) throws IOException {
		if(in ==null){
			throw new NullPointerException("inputStream can not be null");
		}
		this.endOfLine = endOfLine;
		this.in = in;
		getNextLine();
	}
	private void getNextLine() throws IOException{
		if(doneFile){
			return;
		}
		StringBuilder builder = new StringBuilder();
		
		int value;
		value = in.read();
		while(true){	
			if(value == -1){
				doneFile =true;
				close();
				break;
			}
			
			builder.append((char)value);
			if(value == endOfLine){
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
	public boolean hasNextLine(){
		Object next = nextQueue.peek();
		
		return next!= endOfFile;
	}
	public String nextLine() throws IOException{
		Object next= nextQueue.poll();
		if(next == endOfFile){
			return null;
		}
		getNextLine();
		return (String)next;
	}
	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		IOUtil.closeAndIgnoreErrors(in);
		nextQueue.clear();
		
	}
	
}
