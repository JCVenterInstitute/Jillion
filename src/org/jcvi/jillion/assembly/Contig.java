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
 * Created on Sep 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;


import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * A {@code Contig} is a CONTIGuous region of genomic data.
 * Contigs are assembled by overlapping reads to form a consensus.
 * @author dkatzel
 * @param <T> the type of {@link AssembledRead}s which were used to build
 * this contig.
 */
public interface Contig<T extends AssembledRead>{
    /**
     * Get the id of this contig.
     * @return the Id of this contig as a String; will never be null.
     */
    String getId();
    /**
     * Get the number of reads in this contig.
     * @return the number of reads in this contig; will always be &ge; 0.
     */
    long getNumberOfReads();
    /**
     * Get the {@link StreamingIterator} of {@link AssembledRead}s
     * that are contained in this contig. 
     * @return a {@link StreamingIterator}  of {@link AssembledRead}s; will never be null 
     * and never contain any null elements,
     * but could be empty.
     */
    StreamingIterator<T> getReadIterator();
    /**
     * Get the consensus sequence of this contig.  The Consensus
     * is determined by the underlying reads that make up this contig.  Different
     * consensus callers can create different consensus using various criteria
     * and paramters.
     * @return the consensus of this contig as {@link NucleotideSequence}; will
     * never be null.
     * @see org.jcvi.jillion.assembly.util.consensus.ConsensusCaller
     */
    NucleotideSequence getConsensusSequence();
    /**
     * Get the {@link AssembledRead} in this contig with the given id.
     * @param id the id of the read to get.
     * @return the {@link AssembledRead} with that id; or {@code null}
     * if no such read exists in this contig.
     * @see #containsRead(String)
     */
    T getRead(String id);
    /**
     * Does this contig have a {@link AssembledRead} with the given id?
     * @param readId the id of the {@link AssembledRead} to check for.
     * @return {@code true} if this contig has a read
     * with the given id; {@code false} otherwise.
     */
    boolean containsRead(String readId);
    /**
     * Create a {@link Stream} of the {@link AssembledRead}s
     * in this contig.  By default, this method
     * delegates to {@link #getReadIterator()}'s {@link StreamingIterator#toThrowingStream()}
     * method.
     * @return a new ThrowingStream of reads.
     * @since 5.0
     * 
     * @apiNote Jillion Version 5.3 changed the return type to be {@link ThrowingStream} instead of Stream.
     */
    default ThrowingStream<T> reads(){
    	return getReadIterator().toThrowingStream();
    }
}
