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

package org.jcvi.common.core.align.blast;

import java.math.BigDecimal;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestBlastHit {

    private String query = "AF178033";
    private String subject = "EMORG:AF353201";
    private Double ident = 85.36D;
    private int length = 806;
    private int mismatches = 118;
    private int numGapOpenings = 2;
    private DirectedRange queryRange = DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,1,806));
    private DirectedRange subjectRange = DirectedRange.create(Range.of(CoordinateSystem.RESIDUE_BASED,99,904));
    
    private BigDecimal eValue = new BigDecimal("2e-172");
    private BigDecimal bitScore = new BigDecimal(636.8D);
    
    Hsp sut = HspBuilder.create(query)
                        .subject(subject)
                        .percentIdentity(ident)
                        .alignmentLength(length)
                        .numMismatches(mismatches)
                        .numGapOpenings(numGapOpenings)
                        .queryRange(queryRange)
                        .subjectRange(subjectRange)
                        .eValue(eValue)
                        .bitScore(bitScore)
                        .build();
    
    @Test
    public void getters(){
        assertEquals(query, sut.getQueryId());
        assertEquals(subject, sut.getSubjectId());
        assertEquals(ident, Double.valueOf(sut.getPercentIdentity()));
        assertEquals(length, sut.getAlignmentLength());
        assertEquals(mismatches, sut.getNumberOfMismatches());
        assertEquals(numGapOpenings, sut.getNumberOfGapOpenings());
        assertEquals(queryRange, sut.getQueryRange());
        assertEquals(subjectRange, sut.getSubjectRange());
        assertEquals(eValue, sut.getEvalue());        
        assertEquals(bitScore, sut.getBitScore());
    }
    @Test
    public void sameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void sameValuesShouldBeEqual(){
        Hsp same = HspBuilder.copy(sut)
                                .build();
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualToNonBlastHit(){
        assertFalse(sut.equals("not a blast hit"));
    }
    @Test
    public void differentQueryIdShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .query("not"+query)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentSubjectIdShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .subject("not"+subject)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentPercentIdentityShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .percentIdentity(ident / 2)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentBitScoreShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .bitScore(bitScore.divide(new BigDecimal(2)))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentAlignmentLengthShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .alignmentLength(length +1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentNumMismatchesShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .numMismatches(mismatches+1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentNumGapOpeningsShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .numGapOpenings(numGapOpenings+1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentQueryRangeShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .queryRange(DirectedRange.create(
                            		new Range.Builder(queryRange.getRange())
                            		.shiftLeft(2)
                            		.build()))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentSubjectRangeShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .subjectRange(DirectedRange.create(new Range.Builder(subjectRange.getRange())
						                    		.shiftLeft(2)
						                    		.build()))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentEValueShouldNotBeEqual(){
        Hsp different = HspBuilder.copy(sut)
                            .eValue(eValue.divide(BigDecimal.TEN))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullQueryShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .query(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullSubjectShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .subject(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullSubjectRangeShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .subjectRange(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullQueryRangeShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .queryRange(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullEVauleShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .eValue(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullBitScoreShouldThrowNPE(){
        HspBuilder.copy(sut)
                        .bitScore(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void emptyQueryShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .query("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void whiteSpaceOnlyQueryShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .query(" \t  ");
    }
    @Test
    public void queryShouldGetWhitespaceTrimmed(){
        Hsp hit = HspBuilder.copy(sut)
                        .query(" id  ")
                        .build();
        assertEquals("id", hit.getQueryId());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void emptySubjectShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .subject("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void whiteSpaceOnlySubjectShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .subject(" \t  ");
    }
    @Test
    public void subjectShouldGetWhitespaceTrimmed(){
        Hsp hit = HspBuilder.copy(sut)
                        .subject(" id  ")
                        .build();
        assertEquals("id", hit.getSubjectId());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void negativePercentIdentityShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .percentIdentity(-1D);
    }
    @Test(expected = IllegalArgumentException.class)
    public void percentIdentityGreaterThanOneHundredShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .percentIdentity(100.01D);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeBitScoreShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .bitScore(new BigDecimal(-1D));
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeNumGapsShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .numGapOpenings(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeNumMismatchesShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .numMismatches(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void negativeAlignmentLengthShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .alignmentLength(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeEValueShouldThrowIllegalArgumentException(){
        HspBuilder.copy(sut)
                        .eValue(new BigDecimal(-1));
    }
    
}
