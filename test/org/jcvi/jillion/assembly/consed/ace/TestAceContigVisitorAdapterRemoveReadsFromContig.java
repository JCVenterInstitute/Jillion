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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigBuilderVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceContigReadVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitorAdapter;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.assembly.consed.ace.AceParser;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestAceContigVisitorAdapterRemoveReadsFromContig {

	private final Date phdDate = new Date(123456789); 
	
	@Test
	public void removeSingleReadFromContig() throws IOException{
		final AceContig contig = new AceContigBuilder("contig1", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(), 0, Direction.FORWARD, Range.of(10,17), new PhdInfo("read1", "read1.phd", phdDate), 25)
							.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(), 0, Direction.FORWARD, Range.of(10,17), new PhdInfo("read2", "read2.phd", phdDate), 25)
							.addRead("read3", new NucleotideSequenceBuilder("ACGT").build(), 4, Direction.FORWARD, Range.of(14,17), new PhdInfo("read3", "read3.phd", phdDate), 25)
							
							.build();
		
		AceParser handler = AceTestUtil.createAceHandlerFor(contig);
		final AtomicBoolean visitedContig = new AtomicBoolean(false);
		handler.parse(new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				AceContigVisitor delegate = new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
					
					@Override
					protected void visitContig(AceContigBuilder builder) {
						AceContig actual = builder.build();
						AceContig expected = new AceContigBuilder(contig)
													.removeRead("read2")
													.build();
						
						assertEquals(actual, expected);
						visitedContig.set(true);
					}
				};
				
				return new AceContigVisitorAdapter(delegate){

					@Override
					public AceContigReadVisitor visitBeginRead(String readId,
							int gappedLength) {
						if(readId.equals("read2")){
							//skip read2
							return null;
						}
						return getDelegate().visitBeginRead(readId, gappedLength);
					}
					
				};
			}
			
		});
		
		assertTrue(visitedContig.get());
	}
}
