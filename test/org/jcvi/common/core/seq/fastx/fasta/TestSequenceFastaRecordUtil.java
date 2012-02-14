package org.jcvi.common.core.seq.fastx.fasta;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestSequenceFastaRecordUtil {

	@Test
	public void deflineOnlyHasId(){
		String defline = ">myId\n";
		assertEquals("myId",SequenceFastaRecordUtil.parseIdentifierFromIdLine(defline));
		assertNull(SequenceFastaRecordUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void commentIsOnlyWhitespaceHasIdShouldBeConsideredNull(){
		String defline = ">myId\t\t\n";
		assertEquals("myId",SequenceFastaRecordUtil.parseIdentifierFromIdLine(defline));
		assertNull(SequenceFastaRecordUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void deflineWithMultiWordComment(){
		String defline = ">myId\tmy comment\n";
		assertEquals("myId",SequenceFastaRecordUtil.parseIdentifierFromIdLine(defline));
		assertEquals("my comment",SequenceFastaRecordUtil.parseCommentFromIdLine(defline));
	}
	@Test
	public void deflineWithSingleWordComment(){
		String defline = ">myId\tcomment\n";
		assertEquals("myId",SequenceFastaRecordUtil.parseIdentifierFromIdLine(defline));
		assertEquals("comment",SequenceFastaRecordUtil.parseCommentFromIdLine(defline));
	}
}
