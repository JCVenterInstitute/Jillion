/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.PlacedRead;

/**
 *
 * @author dkatzel
 *
 *
 */
public interface  ContigAnalyzer<P extends PlacedRead> {

    ContigAnalysis analyize(ContigCheckerStruct<P> struct);
}
