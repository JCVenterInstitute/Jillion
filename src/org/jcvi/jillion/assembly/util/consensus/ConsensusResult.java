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
 * Created on Dec 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code ConsensusResult} is the base call
 * and quality value that best represents
 * a particular {@link Slice} in a Contig.
 * 
 * @author dkatzel
 *
 *
 */
public interface ConsensusResult {
    /**
     * The best {@link Nucleotide}
     * represented by the Slice.
     * @return a {@link Nucleotide} will never be null.
     */
    Nucleotide getConsensus();
    /**
     * Return the quality of the consensus.  This number may be
     * in the hundreds or thousands depending on the depth of
     * coverage.
     * @return an int; will usually be {@code >= 0}
     * but may be negative depending on the implementation
     * and what is contained in the Slice.
     */
    int getConsensusQuality();
}
