/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.Set;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
public class BasicChurchillWatermanConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    private static final int MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY = 5;


    public BasicChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    

    @Override
    protected NucleotideGlyph getConsensus(
            ProbabilityStruct normalizedErrorProbabilityStruct,
            Slice slice) {
        final Set<NucleotideGlyph> basesUsedTowardsAmbiguity = getBasesUsedTowardsAmbiguity(normalizedErrorProbabilityStruct,
                        MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY);
        return NucleotideGlyph.getAmbiguityFor(basesUsedTowardsAmbiguity);
        
    }
    
    

    


    
}
