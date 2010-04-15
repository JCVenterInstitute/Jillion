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

import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractQualityValueStrategy implements QualityValueStrategy {

    @Override
    public PhredQuality getQualityFor(PlacedRead placedRead,
            EncodedGlyphs<PhredQuality> fullQualities,
            int gappedReadIndex) {
        if(fullQualities ==null){
            throw new NullPointerException("null qualities for "+placedRead);
        }
        final NucleotideEncodedGlyphs encodedGlyphs = placedRead.getEncodedGlyphs();
        if(!AssemblyUtil.isAGap(encodedGlyphs, gappedReadIndex)){
            return getQualityForNonGapBase(placedRead, fullQualities, gappedReadIndex);
        }
        int leftFlankingNonGapIndex = AssemblyUtil.getLeftFlankingNonGapIndex(encodedGlyphs,gappedReadIndex-1);
        int rightFlankingNonGapIndex = AssemblyUtil.getRightFlankingNonGapIndex(encodedGlyphs,gappedReadIndex+1);
        
        final PhredQuality qualityOfGap = getQualityValueForGap(leftFlankingNonGapIndex, rightFlankingNonGapIndex, placedRead, fullQualities,gappedReadIndex);
        
        return qualityOfGap;
    }
    protected abstract PhredQuality getQualityValueIfReadStartsWithGap();
    protected abstract PhredQuality getQualityValueIfReadEndsWithGap();
    
    protected abstract PhredQuality computeQualityValueForGap(int numberOfGapsBetweenFlanks, int ithGapToCompute,
            PhredQuality leftFlankingQuality, PhredQuality rightFlankingQuality);
    
    private PhredQuality getQualityValueForGap(int leftFlankingNonGapIndex,
            int rightFlankingNonGapIndex, PlacedRead placedRead,
            EncodedGlyphs<PhredQuality> fullQualities,int indexOfGap) {
        if(AssemblyUtil.beforeStartOfRead(leftFlankingNonGapIndex)){
            return getQualityValueIfReadStartsWithGap();
        }
        if(AssemblyUtil.afterEndOfRead(rightFlankingNonGapIndex, placedRead.getEncodedGlyphs())){
            return getQualityValueIfReadEndsWithGap();
        }
        PhredQuality leftFlankingQuality = getQualityForNonGapBase(placedRead, fullQualities, leftFlankingNonGapIndex);
        PhredQuality rightFlankingQuality = getQualityForNonGapBase(placedRead, fullQualities, rightFlankingNonGapIndex);
        int ithGapToCompute = indexOfGap - leftFlankingNonGapIndex-1;
        final int numberOfGapsBetweenFlanks = rightFlankingNonGapIndex-leftFlankingNonGapIndex-1;
        return computeQualityValueForGap(numberOfGapsBetweenFlanks, ithGapToCompute, leftFlankingQuality, rightFlankingQuality);
    }
    

    protected PhredQuality getQualityForNonGapBase(PlacedRead placedRead, EncodedGlyphs<PhredQuality> fullQualities,
            int gappedReadIndexForNonGapBase) {
        try{
        int ungappedFullRangeIndex = AssemblyUtil.convertToUngappedFullRangeIndex(placedRead, (int)fullQualities.getLength(),gappedReadIndexForNonGapBase);
        
            return fullQualities.get(ungappedFullRangeIndex);
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("could not get quality for read " + placedRead +" at gapped index " +gappedReadIndexForNonGapBase,e);
        }
    }
    
   
}
