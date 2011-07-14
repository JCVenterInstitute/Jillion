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

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.DefaultPlacedRead;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.nuc.DefaultNucleotideSequence;
import org.jcvi.glyph.nuc.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferenceEncodedNucleotideSequence;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;
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
		PlacedRead delegate = new DefaultPlacedRead(read, offset, SequenceDirection.FORWARD);
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(SequenceDirection.FORWARD, sut.getSequenceDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getStart()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	@Test
	public void reverseReadShouldHaveSwappedSeqLeftandSeqRightAttributes(){
		PlacedRead delegate = new DefaultPlacedRead(read, offset, SequenceDirection.REVERSE);
		TigrAssemblerPlacedReadAdapter sut = new TigrAssemblerPlacedReadAdapter(delegate);
		assertCommonGettersCorrect(sut);		
		assertCommonAttributesCorrect(delegate, sut);
		assertEquals(SequenceDirection.REVERSE, sut.getSequenceDirection());
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_RIGHT),""+(delegate.getValidRange().getStart()+1));
		assertEquals(sut.getAttributeValue(TigrAssemblerReadAttribute.SEQUENCE_LEFT),""+(delegate.getValidRange().getEnd()+1));
		
	}
	
	private void assertCommonGettersCorrect(TigrAssemblerPlacedReadAdapter sut) {
		assertEquals(id, sut.getId());
		
		assertEquals(gappedBasecalls, sut.getEncodedGlyphs());
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
				NucleotideGlyph.convertToString(gappedBasecalls.decode()));
		
		for(Entry<TigrAssemblerReadAttribute, String> entry : sut.getAttributes().entrySet()){
			assertEquals(entry.getValue(), sut.getAttributeValue(entry.getKey()));
		}
	}
}
