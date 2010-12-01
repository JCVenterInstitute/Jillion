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

package org.jcvi.glyph.aa;

import java.util.List;

import org.jcvi.Range;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAminoAcidEncodedGlyphs {

	private final List<AminoAcid> aminoAcids = AminoAcid.getGlyphsFor("ILKMFTWV");
	DefaultAminoAcidEncodedGlyphs sut = new DefaultAminoAcidEncodedGlyphs(
			aminoAcids);
	@Test
	public void decode(){
		assertEquals(aminoAcids,sut.decode());
	}
	@Test
	public void length(){
		assertEquals(aminoAcids.size(), sut.getLength());
	}
	
	@Test
	public void decodeWithRangeShouldOnlyDecodeSubrange(){
		Range range = Range.buildRange(2, 5);
		List<AminoAcid> expected = aminoAcids.subList(2, 6);
		assertEquals(expected, sut.decode(range));
	}
	@Test
	public void get(){
		for(int i=0; i< aminoAcids.size(); i++){
			assertEquals(aminoAcids.get(i), sut.get(i));
		}
	}
}
