package org.jcvi.common.core.io;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.io.FileUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFileUtil {
	File root = new File("tests/org/jcvi/io");
	File higherFile = new File("tests/org/jcvi");
	File lowerFile = new File("tests/org/jcvi/io/fileServer/files/README.txt");
	File siblingFile = new File("tests/org/jcvi/fasta");
	
	
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
