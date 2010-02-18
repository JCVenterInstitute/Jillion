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
/*
 * Created on Jan 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.qualClass;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestQualityClassBuilder {

    NucleotideGlyph consensusGlyph = NucleotideGlyph.Adenine;
    QualityClass.Builder sut;
    PhredQuality highQuality = PhredQuality.valueOf(30);
    @Before
    public void setup(){
        sut = new QualityClass.Builder(consensusGlyph,highQuality);
    }
    @Test
    public void zeroCoverageShouldBeQualityClass_0(){        
        assertCorrectQualityClassBuilt(QualityClass.ZERO_COVERAGE, sut.build());
    }
    
    @Test
    public void buildingMoreThanOnceShouldThrowIllegalStateException(){
        sut.build();
        try{
            sut.build();
            fail("shoult throw illegalStateException");
        }catch(IllegalStateException e){
            assertEquals("already built", e.getMessage());
        }
    }
    private void assertCorrectQualityClassBuilt(QualityClass expectedQualityClass, QualityClass actualQualtyClass) {
        assertEquals(expectedQualityClass, actualQualtyClass);
        assertEquals(expectedQualityClass.getValue(), actualQualtyClass.getValue());
    }
    
    @Test
    public void QualityClass_1(){
        sut.addHighQualityAgreement(SequenceDirection.FORWARD)
        .addHighQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_BOTH_DIRS, sut.build());
    }
    
    @Test
    public void QualityClass_2(){
        sut.addHighQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR, sut.build());
    }
    @Test
    public void QualityClass_3(){
        sut.addHighQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_AND_LOW_QUAL_SAME_DIR, sut.build());
    }
    @Test
    public void QualityClass_4(){
        sut.addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_LOW_QUAL_BOTH_DIR, sut.build());
    }
    
    @Test
    public void QualityClass_5(){
        sut.addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_2_LOW_QUAL_SAME_DIR, sut.build());    
    }
    
    @Test
    public void QualityClass_6(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD)
        .addHighQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRECTIONS_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_7(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_8_agreementInSameDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_8_agreementInOppositeDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.REVERSE)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_9_forward(){
        sut.addHighQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_HIGH_QUAL, sut.build());    
    }
    
    @Test
    public void QualityClass_9_reverse(){
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_HIGH_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_10_forwardConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE, sut.build()); 
    }
    @Test
    public void QualityClass_10_reverseConflict(){
        sut.addLowQualityConflict(SequenceDirection.REVERSE)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE, sut.build()); 
    }
    @Test
    public void QualityClass_11_forward(){
        sut.addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_LOW_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_11_reverse(){
        sut.addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_LOW_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_12_sameDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE, sut.build());
    }
    @Test
    public void QualityClass_12_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.REVERSE)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_13_sameDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        .addHighQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    @Test
    public void QualityClass_13_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.REVERSE)
        .addHighQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_14_sameDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_14_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_15(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD)
        .addHighQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRS_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_16(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE, sut.build());
    
    }
    
    @Test
    public void QualityClass_17_secondAgreementIsLowQuality(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.REVERSE)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_17_secondAgreementIsHighQuality(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.REVERSE)
        .addHighQualityAgreement(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_18(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRS_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_19_sameDirectionAsConflict(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD)
        .addLowQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_19_oppositeDirectionAsConflict(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.REVERSE)
        .addLowQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_20_sameDirectionAsConflict(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_20_oppoisteDirectionAsConflict(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityAgreement(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_21_highQualityAgreement(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(NucleotideGlyph.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addHighQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithAmbigiousConsensus.build());
    }
    @Test
    public void QualityClass_21_gappedConsensus(){
        QualityClass.Builder sutWithGapConsensus = new QualityClass.Builder(NucleotideGlyph.Gap,highQuality);
        sutWithGapConsensus.addHighQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithGapConsensus.build());
    }
    @Test
    public void QualityClass_21_lowQualityAgreement(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(NucleotideGlyph.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityAgreement(SequenceDirection.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_highConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(NucleotideGlyph.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addHighQualityConflict(SequenceDirection.FORWARD)
        .addHighQualityConflict(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_lowConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(NucleotideGlyph.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityConflict(SequenceDirection.FORWARD)
        .addLowQualityConflict(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_highAndLowConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(NucleotideGlyph.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityConflict(SequenceDirection.FORWARD)
        .addLowQualityConflict(SequenceDirection.REVERSE)
        .addHighQualityConflict(SequenceDirection.FORWARD)
        .addHighQualityConflict(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_23_lowQualityAgreement(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addLowQualityAgreement(SequenceDirection.FORWARD);       
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE, sut.build());
    }
    @Test
    public void QualityClass_23_miscalledConsensus(){
        sut.addHighQualityConflict(SequenceDirection.FORWARD)
        
        .addHighQualityConflict(SequenceDirection.FORWARD) 
        .addHighQualityConflict(SequenceDirection.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.MISCALLED_CONSENSUS, sut.build());
    }
    /**
     * while comparing output from QualityClassBuiler and slicetools in
     * cloe, I came across a strange occurrence where sometimes
     * slice tools would report a Quality class of 22 (ambiguous consensus)
     * while QualityClassBuilder reports a 15 (high qual conflict)
     * the data for this test came from a coronavirus sample
     * where the slice of 9x coverage 8 of which are T's and 1 gap
     * The gap is high quality.
     * 
     * I spoke with Erin Hine about this and apparently
     * this is known in closure as  a "fake 22".  Slice tools
     * sees the T's and the gap and considers the consensus ambiguious.
     * 
     */
    @Test
    public void NoFake22(){
        //8x of agreements in both directions
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);        
        sut.addHighQualityAgreement(SequenceDirection.REVERSE);
        sut.addHighQualityAgreement(SequenceDirection.FORWARD);
        sut.addHighQualityAgreement(SequenceDirection.FORWARD);
        sut.addLowQualityAgreement(SequenceDirection.REVERSE);
        //1 high quality conflict
        sut.addHighQualityConflict(SequenceDirection.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.valueOf((byte)15), sut.build());
        
    }
    
    @Test
    public void agreementsInOnlyOneDirHighAndLowQualConflicts(){
        sut.addHighQualityAgreement(SequenceDirection.REVERSE)
        .addHighQualityAgreement(SequenceDirection.REVERSE)
        .addHighQualityAgreement(SequenceDirection.REVERSE)

        .addHighQualityConflict(SequenceDirection.FORWARD)
        .addLowQualityConflict(SequenceDirection.FORWARD)
        .addLowQualityConflict(SequenceDirection.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.valueOf((byte)17), sut.build());
    }
}
