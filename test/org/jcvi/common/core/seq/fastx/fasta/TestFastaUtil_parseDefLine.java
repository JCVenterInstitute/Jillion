package org.jcvi.common.core.seq.fastx.fasta;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestFastaUtil_parseDefLine {

	@Test
	public void deflineOnlyHasId(){
		String defline = ">myId\n";
		assertEquals("myId",FastaUtil.parseIdentifierFromIdLine(defline));
		assertNull(FastaUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void commentIsOnlyWhitespaceHasIdShouldBeConsideredNull(){
		String defline = ">myId\t\t\n";
		assertEquals("myId",FastaUtil.parseIdentifierFromIdLine(defline));
		assertNull(FastaUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void deflineWithMultiWordComment(){
		String defline = ">myId\tmy comment\n";
		assertEquals("myId",FastaUtil.parseIdentifierFromIdLine(defline));
		assertEquals("my comment",FastaUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void deflineWithSingleWordComment(){
		String defline = ">myId\tcomment\n";
		assertEquals("myId",FastaUtil.parseIdentifierFromIdLine(defline));
		assertEquals("comment",FastaUtil.parseCommentFromIdLine(defline));
	}
}
