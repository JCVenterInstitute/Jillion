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

package org.jcvi.common.core.symbol.residue.nuc;

import org.jcvi.common.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestNucleotideSequenceBuilder {

    @Test
    public void notingBuiltShouldReturnEmptySequence(){
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
        assertEquals(expected, Nucleotides.asString(builder.build()));
    }
    private NucleotideSequence createSequence(String seq){
       return  NucleotideSequenceFactory.create(seq);
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
    public void insert(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.insert(2, "-");
        assertBuiltSequenceEquals("AC-GT",sut);
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
    @Test
    public void deleteEmptyShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildEmptyRange());
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteEmptyButBeyondLengthShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildEmptyRange(500000));
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteEmptyButBeyondNegativeShouldDoNothing(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildEmptyRange(-3));
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteSingleBase(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildRange(2));
        assertBuiltSequenceEquals("ACT",sut);
    }
    @Test(expected = IllegalArgumentException.class)
    public void deleteStartingAfterLengthShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildRange(5));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteStartingAtNegativeShouldThrowIllegalArgumentException(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildRange(-5));
    }
    @Test(expected = NullPointerException.class)
    public void deleteNullRangeShouldThrowNPE(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(null);
    }
    @Test
    public void deleteRangeGoesBeyondLengthShouldDeleteAsMuchAsPossible(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildRange(2,10));
        assertBuiltSequenceEquals("AC",sut);
    }
    @Test
    public void deleteMultipleBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("AC-N-GT");
        sut.delete(Range.buildRange(2,4));
        assertBuiltSequenceEquals("ACGT",sut);
    }
    @Test
    public void deleteAllBasesShouldReturnEmptySequence(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.delete(Range.buildRange(0,3));
        assertBuiltSequenceEquals("",sut);
    }
    @Test
    public void mixOfAllOperations(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT");
        sut.append("TCGA") //ACGTTCGA
            .prepend("TCGAN-") //TCGAN-ACGTTCGA
            .insert(5,"AAAAA")//TCGANAAAAA-ACGTTCGA
            .delete(Range.buildRange(2,8)) //TCA-ACGTTCGA
            .append("GGGGG") //TCA-ACGTTCGAGGGGG
            .ungap() //TCAACGTTCGAGGGGG
            .reverseCompliment(); //CCCCCTCGAACGTTGA
        assertBuiltSequenceEquals("CCCCCTCGAACGTTGA",sut);
    }
    
    @Test
    public void reverseComplimentOddNumberOfBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("ACGT-");
        sut.reverseCompliment();
        assertBuiltSequenceEquals("-ACGT",sut);
    }
    
    @Test
    public void reverseComplimentEvenNumberOfBases(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("TGGA");
        sut.reverseCompliment();
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
                                        .delete(Range.buildRange(2,5));
        assertEquals(2,sut.getNumGaps());
    }
    
    @Test
    public void deleteRemovesNumNsCorrectly(){
        NucleotideSequenceBuilder sut = new NucleotideSequenceBuilder("T-G-N--A");
        assertEquals(1, sut.getNumNs());
        sut.delete(Range.buildRange(2,5));
        assertEquals(2,sut.getNumGaps());
        assertEquals(0, sut.getNumNs());
    }
}
