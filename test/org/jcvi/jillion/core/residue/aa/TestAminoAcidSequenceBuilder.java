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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestAminoAcidSequenceBuilder {

	@Test
	public void emptyConstructorShouldStartHaveNoResidues(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		assertEquals(0, sut.getNumGaps());
		assertEquals(0L, sut.getLength());
	}
	
	@Test
	public void stringConstructorShouldStartWithResiduesGiven(){
		String expected = "IKFTW";
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder(expected);
		assertEquals(expected, AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void testToStringShouldPrintSequence(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		assertEquals("IKFTW", sut.toString());
	}
	
	@Test
	public void get(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		assertEquals(AminoAcid.Isoleucine, sut.get(0));
		assertEquals(AminoAcid.Tryptophan, sut.get(4));
	}
	
	@Test
	public void replace(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.replace(2, AminoAcid.Tryptophan);
		assertEquals("IKWTW", sut.toString());
	}
	
	@Test
	public void insertAminoAcid(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.insert(2, AminoAcid.Isoleucine);
		assertEquals("IKIFTW", AminoAcids.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	
	@Test
	public void insertIterable(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.insert(2, Arrays.asList(AminoAcid.Isoleucine));
		assertEquals("IKIFTW", AminoAcids.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void insertString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.insert(2, "I");
		assertEquals("IKIFTW", AminoAcids.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void insertOtherBuilder(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.insert(2, new AminoAcidSequenceBuilder("I"));
		assertEquals("IKIFTW", AminoAcids.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void appendSingleAminoAcid(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append(AminoAcid.Isoleucine);
		assertEquals("I", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	
	
	@Test
	public void appendSingleAminoAcidAsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append("I");
		assertEquals("I", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	
	@Test
	public void appendMultipleAminoAcidsAsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder();
		sut.append("IKFTW");
		assertEquals("IKFTW", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void appendOtherBuilder(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.append(new AminoAcidSequenceBuilder("NDE"));
		assertEquals("IKFTWNDE", sut.toString());
		assertEquals(0, sut.getNumGaps());
		assertEquals(8, sut.getLength());
	}
	@Test
	public void clear(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.clear();
		assertEquals(0, sut.getLength());
		assertEquals(0, sut.getNumGaps());
	}
	
	@Test
	public void clearAndAppend(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW")
											.clear()
											.append("WT-FKI");
		assertEquals("WT-FKI", AminoAcids.asString(sut.build()));
		assertEquals(6, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertEquals(1, sut.getNumGaps());
	}
	@Test
	public void reverse(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.reverse();
		assertEquals("WTFKI", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void trim(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.trim(Range.of(1,3));
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(3L, sut.getLength());
		assertEquals("KFT", AminoAcids.asString(sut.build()));
	}
	@Test
	public void trimEmptyRangeShouldRemoveEntireSequence(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.trim(new Range.Builder(0).build());
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(0L, sut.getLength());
		assertEquals("", AminoAcids.asString(sut.build()));
	}
	@Test
	public void multipleAppendsString(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.append("IHH");
		sut.append("F");
		assertEquals("IKFTWIHHF", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(9L, sut.getLength());
	}
	@Test
	public void multipleAppendsAminoAcids(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.append(AminoAcid.Glutamic_Acid);
		sut.append(AminoAcid.Methionine);
		assertEquals("IKFTWEM", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(7L, sut.getLength());
	}
	@Test
	public void noGapsInSequence(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKFTW")
								.build();
		assertTrue((sut instanceof UngappedAminoAcidSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void ungapWhenNoGapsExistShouldDoNothing(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKFTW")
								.ungap()
								.build();
		assertTrue((sut instanceof UngappedAminoAcidSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void ungap(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKF-TW")
									.ungap()
									.build();
		assertTrue((sut instanceof UngappedAminoAcidSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void gapsInSequence(){
		AminoAcidSequence sut = new AminoAcidSequenceBuilder("IKF-TW")
								.build();
		assertFalse((sut instanceof UngappedAminoAcidSequence));
		assertEquals(1, sut.getNumberOfGaps());
		assertEquals(6, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertEquals(Arrays.asList(3),sut.getGapOffsets());
		
		assertEquals("before gap",2, sut.getGappedOffsetFor(2));
		assertEquals("after gap",4, sut.getGappedOffsetFor(3));
		
		assertEquals("before gap", 2, sut.getUngappedOffsetFor(2));
		assertEquals("on gap", 2, sut.getUngappedOffsetFor(3));
		assertEquals("after gap", 4, sut.getUngappedOffsetFor(5));
	}
	
	@Test
	public void ModificationsToOriginalDoNotAffectCopy(){
		AminoAcidSequenceBuilder builder1 =new AminoAcidSequenceBuilder("IKFTW");
		AminoAcidSequenceBuilder builder2 = builder1.copy();
		
		builder1.append("TW");
		assertEquals("IKFTWTW", AminoAcids.asString(builder1.build()));
		assertEquals("IKFTW", AminoAcids.asString(builder2.build()));
	}
	@Test
	public void ModificationsToCopyDoNotAffectOriginal(){
		AminoAcidSequenceBuilder builder1 =new AminoAcidSequenceBuilder("IKFTW");
		AminoAcidSequenceBuilder builder2 = builder1.copy();
		
		builder2.append("TW");
		assertEquals("IKFTWTW", AminoAcids.asString(builder2.build()));
		assertEquals("IKFTW", AminoAcids.asString(builder1.build()));
	}
	
	@Test
	public void parsingStringShouldRemoveWhitespace(){
		AminoAcidSequence seq = new AminoAcidSequenceBuilder("IKF TW\nMKAIL")
								.append("SED DEH\n")
								.build();
		assertEquals("IKFTWMKAILSEDDEH", AminoAcids.asString(seq));
	}
	
	
}
