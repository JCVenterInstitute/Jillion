/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.junit.Test;

public class TestOpenAwareInputStream {

	byte[] bytes = new byte[]{1,2,3,4,123,Byte.MAX_VALUE, Byte.MIN_VALUE, -1,5,10};
	
	@Test(expected = NullPointerException.class)
	public void nullConstructorShouldThrowNPE(){
		new OpenAwareInputStream(null);
	}
	
	@Test
	public void readingUntilEOFShouldMakeNotOpen() throws IOException{
		OpenAwareInputStream sut = new OpenAwareInputStream(new ByteArrayInputStream(bytes));
		assertTrue(sut.isOpen());
		try{
			while(true){
				if(sut.read() == -1){
					break;
				}
			}
			assertFalse(sut.isOpen());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	
	@Test
	public void readEntireStreamWithoutCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);

		try{
			while(true){
				int b = sut.read();
				if(b ==-1){
					break;
				}
				out.write(b);
			}
			assertArrayEquals(bytes, out.toByteArray());
			//we've seen eof so is not open
			assertFalse(sut.isOpen());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	@Test
	public void readAndCheckIsOpenEachTime() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		try{
			while(sut.isOpen()){
				int b = sut.read();
				if(b ==-1){
					break;
				}
				out.write(b);
				
			}
			assertArrayEquals(bytes, out.toByteArray());
			//we've seen eof so is not open
			assertFalse(sut.isOpen());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	@Test
	public void readArrayEntireStreamWithoutCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		try{
			while(true){
				byte[] temp = new byte[5];
				
				int bytesRead =sut.read(temp);
				if(bytesRead == -1){
					break;
				}
				out.write(temp, 0, bytesRead);
			}
			assertArrayEquals(bytes, out.toByteArray());
			//we've seen eof so is not open
			assertFalse(sut.isOpen());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	@Test
	public void readArrayEntireStreamInChunksWhileCheckingIsOpen() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream out = new ByteArrayOutputStream(bytes.length);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		try{
			while(sut.isOpen()){
				byte[] temp = new byte[5];
				
				int bytesRead =sut.read(temp);
				if(bytesRead == -1){
					break;
				}
				out.write(temp, 0, bytesRead);
			}
			assertArrayEquals(bytes, out.toByteArray());
			//we've seen eof so is not open
			assertFalse(sut.isOpen());
		}finally{
			IOUtil.closeAndIgnoreErrors(sut);
		}
	}
	
	@Test
	public void closingStreamShouldMakeIsOpenReturnFalse() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		OpenAwareInputStream sut = new OpenAwareInputStream(in);
		
		assertTrue(sut.isOpen());
		sut.close();
		assertFalse(sut.isOpen());
	}
}
