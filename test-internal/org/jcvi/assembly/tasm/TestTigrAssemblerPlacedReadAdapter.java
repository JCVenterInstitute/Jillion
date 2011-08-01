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

package org.jcvi.assembly.tasm;

import java.util.Map.Entry;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.contig.DefaultPlacedRead;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrAssemblerPlacedReadAdapter {

	 
	
	Range validRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,5, 13);
	String id = "readId";
	int offset = 1234;
	String readSequence = "ACGT-ACGT";
	ReferenceEncodedNucleotideSequence gappedBasecalls = new DefaultReferenceEncodedNucleotideSequence(
			new DefaultNucleotideSequence("NNNNNACGT-ACGT"),readSequence,5,validRange);
	Read<ReferenceEncodedNucleotideSequence> read = new DefaultRead<ReferenceEncodedNucleotideSequence>(id, gappedBasecalls);
	
	
	@Test(expected = NullPointerException.class)
	public void nullPlacedReadShouldThrowNullPointerException(){
		new TigrAssemblerPlacedReadAdapter(null);
	}
	
	@Test
	public void adaptedReadShouldDelegateAllPlacedReadMethods(){
		PlacedRead delegate = new DefaultPlacedRead(read, offset, Direction.FORWARD);
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.FORWARD, sut.getDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getStart()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	@Test
	public void reverseReadShouldHaveSwappedSeqLeftandSeqRightAttributes(){
		PlacedRead delegate = new DefaultPlacedRead(read, offset, Direction.REVERSE);
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(Direction.REVERSE, sut.getDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getStart()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	
	private void assertCommonGettersCorrect(TigrAssemblerPlacedReadAdapter sut) {
		assertEquals(id, sut.getId());
		
		assertEquals(gappedBasecalls, sut.getSequence());
		assertEquals(offset, sut.getStart());
		assertEquals(offset+gappedBasecalls.getLength()-1, sut.getEnd());
		assertEquals(gappedBasecalls.getLength(),sut.getLength());
		assertTrue(sut.getSnps().isEmpty());
	}
	private void assertCommonAttributesCorrect(PlacedRead delegate,
			TigrAssemblerPlacedReadAdapter sut) {
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.BEST));
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.COMMENT));
		assertFalse(sut.hasAttribute(TigrAssemblerReadAttribute.DB));
		
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.NAME),delegate.getId());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_START_OFFSET),""+delegate.getStart());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_LEFT),""+(delegate.getStart()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.CONTIG_RIGHT),""+(delegate.getEnd()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.GAPPED_SEQUENCE),
				Nucleotides.convertToString(gappedBasecalls.decode()));
		
		for(Entry<TigrAssemblerReadAttribute, String> entry : sut.getAttributes().entrySet()){
			assertEquals(entry.getValue(), sut.getAttributeValue(entry.getKey()));
		}
	}
}
