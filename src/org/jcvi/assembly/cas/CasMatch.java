/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.util.List;

import org.jcvi.assembly.cas.alignment.CasAlignment;

public interface CasMatch {

    boolean matchReported();
    boolean readHasMutlipleMatches();
    boolean hasMultipleAlignments();
    boolean readIsPartOfAPair();
    List<CasAlignment> getAlignments();
    
}
