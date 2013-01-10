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

package org.jcvi.common.core.symbol.residue.aa;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.jillion.core.Range;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestAminoAcidSequence {
	private final List<AminoAcid> aminoAcids = AminoAcids.parse("ILKMFDEX");
	AminoAcidSequence sut;
	@Before
	public void setup(){
		sut = encode(aminoAcids);
	}
	protected abstract AminoAcidSequence encode(List<AminoAcid> aminoAcids);
	
	@Test
	public void length(){
		assertEquals(aminoAcids.size(), sut.getLength());
	}
	
	@Test
	public void decode(){
		for(int i=0; i<aminoAcids.size(); i++){
			assertEquals(aminoAcids.get(i),sut.get(i));
		}
	}
	
	@Test
	public void singleBase(){
		List<AminoAcid> expected = AminoAcids.parse("L");
		AminoAcidSequence seq = encode(expected);
		assertEquals(1, seq.getLength());
		assertEquals(expected.get(0),seq.get(0));
	}
	
	@Test
	public void get(){
		for(int i=0; i< aminoAcids.size(); i++){
			assertEquals(aminoAcids.get(i), sut.get(i));
		}
	}
	
	@Test
	public void gappedSequence(){
		AminoAcidSequence seq = encode(AminoAcids.parse("I-LKM-FDEX"));
		assertEquals("I-LKM-FDEX", AminoAcids.asString(seq));
		assertEquals(2, seq.getNumberOfGaps());
		assertEquals(8, seq.getUngappedLength());
		assertEquals(1, seq.getUngappedOffsetFor(2));
	}
}
