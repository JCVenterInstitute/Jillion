/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.List;

import org.jcvi.glyph.nuc.NucleotideGlyph;

public final class ConsensusUtil {
    
    private ConsensusUtil(){
        throw new RuntimeException("should never be instantiated");
    }
    /**
     * These are the only bases that should be used
     * to consider consensus.
     */
    public static final List<NucleotideGlyph> BASES_TO_CONSIDER = NucleotideGlyph.getGlyphsFor("ACGT-");
    
}
