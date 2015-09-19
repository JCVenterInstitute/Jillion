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

import org.jcvi.jillion.core.Sequence;



/**
 * {@code FastaRecord} is an interface for interacting
 * with a single FASTA record.
 * @param <T> the type of sequence the record uses.
 * @author jsitz
 * @author dkatzel
 */
public interface FastaRecord<S,T extends Sequence<S>>
{

	 /**
     * Get the Id of this record.
     * @return A <code>String</code>.
     */
    String getId();

    /**
     * Get the comment (if any) associated with this record.
     * @return A <code>String</code> of the comment
     * or {@code null} if there is no comment.
     */
    String getComment();
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
}
