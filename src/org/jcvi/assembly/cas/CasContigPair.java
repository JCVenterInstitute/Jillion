/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

public interface CasContigPair {

    int getMinDistanceBetweenContigs();
    int getMaxDistanceBetweenContigs();
    
    int getFirstContigId();
    int getSecondContigId();
    
    boolean areContigsReversed();
}
