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
package org.jcvi.jillion.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_toString {

	@Test
	public void testToString() throws IOException{
		String expected = "this is a test";
		
		InputStream in = new ByteArrayInputStream(expected.getBytes(IOUtil.UTF_8));
		String actual = IOUtil.toString(in);
		assertEquals(expected, actual);
	}
	@Test
	public void testToStringWithEncoding() throws IOException{
		String expected = "this is a test";
		
		InputStream in = new ByteArrayInputStream(expected.getBytes(IOUtil.UTF_8));
		String actual = IOUtil.toString(in, IOUtil.UTF_8_NAME);
		assertEquals(expected, actual);
	}
	@Test
	public void testToStringWithNullEncodingShouldUseDefault() throws IOException{
		String expected = "this is a test";
		
		InputStream in = new ByteArrayInputStream(expected.getBytes(IOUtil.UTF_8));
		String actual = IOUtil.toString(in,null);
		assertEquals(expected, actual);
	}
	@Test(expected = NullPointerException.class)
	public void nullInputStreamShouldThrowNPE() throws IOException{
		IOUtil.toString(null);
	}
	@Test(expected = NullPointerException.class)
	public void withEncodingNullInputStreamShouldThrowNPE() throws IOException{
		IOUtil.toString(null,IOUtil.UTF_8_NAME);
	}
}
