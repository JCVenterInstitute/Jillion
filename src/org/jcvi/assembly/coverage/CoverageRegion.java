/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.util.Collection;

import org.jcvi.assembly.Placed;

public interface CoverageRegion<T extends Placed> extends Placed {
        /**
         * Get the Coverage depth of this coverage region.
         * should be the same as the number of elements.
         * @return
         */
        int getCoverage();
        /**
         * Get the elements in this coverage region
         * @return
         */
        Collection<T> getElements();
        /**
         * Create a new CoverageRegion which has
         * the same elements as this CoverageRegion
         * but whose coordinates
         * have been shifted to the left the specified number
         * of units.
         * @param units
         * @return a new CoverageRegion 
         */
        CoverageRegion<T> shiftLeft(int units);
        /**
         * Create a new CoverageRegion which has
         * the same elements as this CoverageRegion
         * but whose coordinates
         * have been shifted to the left the specified number
         * of units.
         * @param units
         * @return a new CoverageRegion 
         */
        CoverageRegion<T> shiftRight(int units);
}
