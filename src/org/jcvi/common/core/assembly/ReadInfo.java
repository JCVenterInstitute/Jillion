package org.jcvi.common.core.assembly;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public interface ReadInfo {

	/**
     * Get the valid {@link Range} which is ungapped "good" part of the basecalls.  Depending
     * on what this {@link NucleotideSequence} represents can change the 
     * meaning of valid range some possible meanings include:
     * <ul>
     * <li>the high quality region<li>
     * <li>the region that aligns to a reference</li>
     * <li>the region used to compute assembly consensus</li>
     * </ul>
     * The maximum possible valid range length is the length
     * returned by {@link #getUngappedFullLength()}.
     * @return a Range with a minimum (zero-based) begin value of 0 
     * and a max (zero-based) end value of {@link #getUngappedFullLength()} -1.
     */
    Range getValidRange();
    
    /**
     * Get the ungapped full length of this read <strong>including bases outside of the valid range</strong>.
     * If this read has any portion of the read that was trimmed off because of bad quality, primer/vector sequence
     * or because it did not fully align to the reference then those portions still counted by this method.
     * @return the full length including bases outside of the valid range; always positive.
     */
    int getUngappedFullLength();
}
