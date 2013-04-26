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
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestNucleotideSequenceBuilder {

    @Test(expected=IllegalArgumentException.class)
    public void negativeCapacityShouldThrowException(){
        new NucleotideSequenceBuilder(-1);
    }
    @Test(expected=IllegalArgumentException.class)
    public void zeroCapacityShouldThrowException(){
        new NucleotideSequenceBuilder(0);
    }
    @Test
    public void setInitialCapacity(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder(20)
                                        .append(createSequence("GGTGCA"))
                                        .prepend("ACGT");
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    }
    @Test
    public void nothingBuiltShouldReturnEmptySequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder();
        assertBuiltSequenceEquals("",sut);
    }
    @Test
    public void singleSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder();
        sut.append("ACGT");
        assertBuiltSequenceEquals("ACGT",sut);
    }
    private void assertBuiltSequenceEquals(String expected,NucleotideSequenceBuilder builder){
        assertEquals(expected.length(), builder.getLength());
        assertEquals(expected, builder.build().toString());
    }
    private NucleotideSequence createSequence(String seq){
       return  new NucleotideSequenceBuilder(seq).build();
    }
    @Test
    public void singleSequenceInConstructor(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        assertBuiltSequenceEquals("ACGT",sut);
    } 
    @Test
    public void singleNucleotideSequenceInConstructor(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder(createSequence("ACGT"));
        assertBuiltSequenceEquals("ACGT",sut);
    } 
    @Test
    public void appendString(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append("GGTGCA");
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    } 
    @Test
    public void appendNucleotide(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append(Nucleotide.Guanine);
        assertBuiltSequenceEquals("ACGTG",sut);
    } 
    @Test(expected = NullPointerException.class)
    public void appendNullNucleotideShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append((Nucleotide)null);
    } 
    @Test
    public void appendNucleotideSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append(createSequence("GGTGCA"));
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    } 
    
    @Test
    public void appendContentsOfOtherBuilder(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append(new NucleotideSequenceBuilder("GGTGCA"));
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    } 
    @Test
    public void appendContentsOfOtherBuilderWithGaps(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append(new NucleotideSequenceBuilder("GGTG-CA"));
        assertBuiltSequenceEquals("ACGTGGTG-CA",sut);
        assertEquals(1, sut.getNumGaps());
    } 
    @Test
    public void prepend(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend("GGTGCA");
        assertBuiltSequenceEquals("GGTGCAACGT",sut);
    }
    @Test
    public void prependSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend(createSequence("GGTGCA"));
        assertBuiltSequenceEquals("GGTGCAACGT",sut);
    }
    @Test
    public void prependContentsOfOtherBuilder(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend(new NucleotideSequenceBuilder("G-TGCA"));
        assertBuiltSequenceEquals("G-TGCAACGT",sut);
        assertEquals(1, sut.getNumGaps());
    }
    @Test
    public void insertString(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, "-");
        assertBuiltSequenceEquals("AC-GT",sut);
    }
    @Test
    public void insertNucleotide(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, Nucleotide.Gap);
        assertBuiltSequenceEquals("AC-GT",sut);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void insertNucleotideAtNegativeOffsetShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(-1, Nucleotide.Gap);
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertNucleotideAtBeyondLengthShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(5, Nucleotide.Gap);
    }
    public void insertNucleotideAtLengthShouldAppend(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(5, Nucleotide.Gap);
        assertBuiltSequenceEquals("ACGT-",sut);
    }
    @Test(expected = NullPointerException.class)
    public void insertNullNucleotideShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, (Nucleotide)null);
    }
    @Test(expected = NullPointerException.class)
    public void insertNullNucleotideSequenceShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, (NucleotideSequence)null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertNegativeOffsetShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(-2, "-");
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertOffsetBeyondLengthShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(5000, "-");
    }
    @Test
    public void insertSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, createSequence("-"));
        assertBuiltSequenceEquals("AC-GT",sut);
    }
    @Test
    public void insertMultipleBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, "-N-");
        assertBuiltSequenceEquals("AC-N-GT",sut);
    }
    @Test
    public void insertMultipleBaseSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, createSequence("-N-"));
        assertBuiltSequenceEquals("AC-N-GT",sut);
    }
    @Test
    public void insertContentsOfOtherBuilder(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, new NucleotideSequenceBuilder("-N-"));
        assertBuiltSequenceEquals("AC-N-GT",sut);
        assertEquals(2, sut.getNumGaps());
    }
    @Test(expected = NullPointerException.class)
    public void insertContentsOfNullBuilderShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, (NucleotideSequenceBuilder)null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertContentsOfOtherBuilderBeyondLastOffsetShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(4, new NucleotideSequenceBuilder("-N-"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertContentsOfOtherBuilderAtNegativeOffsetShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(-1, new NucleotideSequenceBuilder("-N-"));
    }
    @Test
    public void deleteEmptyShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(new Range.Builder().build());
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteEmptyButBeyondLengthShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(new Range.Builder().shift(500000).build());
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteEmptyButBeyondNegativeShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(new Range.Builder().shift(-3).build());
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteSingleBase(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.of(2));
        assertBuiltSequenceEquals("ACT",sut);
    }
    @Test(expected = IllegalArgumentException.class)
    public void deleteStartingAfterLengthShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.of(5));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteStartingAtNegativeShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.of(-5));
    }
    @Test(expected = NullPointerException.class)
    public void deleteNullRangeShouldThrowNPE(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(null);
    }
    @Test
    public void deleteRangeGoesBeyondLengthShouldDeleteAsMuchAsPossible(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.of(2,10));
        assertBuiltSequenceEquals("AC",sut);
    }
    @Test
    public void deleteMultipleBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("AC-N-GT");
        sut.delete(Range.of(2,4));
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteAllBasesShouldReturnEmptySequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.of(0,3));
        assertBuiltSequenceEquals("",sut);
    }
    @Test
    public void mixOfAllOperations(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append("TCGA") //ACGTTCGA
            .prepend("TCGAN-") //TCGAN-ACGTTCGA
            .insert(5,"AAAAA")//TCGANAAAAA-ACGTTCGA
            .delete(Range.of(2,8)) //TCA-ACGTTCGA
            .append("GGGGG") //TCA-ACGTTCGAGGGGG
            .ungap() //TCAACGTTCGAGGGGG
            .reverseComplement(); //CCCCCTCGAACGTTGA
        assertBuiltSequenceEquals("CCCCCTCGAACGTTGA",sut);
    }
    
    @Test
    public void testToString(){
    	  NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
          sut.append("TCGA") //ACGTTCGA
              .prepend("TCGAN-") //TCGAN-ACGTTCGA
              .insert(5,"AAAAA")//TCGANAAAAA-ACGTTCGA
              .delete(Range.of(2,8)) //TCA-ACGTTCGA
              .append("GGGGG") //TCA-ACGTTCGAGGGGG
              .ungap() //TCAACGTTCGAGGGGG
              .reverseComplement(); //CCCCCTCGAACGTTGA
          assertEquals("CCCCCTCGAACGTTGA",sut.toString());
    }
    
    @Test
    public void reverseComplementOddNumberOfBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT-");
        sut.reverseComplement();
        assertBuiltSequenceEquals("-ACGT",sut);
    }
    @Test
    public void reverseComplementPalindromicSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("GAATTC");
        sut.reverseComplement();
        assertBuiltSequenceEquals("GAATTC",sut);
    }
    @Test
    public void reverseComplementEvenNumberOfBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("TGGA");
        sut.reverseComplement();
        assertBuiltSequenceEquals("TCCA",sut);
    }
    
    @Test
    public void ungapNoGapsShouldNotAffectSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("TGGA")
                    .ungap();
        assertBuiltSequenceEquals("TGGA",sut); 
        assertEquals(0, sut.getNumGaps());
    }
    @Test
    public void ungapHasOneGapShouldRemoveGap(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("TG-GA")
                    .ungap();
        assertBuiltSequenceEquals("TGGA",sut);    
        assertEquals(0, sut.getNumGaps());
    }
    @Test
    public void ungapHasMultipleGapsShouldRemoveAllGaps(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-G--A")
                    .ungap();
        assertBuiltSequenceEquals("TGGA",sut);   
        assertEquals(0, sut.getNumGaps());
    }
    
    @Test
    public void deleteRemovesNumGapsCorrectly(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-G--A")
                                        .delete(Range.of(2,5));
        assertEquals(2,sut.getNumGaps());
    }
    
    @Test
    public void deleteRemovesNumNsCorrectly(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-N--A");
        assertEquals(1, sut.getNumNs());
        sut.delete(Range.of(2,5));
        assertEquals(2,sut.getNumGaps());
        assertEquals(0, sut.getNumNs());
    }
    
    @Test
    public void countsNumberOfAmbiguitiesCorrectly(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-S--A");
        assertEquals(1, sut.getNumAmbiguities());
    }
    
    @Test
    public void deleteRemovesNumAmbiguitiesCorrectly(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-S--A");
        assertEquals(1, sut.getNumAmbiguities());
        sut.delete(Range.of(2,5));
        assertEquals(2,sut.getNumGaps());
        assertEquals(0, sut.getNumAmbiguities());
    }
    @Test
    public void replace(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
                        .replace(2, Nucleotide.Cytosine);
        assertBuiltSequenceEquals("ACCT",sut);   
    }
    @Test
    public void replaceWithGap(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
                        .replace(2, Nucleotide.Gap);
        assertEquals(1,sut.getNumGaps());
        assertBuiltSequenceEquals("AC-T",sut);   
    }
    @Test
    public void replaceGapWithNonGap(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("AC-T")
                        .replace(2, Nucleotide.Cytosine);
        assertEquals(0,sut.getNumGaps());
        assertBuiltSequenceEquals("ACCT",sut);   
    }
    @Test
    public void replaceWithN(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
                        .replace(2, Nucleotide.Unknown);
        assertEquals(1,sut.getNumNs());
        assertBuiltSequenceEquals("ACNT",sut);   
    }
    @Test
    public void replaceNWithNonN(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACNT")
                        .replace(2, Nucleotide.Cytosine);
        assertEquals(0,sut.getNumNs());
        assertBuiltSequenceEquals("ACCT",sut);   
    }
    
    @Test
    public void replaceWithAmbiguity(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
                        .replace(2, Nucleotide.Strong);
        assertEquals(1,sut.getNumAmbiguities());
        assertBuiltSequenceEquals("ACST",sut);   
    }
    @Test
    public void replaceAmbiguityWithNonAmbiguity(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACST")
                        .replace(2, Nucleotide.Cytosine);
        assertEquals(0,sut.getNumAmbiguities());
        assertBuiltSequenceEquals("ACCT",sut);   
    }
    
    @Test(expected = NullPointerException.class)
    public void replaceWithNullShouldThrowException(){
       new NucleotideSequenceBuilder("ACGT")
                        .replace(2, null);

    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceAtNegativeOffsetShouldThrowException(){
       new NucleotideSequenceBuilder("ACGT")
                        .replace(-1, Nucleotide.Cytosine);
    }
    @Test(expected = IllegalArgumentException.class)
    public void replaceBeyondLastOffsetShouldThrowException(){
       new NucleotideSequenceBuilder("ACGT")
                        .replace(4, Nucleotide.Cytosine);
    }
   
    
    @Test
    public void trim(){
    	 assertEquals("CG",
                 new NucleotideSequenceBuilder("ACGT")
    	 					.trim(Range.of(1,2))
                         .build().toString());
    }
    
    @Test
    public void trimNegativeBeginShouldAdjustTo0(){
    	 assertEquals("ACG",
                 new NucleotideSequenceBuilder("ACGT")
    	 					.trim(Range.of(-1,2))
                         .build().toString());
    }
    
    @Test
    public void trimEndBeyondLengthShouldAdjustToEnd(){
    	 assertEquals("CGT",
                 new NucleotideSequenceBuilder("ACGT")
    	 					.trim(Range.of(1,5))
                         .build().toString());
    }
    
    @Test
    public void trimEntirelyNegativeRangeShouldRemoveAllBases(){
         assertEquals(0,        new NucleotideSequenceBuilder("ACGT")
    	 					.trim(Range.of(-5,-2))
                         .build().getLength());
    }
    
    
    @Test
    public void trimEmptyDeletesEntireSequence(){
    	 assertEquals("",
                 new NucleotideSequenceBuilder("ACGT")
    	 					.trim(new Range.Builder(0).shift(-1).build())
                         .build().toString());
    }
    @Test
    public void trimAndAppendSeesDownstreamChangesAndChangesCodecAccordinly(){
    	 assertEquals("CGR-NA",
                 new NucleotideSequenceBuilder("ACGT")
    	 					.trim(Range.of(1,2))
    	 					.append("R-NA")
                         .build().toString());
    }
    @Test
    public void trimReturnsSameReference(){
    	NucleotideSequenceBuilder untrimmed =  new NucleotideSequenceBuilder("ACGT");
    	NucleotideSequenceBuilder trimmed = untrimmed.trim(Range.of(1,2));
    	assertSame(untrimmed, trimmed);
    }
    @Test
    public void toStringShouldReturnSequenceAsString(){
        assertEquals("ACGT",
                new NucleotideSequenceBuilder("ACGT")
                    .toString());
    }
    
    
   
    
    @Test
    public void reverseEvenNumberOfBases(){
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("AAGG");
    	 sut.reverse();
    	 assertEquals("GGAA",sut.build().toString());
    }
    @Test
    public void reverseOddNumberOfBases(){
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("AATGG");
    	 sut.reverse();
    	 assertEquals("GGTAA",sut.build().toString());
    }
    
    @Test
    public void buildWithReference(){
    	 NucleotideSequence reference = new NucleotideSequenceBuilder( "AAACCCGGGTTT").build();
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder(  "ACCCG")
    	 									.setReferenceHint(reference, 2);
    	 NucleotideSequence builtSequence = sut.build();
		assertEquals("ACCCG",builtSequence.toString());
		assertTrue(builtSequence instanceof ReferenceMappedNucleotideSequence);
    }
   
    
    
    @Test
    public void trimWithReference(){
    	NucleotideSequence reference = new NucleotideSequenceBuilder( "AAACCCGGGTTT").build();
   	 	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder(  "ACCCG")
   	 									.setReferenceHint(reference, 2)
   	 									.trim(Range.of(2, 4));
   	 	NucleotideSequence builtSequence = sut.build();
		assertEquals("CCG",builtSequence.toString());
		assertTrue(builtSequence instanceof ReferenceMappedNucleotideSequence);
    }
   
    
    
    
    @Test
    public void copyCopiesFullSequence(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("A-ATG-G");
    	NucleotideSequenceBuilder copy = sut.copy();
    	assertEquals("A-ATG-G", copy.toString());
    }
    
    @Test
    public void changesToCopyDontAffectOriginal(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("A-ATG-G");
    	NucleotideSequenceBuilder copy = sut.copy();
    	
    	copy.ungap();
    	sut.append("TTG");
    	assertEquals("A-ATG-GTTG", sut.toString());
    	assertEquals("AATGG", copy.toString());
    }
    
    @Test
    public void sameRefShouldBeEqual(){
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	 TestUtil.assertEqualAndHashcodeSame(sut,sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	 TestUtil.assertEqualAndHashcodeSame(sut,sut.copy());
    }
    
    @Test
    public void differentValuesBeyondTailShouldStillBeEqual(){
    	 NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	 NucleotideSequenceBuilder other = sut.copy();
    	 
    	 sut.append(Nucleotide.Gap);
    	 other.append(Nucleotide.Cytosine);
    	 
    	 sut.delete(Range.of(4));
    	 other.delete(Range.of(4));
    	 TestUtil.assertEqualAndHashcodeSame(sut,other);
    }
    @Test
    public void differentValuesShouldNotBeEqual(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
   	 	TestUtil.assertNotEqualAndHashcodeDifferent(sut,
   	 									sut.copy()
   	 									.replace(1,Nucleotide.Gap));
    }
    @Test
    public void builderWithOtherSequenceAsPrefixShouldNotBeEqual(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
   	 	TestUtil.assertNotEqualAndHashcodeDifferent(sut,
   	 									sut.copy()
   	 									.append(Nucleotide.Gap));
    }
    
    @Test
    public void testComplement(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
    										.complement();
    	assertEquals("TGCA", sut.toString());
    }
}
