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
package org.jcvi.jillion.core.residue.aa;

import java.util.Arrays;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion.core.residue.aa.AminoAcids;
import org.jcvi.jillion.core.residue.aa.UngappedAminoAcidSequence;
import org.junit.Test;
import static org.junit.Assert.*;
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
	public void reverse(){
		AminoAcidSequenceBuilder sut = new AminoAcidSequenceBuilder("IKFTW");
		sut.reverse();
		assertEquals("WTFKI", AminoAcids.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
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
