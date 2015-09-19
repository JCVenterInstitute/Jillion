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
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
/**
 * {@code RangeableAceTag} is a version of an 
 * {@link AceTag} that maps to a particular location
 * on a genomic element in the assembly.
 * @author dkatzel
 */
public interface RangeableAceTag extends AceTag, Rangeable {
    /**
     * Get the Id of this tag which can refer to the read or contig
     * this tag references.
     * @return a String; never null.
     */
    String getId();
    /**
     * Gapped Range.
     */
    @Override
    Range asRange();
    /**
     * Should this tag be transferred to new
     * assembly if reassembled?
     * @return {@code true} if should <strong>not</strong> be transferred; {@code false}
     * otherwise.
     */
    boolean isTransient();
}
