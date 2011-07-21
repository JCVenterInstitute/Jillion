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
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.qualClass;
import static org.jcvi.glyph.qualClass.QualityClass.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestQualityClassLookupByValue {
    QualityClass qualityClass;
    byte value;
    
    @Parameters
    public static Collection<?> data(){
        return Arrays.asList(new Object[][]{

            {ZERO_COVERAGE,0},
            {NO_CONFLICT_HIGH_QUAL_BOTH_DIRS,1},
            {NO_CONFLICT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR,2},
            {NO_CONFLICT_HIGH_QUAL_AND_LOW_QUAL_SAME_DIR,3},
            {NO_CONFLICT_LOW_QUAL_BOTH_DIR,4},
            {NO_CONFLICT_2_LOW_QUAL_SAME_DIR,5},
            {LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRECTIONS_AGREE,6},
            {LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE,7},
            {LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_SAME_DIR_AGREE,8},
            {ONE_X_COVERAGE_HIGH_QUAL,9},
            {LOW_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRECTIONS_AGREE,10},
            {ONE_X_COVERAGE_LOW_QUAL,11},
            {LOW_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIRECTION_AGREE,12},
            {TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE,13},
            {TWO_X_COVERAGE_LOW_QUAL_CONFLICT_BUT_LOW_QUAL_AGREE,14},
            {HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_BOTH_DIRS_AGREE,15},
            {HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_LOW_QUAL_OTHER_DIR_AGREE,16},
            {HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_ONE_DIR_AND_SOME_OTHER_QUAL_IN_SAME_DIR_AGREE,17},
            {HIGH_QUAL_CONFLICT_BUT_LOW_QUAL_BOTH_DIRS_AGREE,18},
            {HIGH_QUAL_CONFLICT_BUT_2_LOW_QUAL_SAME_DIR_AGREE,19},
            {TWO_X_COVERAGE_HIGH_QUAL_CONFLICT_BUT_HIGH_QUAL_AGREE,20},
            {AMBIGUIOUS_CONSENSUS_ONLY_1_AGREEMENT,21},
            {AMBIGUIOUS_CONSENSUS,22},
            {HIGH_QUAL_CONFLICT_ONLY_ONE_LOW_QUAL_AGREE,23}
                });
            }
    
    public TestQualityClassLookupByValue(QualityClass qualityClass, int value){
        this.qualityClass = qualityClass;
        this.value = (byte)value;
    }
    
    @Test
    public void correctLookup(){
        assertEquals(qualityClass, QualityClass.valueOf(value));
    }
}
