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
/**
 * 
 */
package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;



/**
 * {@code FastaRecord} is an interface for interacting
 * with a single FASTA record.
 * @param <T> the type of sequence the record uses.
 * @author jsitz
 * @author dkatzel
 */
public interface FastaRecord<S,T extends Sequence<S>> extends Defline{
	 /**
     * Get the Sequence associated with this record.
     * @return a Sequence, never null.
     */
    T getSequence();
    
    /**
     * Get the sequence length.
     * 
     * @implSpec the default implementation
     * of this method calls
     * {@code getSequence().getLength()} but some {@link FastaRecord}
     * implementations may optimize this return value.
     * 
     * @return the length of this record sequence,
     * usually the number of residues or qualities.
     * 
     * @since 5.0
     */
    default long getLength(){
    	return getSequence().getLength();
    }
    /**
     * Two FastaRecords are equal
     * if they both have the same id
     * and the same sequence.
     */
    @Override
    boolean equals(Object o);
    
    @Override
    int hashCode();

    /**
     * Create a new Fasta Record with the same id and comment
     * but only have the sequence be the subsequence given
     * by the passed in Range.
     * @param trimRange the range to trim the sequence to; can not be null.
     * @return a new FastaRecord.
     *
     * @since 5.3.2
     */
    FastaRecord<S,T> trim(Range trimRange);
}
