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

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestIOUtil_createTempDir {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void makeTempDirNoSuffixMakesNameEndingWithDotTmp() throws IOException{
		File sut =IOUtil.createTempDir("prefix", null, tmpDir.getRoot());
		
		assertTrue(sut.exists());
		assertTrue(sut.isDirectory());
		assertThat(sut.getName(), startsWith("prefix"));
		assertThat(sut.getName(), endsWith(".tmp"));
	}
	
	@Test
	public void makeTempDirWithSuffix() throws IOException{
		File sut =IOUtil.createTempDir("prefix", "my.suffix", tmpDir.getRoot());
		
		assertTrue(sut.exists());
		assertTrue(sut.isDirectory());
		assertThat(sut.getName(), startsWith("prefix"));
		assertThat(sut.getName(), endsWith("my.suffix"));
	}
}
