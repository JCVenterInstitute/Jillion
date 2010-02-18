/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.alignment;

public interface CasAlignmentRegion {

    CasAlignmentRegionType getType();
    
    long getLength();
}
