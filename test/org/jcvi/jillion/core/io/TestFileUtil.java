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
package org.jcvi.jillion.core.io;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFileUtil {
	File root = new File("tests/org/jcvi/io");
	File higherFile = new File("tests/org/jcvi");
	File lowerFile = new File("tests/org/jcvi/io/fileServer/files/README.txt");
	File siblingFile = new File("tests/org/jcvi/fasta");
	
	@Test
	public void getExtensionFromFile(){
		assertEquals("txt", FileUtil.getExtension(lowerFile));
	}
	@Test
	public void getExtensionFromString(){
		assertEquals("txt", FileUtil.getExtension("README.txt"));
	}
	@Test
	public void getExtensionFromStringFullPath(){
		assertEquals("txt", FileUtil.getExtension("tests/org/jcvi/io/fileServer/files/README.txt"));
	}
	@Test
	public void getExtensionFromStringWithNoExtensionShouldReturnEmptyString(){
		assertTrue(FileUtil.getExtension("filename").isEmpty());
	}
	@Test
	public void getExtensionFromFileWithNoExtensionShouldReturnEmptyString(){
		assertTrue(FileUtil.getExtension(root).isEmpty());
	}
	
	@Test
	public void getBaseNameFromFile(){
		assertEquals("README", FileUtil.getBaseName(lowerFile));
	}
	@Test
	public void getBaseNameFromString(){
		assertEquals("README", FileUtil.getBaseName("README.txt"));
	}
	@Test
	public void getBaseNameFromStringFullPath(){
		assertEquals("README", FileUtil.getBaseName("tests/org/jcvi/io/fileServer/files/README.txt"));
	}
	@Test
	public void getBaseNameFromStringWithNoExtension(){
		assertEquals("filename",FileUtil.getBaseName("filename"));
	}
	@Test
	public void getBaseNameFromFileWithNoExtensionShouldReturnEmptyString(){
		assertEquals("io",FileUtil.getBaseName(root));
	}
	@Test
	public void relativePathSameFileShouldReturnEmptyString() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, root, '/');
		assertEquals("", actual);
	}
	@Test
	public void relativePathSiblingFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, siblingFile, '/');
		assertEquals("../fasta", actual);
	}
	
	@Test
	public void relativePathToHigherFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, higherFile, '/');
		assertEquals("..", actual);
	}
	
	@Test
	public void relativePathToLowerFile() throws IOException{
		String actual =FileUtil.createRelavitePathFrom(root, lowerFile, '/');
		assertEquals("fileServer/files/README.txt", actual);
	}
}
