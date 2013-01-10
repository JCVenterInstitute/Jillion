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

import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.jillion.core.Direction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestQualityClassBuilder {

    Nucleotide consensusGlyph = Nucleotide.Adenine;
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
        sut.addHighQualityAgreement(Direction.FORWARD)
        .addHighQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_BOTH_DIRS, sut.build());
    }
    
    @Test
    public void QualityClass_2(){
        sut.addHighQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR, sut.build());
    }
    @Test
    public void QualityClass_3(){
        sut.addHighQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_HIGH_QUAL_AND_LOW_QUAL_SAME_DIR, sut.build());
    }
    @Test
    public void QualityClass_4(){
        sut.addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_LOW_QUAL_BOTH_DIR, sut.build());
    }
    
    @Test
    public void QualityClass_5(){
        sut.addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.NO_CONFLICT_2_LOW_QUAL_SAME_DIR, sut.build());    
    }
    
    @Test
    public void QualityClass_6(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD)
        .addHighQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRECTIONS_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_7(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_8_agreementInSameDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_8_agreementInOppositeDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.REVERSE)
        .addLowQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE, sut.build());    
    }
    
    @Test
    public void QualityClass_9_forward(){
        sut.addHighQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_HIGH_QUAL, sut.build());    
    }
    
    @Test
    public void QualityClass_9_reverse(){
        sut.addHighQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_HIGH_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_10_forwardConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE, sut.build()); 
    }
    @Test
    public void QualityClass_10_reverseConflict(){
        sut.addLowQualityConflict(Direction.REVERSE)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE, sut.build()); 
    }
    @Test
    public void QualityClass_11_forward(){
        sut.addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_LOW_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_11_reverse(){
        sut.addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.ONE_X_COVERAGE_LOW_QUAL, sut.build()); 
    }
    
    @Test
    public void QualityClass_12_sameDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE, sut.build());
    }
    @Test
    public void QualityClass_12_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.REVERSE)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_13_sameDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        .addHighQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    @Test
    public void QualityClass_13_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.REVERSE)
        .addHighQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_14_sameDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_14_oppositeDirectionAsConflict(){
        sut.addLowQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_15(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD)
        .addHighQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRS_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_16(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE, sut.build());
    
    }
    
    @Test
    public void QualityClass_17_secondAgreementIsLowQuality(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.REVERSE)
        .addLowQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_17_secondAgreementIsHighQuality(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.REVERSE)
        .addHighQualityAgreement(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_18(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRS_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_19_sameDirectionAsConflict(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.FORWARD)
        .addLowQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_19_oppositeDirectionAsConflict(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.REVERSE)
        .addLowQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_20_sameDirectionAsConflict(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_20_oppoisteDirectionAsConflict(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityAgreement(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE, sut.build());
    }
    
    @Test
    public void QualityClass_21_highQualityAgreement(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(Nucleotide.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addHighQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithAmbigiousConsensus.build());
    }
    @Test
    public void QualityClass_21_gappedConsensus(){
        QualityClass.Builder sutWithGapConsensus = new QualityClass.Builder(Nucleotide.Gap,highQuality);
        sutWithGapConsensus.addHighQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithGapConsensus.build());
    }
    @Test
    public void QualityClass_21_lowQualityAgreement(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(Nucleotide.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityAgreement(Direction.FORWARD);
        
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_highConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(Nucleotide.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addHighQualityConflict(Direction.FORWARD)
        .addHighQualityConflict(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_lowConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(Nucleotide.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityConflict(Direction.FORWARD)
        .addLowQualityConflict(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_22_highAndLowConflicts(){
        QualityClass.Builder sutWithAmbigiousConsensus = new QualityClass.Builder(Nucleotide.NotAdenine,highQuality);
        sutWithAmbigiousConsensus.addLowQualityConflict(Direction.FORWARD)
        .addLowQualityConflict(Direction.REVERSE)
        .addHighQualityConflict(Direction.FORWARD)
        .addHighQualityConflict(Direction.REVERSE);
        assertCorrectQualityClassBuilt(QualityClass.AMBIGUIOUS_CONSENSUS, sutWithAmbigiousConsensus.build());
    }
    
    @Test
    public void QualityClass_23_lowQualityAgreement(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addLowQualityAgreement(Direction.FORWARD);       
        assertCorrectQualityClassBuilt(QualityClass.HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE, sut.build());
    }
    @Test
    public void QualityClass_23_miscalledConsensus(){
        sut.addHighQualityConflict(Direction.FORWARD)
        
        .addHighQualityConflict(Direction.FORWARD) 
        .addHighQualityConflict(Direction.REVERSE);
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
        sut.addHighQualityAgreement(Direction.REVERSE);
        sut.addHighQualityAgreement(Direction.REVERSE);
        sut.addHighQualityAgreement(Direction.REVERSE);
        sut.addHighQualityAgreement(Direction.REVERSE);        
        sut.addHighQualityAgreement(Direction.REVERSE);
        sut.addHighQualityAgreement(Direction.FORWARD);
        sut.addHighQualityAgreement(Direction.FORWARD);
        sut.addLowQualityAgreement(Direction.REVERSE);
        //1 high quality conflict
        sut.addHighQualityConflict(Direction.REVERSE);
        
        assertCorrectQualityClassBuilt(QualityClass.valueOf(15), sut.build());
        
    }
    
    @Test
    public void agreementsInOnlyOneDirHighAndLowQualConflicts(){
        sut.addHighQualityAgreement(Direction.REVERSE)
        .addHighQualityAgreement(Direction.REVERSE)
        .addHighQualityAgreement(Direction.REVERSE)

        .addHighQualityConflict(Direction.FORWARD)
        .addLowQualityConflict(Direction.FORWARD)
        .addLowQualityConflict(Direction.FORWARD);
        assertCorrectQualityClassBuilt(QualityClass.valueOf(17), sut.build());
    }
}
