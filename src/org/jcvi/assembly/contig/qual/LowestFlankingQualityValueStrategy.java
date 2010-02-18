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
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.glyph.phredQuality.PhredQuality;

public class LowestFlankingQualityValueStrategy extends
        AbstractQualityValueStrategy {

    private final PhredQuality LOWEST_QUALITY = PhredQuality.valueOf((byte)1);
    

    @Override
    protected PhredQuality getQualityValueIfReadEndsWithGap() {
        return LOWEST_QUALITY;
    }

    @Override
    protected PhredQuality getQualityValueIfReadStartsWithGap() {
        return LOWEST_QUALITY;
    }

    @Override
    protected PhredQuality computeQualityValueForGap(
            int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality) {
        if(leftFlankingQuality.compareTo(rightFlankingQuality)<0){
            return leftFlankingQuality;
        }
        return rightFlankingQuality;
    }

}
