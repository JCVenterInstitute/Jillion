/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat;

import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.trace.Trace;
/**
 * {@code Chromatogram} is an interface
 * for SangerTrace objects that also contains
 * {@link ChannelGroup} data and optional
 * comments generated from the sequencing machine.
 * @author dkatzel
 *
 *
 */
public interface Chromatogram extends Trace{
	/**
     * Get the {@link PositionSequence}
     * of the peak positions from the trace scan positions
     * in the trace file.  The peaks are the positions
     * that the basecaller determined are the scan points
     * positions that are likely to be the bases.
     * @return a {@link PositionSequence}; will never
     * be null.
     */
	PositionSequence getPeakSequence();
    /**
     * Get the number of actual trace scan positions
     * in the trace file.  Most Sanger Trace files
     * have about 15,000 trace points.
     * @return the number of trace position in this chromatogram.
     * 
     */
    int getNumberOfTracePositions();
    /**
     * Get the {@link ChannelGroup} of this Chroamtogram.
     * @return a ChannelGroup, never null.
     */
    ChannelGroup getChannelGroup();
    /**
     * Get the key-value pair comments associated with this chromatogram.
     * @return a Map of comments by key, may be empty but will
     * never be null.
     */
    Map<String,String> getComments();

}
