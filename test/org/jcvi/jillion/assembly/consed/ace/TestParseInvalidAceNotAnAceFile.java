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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileParser;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestParseInvalidAceNotAnAceFile {

	@Test(expected = IOException.class)
	public void tryingToParseNonAceFileShouldThrowIOException() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestParseInvalidAceNotAnAceFile.class);
		File nonAce = resources.getFile("files/sample.contig");
		AceFileParser.create(nonAce).parse( new AbstractAceFileVisitor() {

			@Override
			public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
				fail("should not visit header of non-ace file");
			}

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				fail("should not visit contig of non-ace file");
				return null;
			}
		
		});
		
		
	}
	
	
}
