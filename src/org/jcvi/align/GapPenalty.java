/*
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

public interface GapPenalty {

    int getNextGapPenalty();
    void reset();
}
