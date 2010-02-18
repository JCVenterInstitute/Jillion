/*
 * Created on Sep 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import org.jcvi.sequence.Peaks;
import org.jcvi.trace.Trace;

public interface SangerTrace extends Trace {

    Peaks getPeaks();
    /**
     * Get the number of actual trace scan positions
     * in the trace file.  Most Sanger Trace files
     * have about 15,000 trace points.
     * @return 
     */
    int getNumberOfTracePositions();
}
