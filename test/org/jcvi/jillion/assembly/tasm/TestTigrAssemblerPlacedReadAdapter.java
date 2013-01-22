/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.DefaultAssembledRead;
import org.jcvi.jillion.assembly.tasm.TasmAssembledReadAdapter;
import org.jcvi.jillion.assembly.tasm.TasmReadAttribute;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
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
		AssembledRead delegate = DefaultAssembledRead.createBuilder(consensus, id, readSequence, offset, 
		        Direction.FORWARD,validRange, ungappedLength)
		        .build();
		TasmAssembledReadAdapter sut = new TasmAssembledReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.FORWARD, sut.getDirection());
		assertEquals(sut.getAttributeValue(TasmReadAttribute.SEQUENCE_LEFT),""+(delegate.getReadInfo().getValidRange().getBegin()+1));
		assertEquals(sut.getAttributeValue(TasmReadAttribute.SEQUENCE_RIGHT),""+(delegate.getReadInfo().getValidRange().getEnd()+1));
		
	}
	@Test
	public void reverseReadShouldHaveSwappedSeqLeftandSeqRightAttributes(){
	    AssembledRead delegate = DefaultAssembledRead.createBuilder(consensus, id, readSequence, offset, 
                Direction.REVERSE,validRange, ungappedLength)
                .build();
		TasmAssembledReadAdapter sut = new TasmAssembledReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(sut.getAttributeValue(TasmReadAttribute.SEQUENCE_RIGHT),""+(delegate.getReadInfo().getValidRange().getBegin()+1));
		assertEquals(sut.getAttributeValue(TasmReadAttribute.SEQUENCE_LEFT),""+(delegate.getReadInfo().getValidRange().getEnd()+1));
		
	}
	
	private void assertCommonGettersCorrect(TasmAssembledReadAdapter sut) {
		assertEquals(id, sut.getId());
		
		assertEquals(gappedBasecalls, sut.getNucleotideSequence());
		assertEquals(offset, sut.getGappedStartOffset());
		assertEquals(offset+gappedBasecalls.getLength()-1, sut.getGappedEndOffset());
		assertEquals(gappedBasecalls.getLength(),sut.getGappedLength());
		assertTrue(sut.getNucleotideSequence().getDifferenceMap().isEmpty());
	}
	private void assertCommonAttributesCorrect(AssembledRead delegate,
			TasmAssembledReadAdapter sut) {
		assertFalse(sut.hasAttribute(TasmReadAttribute.BEST));
		assertFalse(sut.hasAttribute(TasmReadAttribute.COMMENT));
		assertFalse(sut.hasAttribute(TasmReadAttribute.DB));
		
		NucleotideSequence consensus =delegate.getNucleotideSequence().getReferenceSequence();
		
		assertEquals(sut.getAttributeValue(TasmReadAttribute.NAME),delegate.getId());
		long readGappedStartOffset = delegate.getGappedStartOffset();
		assertEquals(sut.getAttributeValue(TasmReadAttribute.CONTIG_START_OFFSET),""+readGappedStartOffset);
		assertEquals(sut.getAttributeValue(TasmReadAttribute.CONTIG_LEFT),""+(consensus.getUngappedOffsetFor((int)readGappedStartOffset) +1));
		assertEquals(sut.getAttributeValue(TasmReadAttribute.CONTIG_RIGHT),""+(consensus.getUngappedOffsetFor((int)delegate.getGappedEndOffset()) +1));
		assertEquals(sut.getAttributeValue(TasmReadAttribute.GAPPED_SEQUENCE),
				gappedBasecalls.toString());
		
		for(Entry<TasmReadAttribute, String> entry : sut.getAttributes().entrySet()){
			assertEquals(entry.getValue(), sut.getAttributeValue(entry.getKey()));
		}
	}
}
