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
