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
    private Range queryRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,1,806);
    private Range subjectRange = Range.buildRange(CoordinateSystem.RESIDUE_BASED,99,904);
    
    private BigDecimal eValue = new BigDecimal("2e-172");
    private BigDecimal bitScore = new BigDecimal(636.8D);
    
    BlastHit sut = BlastHitBuilder.create(query)
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
        BlastHit same = BlastHitBuilder.copy(sut)
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
        BlastHit different = BlastHitBuilder.copy(sut)
                            .query("not"+query)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentSubjectIdShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .subject("not"+subject)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test
    public void differentPercentIdentityShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .percentIdentity(ident / 2)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentBitScoreShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .bitScore(bitScore.divide(new BigDecimal(2)))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentAlignmentLengthShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .alignmentLength(length +1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentNumMismatchesShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .numMismatches(mismatches+1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentNumGapOpeningsShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .numGapOpenings(numGapOpenings+1)
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentQueryRangeShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .queryRange(queryRange.shiftLeft(2))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentSubjectRangeShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .subjectRange(subjectRange.shiftLeft(2))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    @Test
    public void differentEValueShouldNotBeEqual(){
        BlastHit different = BlastHitBuilder.copy(sut)
                            .eValue(eValue.divide(BigDecimal.TEN))
                            .build();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullQueryShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .query(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullSubjectShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .subject(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullSubjectRangeShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .subjectRange(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullQueryRangeShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .queryRange(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullEVauleShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .eValue(null);
    }
    @Test(expected = NullPointerException.class)
    public void nullBitScoreShouldThrowNPE(){
        BlastHitBuilder.copy(sut)
                        .bitScore(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void emptyQueryShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .query("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void whiteSpaceOnlyQueryShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .query(" \t  ");
    }
    @Test
    public void queryShouldGetWhitespaceTrimmed(){
        BlastHit hit = BlastHitBuilder.copy(sut)
                        .query(" id  ")
                        .build();
        assertEquals("id", hit.getQueryId());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void emptySubjectShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .subject("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void whiteSpaceOnlySubjectShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .subject(" \t  ");
    }
    @Test
    public void subjectShouldGetWhitespaceTrimmed(){
        BlastHit hit = BlastHitBuilder.copy(sut)
                        .subject(" id  ")
                        .build();
        assertEquals("id", hit.getSubjectId());
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void negativePercentIdentityShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .percentIdentity(-1D);
    }
    @Test(expected = IllegalArgumentException.class)
    public void percentIdentityGreaterThanOneHundredShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .percentIdentity(100.01D);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeBitScoreShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .bitScore(new BigDecimal(-1D));
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeNumGapsShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .numGapOpenings(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeNumMismatchesShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .numMismatches(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void negativeAlignmentLengthShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .alignmentLength(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeEValueShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .eValue(new BigDecimal(-1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void eValueGreaterThanOneShouldThrowIllegalArgumentException(){
        BlastHitBuilder.copy(sut)
                        .eValue(new BigDecimal(1.01));
    }
}
