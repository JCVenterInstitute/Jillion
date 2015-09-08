/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
public class TestProteinSequenceBuilder {

	@Test
	public void emptyConstructorShouldStartHaveNoResidues(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder();
		assertEquals(0, sut.getNumGaps());
		assertEquals(0L, sut.getLength());
	}
	
	@Test
	public void stringConstructorShouldStartWithResiduesGiven(){
		String expected = "IKFTW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder(expected);
		assertEquals(expected, AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void testToStringShouldPrintSequence(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		assertEquals("IKFTW", sut.toString());
	}
	
	@Test
	public void get(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		assertEquals(AminoAcid.Isoleucine, sut.get(0));
		assertEquals(AminoAcid.Tryptophan, sut.get(4));
	}
	
	@Test
	public void replace(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replace(2, AminoAcid.Tryptophan);
		assertEquals("IKWTW", sut.toString());
	}
	
	@Test
	public void insertAminoAcid(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.insert(2, AminoAcid.Isoleucine);
		assertEquals("IKIFTW", AminoAcidUtil.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	
	@Test
	public void insertIterable(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.insert(2, Arrays.asList(AminoAcid.Isoleucine));
		assertEquals("IKIFTW", AminoAcidUtil.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void insertString(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.insert(2, "I");
		assertEquals("IKIFTW", AminoAcidUtil.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void insertOtherBuilder(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.insert(2, new ProteinSequenceBuilder("I"));
		assertEquals("IKIFTW", AminoAcidUtil.asString(sut));
		assertEquals(0, sut.getNumGaps());
		assertEquals(6L, sut.getLength());
	}
	@Test
	public void appendSingleAminoAcid(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder();
		sut.append(AminoAcid.Isoleucine);
		assertEquals("I", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	
	
	@Test
	public void appendSingleAminoAcidAsString(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder();
		sut.append("I");
		assertEquals("I", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(1L, sut.getLength());
	}
	
	@Test
	public void appendMultipleAminoAcidsAsString(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder();
		sut.append("IKFTW");
		assertEquals("IKFTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void appendOtherBuilder(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.append(new ProteinSequenceBuilder("NDE"));
		assertEquals("IKFTWNDE", sut.toString());
		assertEquals(0, sut.getNumGaps());
		assertEquals(8, sut.getLength());
	}
	@Test
	public void clear(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.clear();
		assertEquals(0, sut.getLength());
		assertEquals(0, sut.getNumGaps());
	}
	
	@Test
	public void clearAndAppend(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW")
											.clear()
											.append("WT-FKI");
		assertEquals("WT-FKI", AminoAcidUtil.asString(sut.build()));
		assertEquals(6, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertEquals(1, sut.getNumGaps());
	}
	@Test
	public void reverse(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.reverse();
		assertEquals("WTFKI", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}
	
	@Test
	public void trim(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.trim(Range.of(1,3));
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(3L, sut.getLength());
		assertEquals("KFT", AminoAcidUtil.asString(sut.build()));
	}
	
	@Test
	public void trimBeyondEdge(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.trim(Range.of(1,10));
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
		assertEquals("KFTW", AminoAcidUtil.asString(sut.build()));
	}
	@Test
	public void trimBeforeStart(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.trim(Range.of(-1,3));
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
		assertEquals("IKFT", AminoAcidUtil.asString(sut.build()));
	}
	
	@Test
	public void trimEmptyRangeShouldRemoveEntireSequence(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.trim(new Range.Builder(0).build());
		
		assertEquals(0, sut.getNumGaps());
		assertEquals(0L, sut.getLength());
		assertEquals("", AminoAcidUtil.asString(sut.build()));
	}
	@Test
	public void multipleAppendsString(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.append("IHH");
		sut.append("F");
		assertEquals("IKFTWIHHF", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(9L, sut.getLength());
	}
	@Test
	public void multipleAppendsAminoAcids(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.append(AminoAcid.Glutamic_Acid);
		sut.append(AminoAcid.Methionine);
		assertEquals("IKFTWEM", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(7L, sut.getLength());
	}
	@Test
	public void noGapsInSequence(){
		ProteinSequence sut = new ProteinSequenceBuilder("IKFTW")
								.build();
		assertTrue((sut instanceof UngappedProteinSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void ungapWhenNoGapsExistShouldDoNothing(){
		ProteinSequence sut = new ProteinSequenceBuilder("IKFTW")
								.ungap()
								.build();
		assertTrue((sut instanceof UngappedProteinSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void ungap(){
		ProteinSequence sut = new ProteinSequenceBuilder("IKF-TW")
									.ungap()
									.build();
		assertTrue((sut instanceof UngappedProteinSequence));
		assertEquals(0, sut.getNumberOfGaps());
		assertEquals(5, sut.getLength());
		assertEquals(5, sut.getUngappedLength());
		assertTrue(sut.getGapOffsets().isEmpty());
		assertEquals(4, sut.getGappedOffsetFor(4));
		assertEquals(4, sut.getUngappedOffsetFor(4));
	}
	@Test
	public void gapsInSequence(){
		ProteinSequence sut = new ProteinSequenceBuilder("IKF-TW")
								.build();
		assertFalse((sut instanceof UngappedProteinSequence));
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
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFTW");
		ProteinSequenceBuilder builder2 = builder1.copy();
		
		builder1.append("TW");
		assertEquals("IKFTWTW", AminoAcidUtil.asString(builder1.build()));
		assertEquals("IKFTW", AminoAcidUtil.asString(builder2.build()));
	}
	@Test
	public void ModificationsToCopyDoNotAffectOriginal(){
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFTW");
		ProteinSequenceBuilder builder2 = builder1.copy();
		
		builder2.append("TW");
		assertEquals("IKFTWTW", AminoAcidUtil.asString(builder2.build()));
		assertEquals("IKFTW", AminoAcidUtil.asString(builder1.build()));
	}
	
	@Test
	public void parsingStringShouldRemoveWhitespace(){
		ProteinSequence seq = new ProteinSequenceBuilder("IKF TW\nMKAIL")
								.append("SED DEH\n")
								.build();
		assertEquals("IKFTWMKAILSEDDEH", AminoAcidUtil.asString(seq));
	}
	
	
}
