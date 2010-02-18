/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

import java.util.List;

public interface CasAlignment {

    long contigSequenceId();
    long getStartOfMatch();
    boolean readIsReversed();
    
    List<CasAlignmentRegion> getAlignmentRegions();
}
