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
 * Created on Mar 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.trace.Trace;
/**
 * {@code Phd} is an object representation
 * of a single read from a phd file or a phd.ball file.
 * @author dkatzel
 *
 */
public interface Phd extends Trace {

	/**
	 * Get the optional comments associated
	 * with this phd.
	 * @return a {@link Map} where
	 * the keys and values are both 
	 * Strings; if this phd does not
	 * contain any comments, then
	 * this method returns an empty map.
	 * Will never return null.
	 */
    Map<String,String> getComments();
    /**
	 * Get the optional whole read items associated
	 * with this phd that are contained in the phd file.
	 * @return a {@link List} of {@link PhdWholeReadItem}s
	 * if this phd does not
	 * contain any whole read items, then
	 * this method returns an empty list.
	 * Will never return null.
	 */
    List<PhdWholeReadItem> getWholeReadItems();
    /**
   	 * Get the optional whole read tags associated
   	 * with this phd that are contained in the phd file.
   	 * @return a {@link List} of {@link PhdReadTag}s
   	 * if this phd does not
   	 * contain any whole read items, then
   	 * this method returns an empty list.
   	 * Will never return null.
   	 */
    List<PhdReadTag> getReadTags();
    /**
     * Get the optional {@link PositionSequence}
     * representing the sanger peaks for this Phd.  Recent versions of consed
     * allow the peak sequence to be optional
     * if the sequence technology does not support it
     * (previous versions of consed forced the positions
     * to be faked).  For example, solexa sequences
     * do not have peaks, however sanger chromatograms
     * do.
     * @return the {@link PositionSequence} for this
     * phd if there is one; or null
     * if no peak sequence exists
     * for this trace.
     */
    PositionSequence getPeakSequence();
}
