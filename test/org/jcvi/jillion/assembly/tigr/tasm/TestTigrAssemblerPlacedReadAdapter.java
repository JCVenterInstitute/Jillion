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
package org.jcvi.jillion.assembly.tigr.tasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.internal.assembly.DefaultAssembledRead;
import org.junit.Test;
public class TestTigrAssemblerPlacedReadAdapter {

	 
	
	Range validRange = Range.of(CoordinateSystem.RESIDUE_BASED,5, 13);
	String id = "readId";
	int offset = 5;
	String readSequence = "ACGT-ACGT";
	int ungappedLength = 500;
	NucleotideSequence consensus = new NucleotideSequenceBuilder("NNNNNACGT-ACGT").build();
	ReferenceMappedNucleotideSequence gappedBasecalls = new NucleotideSequenceBuilder(readSequence)
														.setReferenceHint(consensus, 5)
														.buildReferenceEncodedNucleotideSequence();

	
	
	@Test(expected = NullPointerException.class)
	public void nullPlacedReadShouldThrowNullPointerException(){
		new TasmAssembledReadAdapter(null);
	}
	
	@Test
	public void adaptedReadShouldDelegateAllPlacedReadMethods(){
		AssembledRead delegate = DefaultAssembledRead.createBuilder(id, readSequence, offset, Direction.FORWARD, 
		        validRange,ungappedLength)
		        .build(consensus);
		TasmAssembledReadAdapter sut = new TasmAssembledReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		

		assertEquals(Direction.FORWARD, sut.getDirection());
			
	}
	@Test
	public void reverseReadShouldHaveSwappedSeqLeftandSeqRightAttributes(){
	    AssembledRead delegate = DefaultAssembledRead.createBuilder(id, readSequence, offset, Direction.REVERSE, 
                validRange,ungappedLength)
                .build(consensus);
		TasmAssembledReadAdapter sut = new TasmAssembledReadAdapter(delegate);
	
		assertEquals(Direction.REVERSE, sut.getDirection());
		
	}
	
	private void assertCommonGettersCorrect(TasmAssembledReadAdapter sut) {
		assertEquals(id, sut.getId());
		
		assertEquals(gappedBasecalls, sut.getNucleotideSequence());
		assertEquals(offset, sut.getGappedStartOffset());
		assertEquals(offset+gappedBasecalls.getLength()-1, sut.getGappedEndOffset());
		assertEquals(gappedBasecalls.getLength(),sut.getGappedLength());
		assertTrue(sut.getNucleotideSequence().getDifferenceMap().isEmpty());
	}

}
