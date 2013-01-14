package org.jcvi.jillion.fasta;

import org.jcvi.jillion.fasta.FastaUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFastaUtil_parseDefLine {

	@Test
	public void deflineOnlyHasId(){
		String defline = ">myId\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertNull(FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void commentIsOnlyWhitespaceHasIdShouldBeConsideredNull(){
		String defline = ">myId\t\t\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertNull(FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void deflineWithMultiWordComment(){
		String defline = ">myId\tmy comment\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertEquals("my comment",FastaUtil.parseCommentFromDefLine(defline));
	}
	@Test
	public void deflineWithSingleWordComment(){
		String defline = ">myId\tcomment\n";
		assertEquals("myId",FastaUtil.parseIdFromDefLine(defline));
		assertEquals("comment",FastaUtil.parseCommentFromDefLine(defline));
	}
}
