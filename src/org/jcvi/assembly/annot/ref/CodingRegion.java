/*
 * Created on Dec 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.annot.Exon;

public interface CodingRegion {

    Range getRange();
    CodingRegionState getStartCodingRegionState();
    CodingRegionState getEndCodingRegionState();
    List<Exon> getExons();
}
