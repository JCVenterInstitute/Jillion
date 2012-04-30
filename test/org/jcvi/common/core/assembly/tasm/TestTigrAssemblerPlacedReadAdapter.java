/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.tasm;

import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.DefaultPlacedRead;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerPlacedReadAdapter;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerReadAttribute;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.jcvi.common.core.symbol.residue.nt.ReferenceEncodedNucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrAssemblerPlacedReadAdapter {

	 
	
	Range validRange = Range.create(CoordinateSystem.RESIDUE_BASED,5, 13);
	String id = "readId";
	int offset = 5;
	String readSequence = "ACGT-ACGT";
	int ungappedLength = 500;
	NucleotideSequence consensus = new NucleotideSequenceBuilder("NNNNNACGT-ACGT").build();
	ReferenceEncodedNucleotideSequence gappedBasecalls = new NucleotideSequenceBuilder(readSequence)
														.setReferenceHint(consensus, 5)
														.buildReferenceEncodedNucleotideSequence();

	Read<ReferenceEncodedNucleotideSequence> read = new DefaultRead<ReferenceEncodedNucleotideSequence>(id, gappedBasecalls);
	
	
	@Test(expected = NullPointerException.class)
	public void nullPlacedReadShouldThrowNullPointerException(){
		new TigrAssemblerPlacedReadAdapter(null);
	}
	
	@Test
	public void adaptedReadShouldDelegateAllPlacedReadMethods(){
		PlacedRead delegate = DefaultPlacedRead.createBuilder(consensus, id, readSequence, offset, 
		        Direction.FORWARD,validRange, ungappedLength)
		        .build();
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.FORWARD, sut.getDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getBegin()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	@Test
	public void reverseReadShouldHaveSwappedSeqLeftandSeqRightAttributes(){
	    PlacedRead delegate = DefaultPlacedRead.createBuilder(consensus, id, readSequence, offset, 
                Direction.REVERSE,validRange, ungappedLength)
                .build();
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getBegin()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	
	private void assertCommonGettersCorrect(TigrAssemblerPlacedReadAdapter sut) {
		assertEquals(id, sut.getId());
		
		assertEquals(gappedBasecalls, sut.getNucleotideSequence());
		assertEquals(offset, sut.getGappedContigStart());
		assertEquals(offset+gappedBasecalls.getLength()-1, sut.getGappedContigEnd());
		assertEquals(gappedBasecalls.getLength(),sut.getGappedLength());
		assertTrue(sut.getNucleotideSequence().getDifferenceMap().isEmpty());
	}
	private void assertCommonAttributesCorrect(PlacedRead delegate,
			TigrAssemblerPlacedReadAdapter sut) {
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.BEST));
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.COMMENT));
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.DB));
		
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.NAME),delegate.getId());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_START_OFFSET),""+delegate.getGappedContigStart());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_LEFT),""+(delegate.getGappedContigStart()));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_RIGHT),""+(delegate.getGappedContigEnd()));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.GAPPED_SEQUENCE),
				Nucleotides.asString(gappedBasecalls.asList()));
		
		for(Entry<TigrAssemblerReadAttribute, String> entry : sut.getAttributes().entrySet()){
			assertEquals(entry.getValue(), sut.getAttributeValue(entry.getKey()));
		}
	}
}
