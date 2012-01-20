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

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestAminoAcidEncodedGlyphs {
	private final List<AminoAcid> aminoAcids = AminoAcid.getGlyphsFor("ILKMFDEX");
	Sequence<AminoAcid> sut;
	@Before
	public void setup(){
		sut = encode(aminoAcids);
	}
	protected abstract AminoAcidSequence encode(List<AminoAcid> aminoAcids);
	@Test
	public void decode(){
		assertEquals(aminoAcids,sut.asList());
	}
	@Test
	public void length(){
		assertEquals(aminoAcids.size(), sut.getLength());
	}
	
	@Test
	public void decodeWithRangeShouldOnlyDecodeSubrange(){
		Range range = Range.buildRange(2, 5);
		List<AminoAcid> expected = aminoAcids.subList(2, 6);
		assertEquals(expected, sut.asList(range));
	}
	
	@Test
	public void singleBase(){
		List<AminoAcid> expected = AminoAcid.getGlyphsFor("L");
		AminoAcidSequence seq = encode(expected);
		assertEquals(expected, seq.asList());
	}
	
	@Test
	public void get(){
		for(int i=0; i< aminoAcids.size(); i++){
			assertEquals(aminoAcids.get(i), sut.get(i));
		}
	}
}
