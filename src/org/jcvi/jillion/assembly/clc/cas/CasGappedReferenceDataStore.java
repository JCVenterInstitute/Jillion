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
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
/**
 * {@code CasGappedReferenceDataStore} is a {@link NucleotideSequenceDataStore}
 * that stores the gapped reference sequences that were computed from a cas file
 * along with a mapping from the internal cas file reference indexes to the actual
 * reference ids from the input reference file.
 * 
 * @author dkatzel
 * @see CasGappedReferenceDataStoreBuilderVisitor
 */
public interface CasGappedReferenceDataStore extends NucleotideSequenceDataStore{
	/**
	 * This method get the {@link NucleotideSequence} for the gapped assembled reference
	 * by the cas internal reference index.
	 * The cas file does not use the reference ids from the input reference file(s),
	 * instead it uses an internal numbering scheme to keep the filesize as small as possible.
	 * 
	 * @param index the cas internal reference index to use;
	 * should be >=0.
	 * @return the {@link NucleotideSequence} for that reference index;
	 * or {@code null} if the index does not exist.
	 * @throws DataStoreException if there was a problem getting the sequence
	 * from the datastore.
	 */
	NucleotideSequence getReferenceByIndex(long index) throws DataStoreException;
	/**
	 * Get the real reference id that is used to identify the reference sequence in the 
	 * original input into the CLC assembler.
	 * @param index index the cas internal reference index to use;
	 * should be >=0.
	 * @return the String id for that reference index;
	 * or {@code null} if the index does not exist.
	 */
	String getIdByIndex(long index);
	/**
	 * Get the cas internal reference index from the 
	 * real reference id that is used to identify the reference sequence in the 
	 * original input into the CLC assembler.
	 * @param id the reference id from the input file(s) given to the CLC assembler.
	 * @return the cas internal reference index as a long
	 * should be >=0; or null if that id does not exist in the datastore.
	 */
	Long getIndexById(String id);
}
