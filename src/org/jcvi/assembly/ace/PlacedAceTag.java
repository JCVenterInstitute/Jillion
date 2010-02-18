/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.assembly.Placed;

public interface PlacedAceTag extends AceTag, Placed {

    String getId();
    /**
     * Gapped Start offset.
     */
    @Override
    long getStart();
    /**
     * Gapped End Offset.
     */
    @Override
    long getEnd();
    /**
     * Gapped length.
     */
    @Override
    long getLength();
    /**
     * Should this tag be transferred to new
     * assembly if reassembled?
     * @return {@code true} if should Not be transferred; {@code false}
     * otherwise.
     */
    boolean isTransient();
}
