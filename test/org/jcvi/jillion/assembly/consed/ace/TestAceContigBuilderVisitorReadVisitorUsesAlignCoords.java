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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.AssemblyTestUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;

public class TestAceContigBuilderVisitorReadVisitorUsesAlignCoords {

	private final Date phdDate = new Date(123456789); 
	
	@Test
	public void useReadAlignCoordsOnly() throws IOException{
		final AceContig expectedContig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(), 0, Direction.FORWARD,
													Range.of(10,17), new PhdInfo("read1", "read1.phd", phdDate), 18)
							.addRead("read2", new NucleotideSequenceBuilder(  "GTAC").build(), 2, Direction.FORWARD,
													Range.of(12,15), new PhdInfo("read2", "read2.phd", phdDate), 16)
							
							.build();
		
		
		
		TestVisitorBuilder visitor = new TestVisitorBuilder("contig", 8, 2);
		
		visitor.visitBasesLine("ACGTACGT");
		visitor.visitAlignedReadInfo("read1", Direction.FORWARD, -9);
		visitor.visitAlignedReadInfo("read2", Direction.FORWARD, -9);
		
		AceContigReadVisitor read1Visitor= visitor.visitBeginRead("read1", 18);
		
		read1Visitor.visitBasesLine("NNNNNNNNNNACGTACGT");
		read1Visitor.visitQualityLine(11, 18, 11, 18);
		read1Visitor.visitTraceDescriptionLine("read1", "read1.phd",phdDate);
		read1Visitor.visitEnd();
		
		AceContigReadVisitor read2Visitor= visitor.visitBeginRead("read2", 16);
		
		read2Visitor.visitBasesLine("NNNNNNNNNNNNGTAC");
		read2Visitor.visitQualityLine(11, 18, 13, 16);
		read2Visitor.visitTraceDescriptionLine("read2", "read2.phd",phdDate);
		read2Visitor.visitEnd();
		
		visitor.visitEnd();
		
		AceContig actual = visitor.getActualContig();
		assertEquals("contig id", expectedContig.getId(), actual.getId());
		assertEquals("consensus", expectedContig.getConsensusSequence(), actual.getConsensusSequence());
		assertEquals("# reads", expectedContig.getNumberOfReads(), actual.getNumberOfReads());
		StreamingIterator<AceAssembledRead> expectedIter = expectedContig.getReadIterator();
		try{
			while(expectedIter.hasNext()){
				AceAssembledRead expectedRead =expectedIter.next();
				AceAssembledRead actualRead = actual.getRead(expectedRead.getId());
				AssemblyTestUtil.assertPlacedReadCorrect(expectedRead, actualRead);
			}
		}finally{
			expectedIter.close();
		}
		assertEquals(expectedContig, visitor.getActualContig());
	}
	
	private static final class TestVisitorBuilder extends AbstractAceContigBuilderVisitor{
		AceContig actualContig=null;
		
		
		public TestVisitorBuilder(String contigId, int consensusLength,
				int numberOfReads) {
			super(contigId, consensusLength, numberOfReads);
		}
		@Override
		protected void visitContig(AceContigBuilder builder) {
			actualContig = builder.build();
		}
		public AceContig getActualContig() {
			return actualContig;
		}
		
		
	};
}
