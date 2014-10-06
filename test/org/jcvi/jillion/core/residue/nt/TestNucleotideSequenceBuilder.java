/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
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
    public void appendCharArray(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append("GGTGCA".toCharArray());
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    } 
    @Test
    public void appendCharArrayWithWhitespace(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append("\nG\tGT GCA".toCharArray());
        assertBuiltSequenceEquals("ACGTGGTGCA",sut);
    } 
    @Test
    public void appendCharArrayWithNullsShouldIgnoreNull(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        char[] array = new char[7];
        array[0] = 'G';
        array[1] = 'G';
        array[2] = 'T';
        array[3] = '\0';
        array[4] = 'G';
        array[5] = 'C';
        array[6] = 'A';
        
        sut.append(array);
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
    public void appendNucleotideList(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append(Nucleotides.parse("GGTGCA"));
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
    public void prependString(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend("GGTGCA");
        assertBuiltSequenceEquals("GGTGCAACGT",sut);
    }
    @Test
    public void prependArray(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend("GGTGCA".toCharArray());
        assertBuiltSequenceEquals("GGTGCAACGT",sut);
    }
    @Test
    public void prependSequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend(createSequence("GGTGCA"));
        assertBuiltSequenceEquals("GGTGCAACGT",sut);
    }
    @Test
    public void prependList(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.prepend(Nucleotides.parse("GGTGCA"));
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
    public void insertArray(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, "-".toCharArray());
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
    public void insertNucleotideList(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, Nucleotides.parse("-N-"));
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
    public void insertContentsOfOtherBuilder2BeyondLastOffsetShouldThrowException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(5, new NucleotideSequenceBuilder("-N-"));
    }
    @Test
    public void insertContentsOfOtherBuilderBeyondLastOffsetShouldActLikeAppend(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(4, new NucleotideSequenceBuilder("-N-"));
        assertBuiltSequenceEquals("ACGT-N-",sut);
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
           //   assertEquals("TCAACGTTCGAGGGGG",sut.toString());
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
    
    @Test
    public void clear(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
    										.clear();
    	assertEquals("",sut.toString());
    	assertEquals(0, sut.getLength());
    	assertEquals(0, sut.getNumAmbiguities());
    	assertEquals(0, sut.getNumGaps());
    	assertEquals(0, sut.getNumNs());
    	assertEquals(0, sut.getUngappedLength());
    }
    
    @Test
    public void clearThenAppend(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT")
											.clear()
											.append("RW-GN");
    	assertEquals("RW-GN",sut.toString());
    	assertEquals(5, sut.getLength());
    	assertEquals(3, sut.getNumAmbiguities());
    	assertEquals(1, sut.getNumGaps());
    	assertEquals(1, sut.getNumNs());
    	assertEquals(4, sut.getUngappedLength());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getBeyondLengthShouldThrowException(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ACGT");
    	sut.get(5);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void getNegativeShouldThrowException(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ACGT");
    	sut.get(-1);
    }
    @Test
    public void get(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ACGT");
    	
    	assertEquals(Nucleotide.Adenine, sut.get(0));
    	assertEquals(Nucleotide.Cytosine, sut.get(1));
    	assertEquals(Nucleotide.Guanine, sut.get(2));
    	assertEquals(Nucleotide.Thymine, sut.get(3));
    }
    
    @Test
    public void getNOffsetsOfReversed(){
    	//gap offsets = 1,4,6
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ANCGNTT");
    	sut.reverseComplement();
    	assertEquals("AANCGNT", sut.toString());
    	assertArrayEquals(new int[]{2,5}, sut.getNOffsets());
    }
    
    @Test
    public void ungapSequenceWithNs(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ANC-GNT-T");
    	sut.ungap();
    	assertArrayEquals(new int[]{1,4}, sut.getNOffsets());
    	assertEquals("ANCGNTT", sut.build().toString());
    }
    
    @Test
    public void ungapSequenceWithManyGapsBetweenNs(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("A---NC--GNT-T");
    	sut.ungap();
    	assertArrayEquals(new int[]{1,4}, sut.getNOffsets());
    	assertEquals("ANCGNTT", sut.build().toString());
    }
    
    @Test
    public void ungapSequenceWithNsButHasNoGaps(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ANCGNTT");
    	sut.ungap();
    	assertArrayEquals(new int[]{1,4}, sut.getNOffsets());
    	assertEquals("ANCGNTT", sut.build().toString());
    }
    
    @Test
    public void prependSequenceWithNs(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ANCGNTT");
    	sut.prepend("ACGT");
    	assertArrayEquals(new int[]{5,8}, sut.getNOffsets());
    	assertEquals("ACGTANCGNTT", sut.build().toString());
    }
    @Test
    public void insertSequenceWithNs(){
    	NucleotideSequenceBuilder sut =new NucleotideSequenceBuilder("ANCGNTT");
    	sut.insert(2,"ACGT");
    	assertArrayEquals(new int[]{1,8}, sut.getNOffsets());
    	assertEquals("ANACGTCGNTT", sut.build().toString());
    }
    
    @Test
    public void turnOffCompression(){
    	NucleotideSequence uncompressed =new NucleotideSequenceBuilder("ACGTACGTTACG")
    													.turnOffDataCompression(true)
    													.build();
    	NucleotideCodec codec = (NucleotideCodec) Whitebox.getInternalState(uncompressed, "codec");
    	assertTrue(codec instanceof BasicNucleotideCodec);
    	
    													
    }
    
    
    @Test
    public void toUngappedRangeWithNoGapsShouldReturnSameRange(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGTACGTTACG");
    	
    	assertEquals(range, sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeWithOnlyUpstreamGapsShouldReturnSameRange(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGTACGTT-AC-G");
    	
    	assertEquals(range, sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeDownStreamGapsOnly(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("A-CGTACGTTACG");
    	
    	assertEquals(new Range.Builder(range)
    								.shift(1)
    								.build()
						, sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeWithOnlyInternalGapsShouldGrowEnd(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACG-TACGTTACG");
    	
    	assertEquals(new Range.Builder(range)
								.expandEnd(1)
								.build(), 
							sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeWithMultipleInternalGapsShouldGrowEnd(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACG-TA-CGTTACG");
    	
    	assertEquals(new Range.Builder(range)
								.expandEnd(2)
								.build(), 
						sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeWithConsecutiveInternalGapsShouldGrowEnd(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACG--TA--CGTTACG");
    	
    	assertEquals(new Range.Builder(range)
								.expandEnd(4)
								.build(), 
						sut.toGappedRange(range));
    }
    
    @Test
    public void toUngappedRangeWithGapsAllOver(){
    	Range range = Range.of(2,5);
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("A-CG--TA--CGTTA-C--G");
    	
    	assertEquals(new Range.Builder(range)
								.expandEnd(4)
								.shift(1)
								.build(), 
						sut.toGappedRange(range));
    }
    
    @Test
    public void isEqualToNullShouldReturnFalse(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	assertFalse(sut.isEqualTo(null));
    	
    }
    
    @Test
    public void isEqualToSameSequence(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	assertTrue(sut.isEqualTo(sut.build()));
    }
    
    @Test
    public void isEqualToDifferentSequenceShouldReturnFalse(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	assertFalse(sut.isEqualTo(new NucleotideSequenceBuilder("ACGC").build()));
    }
    
    @Test
    public void isEqualToDifferentOtherSequenceLengthSeqShouldReturnFalse(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	assertFalse(sut.isEqualTo(new NucleotideSequenceBuilder("ACGTC").build()));
    }
    @Test
    public void isEqualToDifferentSequenceLengthSeqShouldReturnFalse(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGTC");
    	assertFalse(sut.isEqualTo(new NucleotideSequenceBuilder("ACGT").build()));
    }
    
    @Test
    public void copySubRangeWithFullRange(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	
    	assertEquals(sut, sut.copy(Range.ofLength(4)));
    	
    }
    
    @Test
    public void copySubRange(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	
    	Range range = Range.of(1,2);
		NucleotideSequenceBuilder subrange = sut.copy(range);
		assertEquals(sut.trim(range), subrange);
    	
    }
    
    @Test
    public void copySubRangeGoesBeyondLengthShouldOnlyCopyTilLength(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	
    	Range range = Range.of(1,10);
		NucleotideSequenceBuilder subrange = sut.copy(range);
		assertEquals(sut.trim(range), subrange);
    }
    
    @Test
    public void copySubRangeGoesBeforeStartShouldOnlyCopyStartingAtStart(){
    	NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
    	
    	Range range = Range.of(-10,2);
		NucleotideSequenceBuilder subrange = sut.copy(range);
		assertEquals(sut.trim(range), subrange);
    }
    
    
    
}
