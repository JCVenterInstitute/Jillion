/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.DecodingOptions;
import org.jcvi.jillion.core.residue.InvalidCharacterHandlers;
import org.junit.Test;

import static org.junit.Assert.*;

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
	public void decoderIgnoreInvalid(){
		String expected = "IK0TW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder()
					.setDecodingOptions(DecodingOptions.builder().invalidCharacterHandler(InvalidCharacterHandlers.IGNORE).build())
				.append(expected);
		assertEquals("IKTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
	}

	@Test
	public void invalidCharHandlerIgnoreInvalid(){
		String expected = "IK0TW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder()
				.setInvalidCharacterHandler(InvalidCharacterHandlers.IGNORE)
				.append(expected);
		assertEquals("IKTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
	}

	@Test
	public void decoderConstructorIgnoreInvalid(){
		String expected = "IK0TW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder(InvalidCharacterHandlers.IGNORE)
				.append(expected);
		assertEquals("IKTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
	}
	@Test
	public void decoderConstructorUnknownInvalid(){
		String expected = "IK0TW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder(InvalidCharacterHandlers.REPLACE_WITH_UNKNOWN)
				.append(expected);
		assertEquals("IKXTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(5L, sut.getLength());
	}

	@Test
	public void invalidCharHandlerConstructorIgnoreInvalid(){
		String expected = "IK0TW";
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder(InvalidCharacterHandlers.IGNORE)
				.append(expected);
		assertEquals("IKTW", AminoAcidUtil.asString(sut.build()));
		assertEquals(0, sut.getNumGaps());
		assertEquals(4L, sut.getLength());
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
	public void replaceWithGaps(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replaceWithGaps(Range.of(2,3));
		assertEquals("IK--W", sut.toString());
	}
	@Test
	public void replaceWithGapsWithSameLength(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replaceWithGaps(Range.of(2,3), 2);
		assertEquals("IK--W", sut.toString());
	}
	@Test
	public void replaceWithGapsWithMoreLength(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replaceWithGaps(Range.of(2,3), 3);
		assertEquals("IK---W", sut.toString());
	}
	@Test
	public void replaceWithGapsWithLessLength(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replaceWithGaps(Range.of(2,3), 1);
		assertEquals("IK-W", sut.toString());
	}
	@Test
	public void replaceWithGapsWithZeroLength(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		sut.replaceWithGaps(Range.of(2,3), 0);
		assertEquals("IKW", sut.toString());
	}

	@Test
	public void gapDeleteAndAddSingle(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("------L----");
		sut.replace(6, AminoAcid.Gap);
		assertEquals(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10) ,sut.getGapOffsets());
	}
	@Test
	public void gapDeleteAndAddSeveral(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("------L----");
		sut.replace(Range.of(5,7), new AminoAcid[]{AminoAcid.Gap, AminoAcid.Gap, AminoAcid.Gap});

		assertEquals(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10) ,sut.getGapOffsets());
	}

	@Test
	public void replaceWithGapsWithNegLengthShouldThrowException(){
		ProteinSequenceBuilder sut = new ProteinSequenceBuilder("IKFTW");
		assertThrows(IllegalArgumentException.class, ()->sut.replaceWithGaps(Range.of(2,3), -1));

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
		
		assertEquals("IKFTW", sut.toString());
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
	public void copyIncludesGapsNoGaps() {
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFTW").copy();
		assertEquals(0, builder1.getNumGaps());
		assertEquals(5, builder1.getLength());
		assertEquals(5, builder1.getUngappedLength());
	}
	@Test
	public void copyIncludesGapsWithGaps() {
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFT-W").copy();
		assertEquals(1, builder1.getNumGaps());
		assertEquals(6, builder1.getLength());
		assertEquals(5, builder1.getUngappedLength());
	}
	
	@Test
	public void copyTrimIncludesGapsNoGaps() {
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFTW").copy(Range.of(2, 3));
		assertEquals(0, builder1.getNumGaps());
		assertEquals(2, builder1.getLength());
		assertEquals(2, builder1.getUngappedLength());
	}
	@Test
	public void copyTrimIncludesGapsWithGaps() {
		ProteinSequenceBuilder builder1 =new ProteinSequenceBuilder("IKFT-W").copy(Range.of(2, 4));
		assertEquals(1, builder1.getNumGaps());
		assertEquals(3, builder1.getLength());
		assertEquals(2, builder1.getUngappedLength());
	}

	@Test
	public void toBuilderRange() {
		Range r = Range.of(2, 3);
		ProteinSequenceBuilder builder0 = new ProteinSequenceBuilder("IKFTW");
		ProteinSequenceBuilder builder1 = builder0.copy(r);
		ProteinSequenceBuilder builder2 = builder0.build().toBuilder(r);

		assertEquals(builder1.toString(), builder2.toString());
		assertEquals("FT", builder1.toString());
	}
	@Test
	public void toBuilderMultipleRanges() {

		ProteinSequenceBuilder builder0 = new ProteinSequenceBuilder("IKFTWX");
		ProteinSequenceBuilder builder1 = builder0.build().toBuilder(List.of(Range.of(2,3), Range.of(4,5)));

		assertEquals("FTWX", builder1.toString());
	}
	@Test
	public void toBuilderMultipleOverlappingRanges() {

		ProteinSequenceBuilder builder0 = new ProteinSequenceBuilder("IKFTWX");
		ProteinSequenceBuilder builder1 = builder0.build().toBuilder(List.of(Range.of(2,3), Range.of(3,5)));

		assertEquals("FTTWX", builder1.toString());
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
	
	@Test
	public void insertMultipleViaString(){
	    ProteinSequenceBuilder builder =new ProteinSequenceBuilder("IKFTW");
	    builder.insert(2, "AAAA");
	    
	    assertEquals("IKAAAAFTW", builder.build().toString());
	}
	
	@Test
	public void cleanSeq(){
		assertEquals("IKFTW", AminoAcid.cleanSequence("IKFTW", "N"));


		assertEquals("   IKF  TW   ", AminoAcid.cleanSequence("   IKF  TW   ", "N"));

		assertEquals("\tIKF\tTW   ", AminoAcid.cleanSequence("\tIKF\tTW   ", "N"));
		assertEquals("\tIKF\tTW   ", AminoAcid.cleanSequence("\tIKF3\tTW   "));
		assertEquals("\tIKFX\tTW   ", AminoAcid.cleanSequence("\tIKF3\tTW   ", "X"));

		assertEquals("ikftw", AminoAcid.cleanSequence("ikftw", "N"));


		assertEquals("   ikf  TW   ", AminoAcid.cleanSequence("   ikf  TW   ", "N"));

		assertEquals("\tIKF\ttw   ", AminoAcid.cleanSequence("\tIKF\ttw   ", "N"));
		assertEquals("\tIKF\ttw   ", AminoAcid.cleanSequence("\tIKF3\ttw   "));
		assertEquals("\tIKFX\tTW   ", AminoAcid.cleanSequence("\tIKF3\tTW   ", "X"));


		assertEquals("\tIKF\ttw   ", AminoAcid.cleanSequence("\tIKF33\ttw   "));
		assertEquals("\tIKFXX\tTW   ", AminoAcid.cleanSequence("\tIKF33\tTW   ", "X"));

	}
}
