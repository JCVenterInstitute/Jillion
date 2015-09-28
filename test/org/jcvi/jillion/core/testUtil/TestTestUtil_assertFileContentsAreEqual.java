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
package org.jcvi.jillion.core.testUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestTestUtil_assertFileContentsAreEqual {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	@Test
	public void twoEmptyFilesAreEqual() throws IOException{
		File tmp1 = tmpDir.newFile();
		File tmp2 = tmpDir.newFile();
		TestUtil.assertContentsAreEqual(tmp1, tmp2);
	}
	
	@Test
	public void sameFileIsEqualToItself() throws IOException{
		File tmp = tmpDir.newFile();
		PrintWriter writer = new PrintWriter(tmp);
		writer.println("this is a test...");
		writer.close();
		TestUtil.assertContentsAreEqual(tmp, tmp);
	}
	
	@Test
	public void copyOfFileIsEqualToItself() throws IOException{
		File tmp = tmpDir.newFile();
		PrintWriter writer = new PrintWriter(tmp);
		writer.println("this is a test...");
		writer.close();
		
		File copy = tmpDir.newFile();
		OutputStream out=null;
		InputStream in=null;
		try{
		 out = new FileOutputStream(copy);
		 in = new FileInputStream(tmp);
		 IOUtil.copy(in, out);
		}finally{
			IOUtil.closeAndIgnoreErrors(in, out);
		}
		TestUtil.assertContentsAreEqual(tmp, tmp);
	}
}
