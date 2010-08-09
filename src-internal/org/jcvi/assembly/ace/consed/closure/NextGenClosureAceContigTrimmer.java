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

package org.jcvi.assembly.ace.consed.closure;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigTrimmer;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.trim.ElviraSangerContigEndTrimmer;
import org.jcvi.assembly.trim.MinimumBidirectionalEndCoverageTrimmer;
import org.jcvi.assembly.trim.MinimumEndCoverageTrimmer;
import org.jcvi.assembly.trim.PlacedReadTrimmer;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
/**
 * @author dkatzel
 *
 *
 */
public class NextGenClosureAceContigTrimmer extends AceContigTrimmer{

    /**
     * @param trimmers
     */
    public NextGenClosureAceContigTrimmer(int minSangerEndCloneCoverage,int minBiDirectionalEndCoverage, int ignoreThresholdEndCoverage){
        super( Arrays.<PlacedReadTrimmer<AcePlacedRead, AceContig>>asList(
                     //   new MinimumEndCoverageTrimmer<AcePlacedRead, AceContig>(minimumEndCoverage),
                    //    new MinimumBidirectionalEndCoverageTrimmer<AcePlacedRead, AceContig>(minBiDirectionalEndCoverage, ignoreThresholdEndCoverage),
                        new ElviraSangerContigEndTrimmer<AcePlacedRead, AceContig>(minSangerEndCloneCoverage,minBiDirectionalEndCoverage, ignoreThresholdEndCoverage)));
          
    }

    @Override
    protected String createNewContigId(String oldContigId, NucleotideEncodedGlyphs oldConsensus, Range newContigRange){
       String id= super.createNewContigId(oldContigId, oldConsensus, newContigRange);
       Pattern pattern = Pattern.compile("^(\\S+)_(\\d+)_(\\d+)$");
       Matcher trimmedSplitmatcher = pattern.matcher(id);
       if(!trimmedSplitmatcher.matches()){
           return id;
       }
       String untrimmedId = trimmedSplitmatcher.group(1);
       
       Matcher _0xMatcher = pattern.matcher(untrimmedId);
       if(!_0xMatcher.matches()){
           return id;
       }
       int trimmedLeft = Integer.parseInt(trimmedSplitmatcher.group(2));
       int trimmedRight = Integer.parseInt(trimmedSplitmatcher.group(3));
       String originalId = _0xMatcher.group(1);
       int _0xLeft = Integer.parseInt(_0xMatcher.group(2));
       return String.format("%s_%d_%d",originalId,_0xLeft +trimmedLeft-1, _0xLeft+trimmedRight);
    }
    
    
}
