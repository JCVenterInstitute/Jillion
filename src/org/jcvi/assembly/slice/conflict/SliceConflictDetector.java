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
 * Created on Dec 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.conflict;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.assembly.slice.SliceElement;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public class SliceConflictDetector {

    public static enum SliceConflicts implements Conflict{
        SEQUENCE_ERROR,
        POSSIBLE_SNP,
        NO_CONFLICT
    }
    private final int percentDifferenceForSnp;

    /**
     * @param percentDifferenceForSnp
     */
    public SliceConflictDetector(int percentDifferenceForSnp) {
        this.percentDifferenceForSnp = percentDifferenceForSnp;
    }
    
    public SliceConflicts analyize(Slice slice, NucleotideGlyph consensus){
        double conflictCount=0;
        int totalCount=0;
        for(SliceElement element : slice){
            if(!element.getBase().equals(consensus) && !element.getBase().isGap()){
                conflictCount++;  
            }
            totalCount++;
        }
        if(conflictCount >0){
            double percentConflict = conflictCount/totalCount *100;
            if(percentConflict>= percentDifferenceForSnp){
                return SliceConflicts.POSSIBLE_SNP;
            }
            return SliceConflicts.SEQUENCE_ERROR;
        }
        return SliceConflicts.NO_CONFLICT;        
    }
    
    
    
}
