package org.jcvi.jillion.assembly.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class TestConsedUtilGetAcePrefix {

	@Test
	public void versionedAce(){
		assertEquals("prefix", ConsedUtil.getAcePrefixFor(new File("prefix.ace.1")));
	}
	
	@Test
	public void unversionedAce(){
		assertEquals("prefix", ConsedUtil.getAcePrefixFor(new File("prefix.ace")));
	}
	@Test
	public void versionedAceWithDotPrefix(){
		assertEquals("foo.prefix", ConsedUtil.getAcePrefixFor(new File("foo.prefix.ace.1")));
	}
	
	@Test
	public void unversionedAceWithDotPrefix(){
		assertEquals("foo.prefix", ConsedUtil.getAcePrefixFor(new File("foo.prefix.ace")));
	}
}
