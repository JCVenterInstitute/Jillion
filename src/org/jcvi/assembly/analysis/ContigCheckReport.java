/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;

public interface ContigCheckReport<P extends PlacedRead> {

    Contig<P> getContig();
    List<AnalysisIssue> getAnalysisIssues();
}
