package org.jcvi.jillion.assembly.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
public class TestConsedUtilGetConsedDir {

	@Test
	public void getConsedDir(){
		File consedDir = new File("consed");
		File editDir = new File(consedDir, "edit_dir");
		File ace = new File(editDir, "foo.ace.1");
		
		assertEquals(consedDir, ConsedUtil.getConsedDirFor(ace));
	}
}
