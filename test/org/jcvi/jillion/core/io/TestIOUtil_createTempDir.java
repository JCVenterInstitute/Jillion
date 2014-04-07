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
