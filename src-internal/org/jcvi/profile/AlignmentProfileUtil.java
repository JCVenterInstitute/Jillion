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

package org.jcvi.profile;

import org.jcvi.glyph.nuc.NucleotideGlyph;

import java.util.Arrays;
import java.util.List;

/**
 * @author dkatzel
 *
 *
 */
public class AlignmentProfileUtil {

    public static List<NucleotideGlyph> ALLELE_ORDER = 
        Arrays.asList(NucleotideGlyph.Adenine,
                NucleotideGlyph.Thymine,
                NucleotideGlyph.Guanine,
                NucleotideGlyph.Cytosine,
                NucleotideGlyph.Gap);
    
    public static int getIndexOf(NucleotideGlyph base){
        return ALLELE_ORDER.indexOf(base);           
    }
    public static NucleotideGlyph getAlleleFor(int offset){
        return ALLELE_ORDER.get(offset);           
    }
    
    public static int getMostCommonAlleleIndexFor(float[] alleleCounts){
        int maxIndex=0;
        for(int i=0; i<alleleCounts.length; i++){
            if(alleleCounts[i] > alleleCounts[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
