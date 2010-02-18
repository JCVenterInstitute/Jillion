/*
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
/**
 * {@code ConsensusResult} is the base call
 * and quality value that best represents
 * a particular {@link Slice} in a Contig.
 * 
 * @author dkatzel
 *
 *
 */
public interface ConsensusResult {
    /**
     * The best {@link NucleotideGlyph}
     * represented by the Slice.
     * @return a {@link NucleotideGlyph} will never be null.
     */
    NucleotideGlyph getConsensus();
    /**
     * Return the quality of the consensus.  This number may be
     * in the hundreds or thousands depending on the depth of
     * coverage.
     * @return an int; will always be {@code >= 0}
     */
    int getConsensusQuality();
}
