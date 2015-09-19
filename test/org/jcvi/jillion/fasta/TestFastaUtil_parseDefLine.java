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
