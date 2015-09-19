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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.Map;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * A {@code Slice} is a one base wide vertical cut of an assembly containing zero 
 * or more {@link SliceElement}s.
 * @author dkatzel
 *
 *
 */
public interface Slice extends Iterable<SliceElement>{
    /**
     * Get the coverage depth of this Slice.
     * @return the coverage depth of this slice, will
     * always be {@code >= 0}.
     */
    int getCoverageDepth();
    
	/**
     * Does this {@link Slice} contain a 
     * {@link SliceElement} with the given id.
     * @param elementId the id of the {@link SliceElement} being queried.
     * @return {@code true} if this Slice does contain
     * a {@link SliceElement} with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link SliceElement} if exists; or {@code null}
     * if there is no {@link SliceElement} for this Slice with that id.
     */
    SliceElement getSliceElement(String elementId);
    /**
     * Get a Mapping of how many of each {@link Nucleotide}
     * is present in this slice.  
     * @return a new {@link Map} containing just
     * the {@link Nucleotide}s counts for this slice
     * if a Nucleotide is not present in this slice,
     * then the Map will contain that key with a value of 0.
     * If {@link #getCoverageDepth()} ==0 then all the Nucletoides
     * will have values of 0.
     * Will never return null, 
     */
    Map<Nucleotide, Integer> getNucleotideCounts();
    /**
     * Optional consensus of this slice.  If present,
     * this is what the consensus call of this Slice
     * was previously called.  If this Slice gets its
     * consensus recalled using a 
     * {@link org.jcvi.jillion.assembly.util.consensus.ConsensusCaller}
     * then this value will <strong>NOT</strong> be changed.
     * If the consensus for this slice is not available
     * or does not exist yet, then this will return {@code null}.
     * @return the {@link Nucleotide} consensus call for
     * this slice if present, or {@code null}
     * if the consensus is not available or does not
     * exist.
     */
    Nucleotide getConsensusCall();
   
}
