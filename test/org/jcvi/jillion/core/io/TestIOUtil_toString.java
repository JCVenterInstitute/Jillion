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
