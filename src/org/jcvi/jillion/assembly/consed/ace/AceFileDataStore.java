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
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.assembly.ContigDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * {@code AceFileDataStore} is a {@link ContigDataStore}
 * for {@link AceContig}s that are contained in a single .ace file.
 * 
 * @author dkatzel
 *
 *
 */
public interface AceFileDataStore extends ContigDataStore<AceAssembledRead,AceContig>{
	/**
	 * Get the total number of reads over all the contigs
	 * in the datastore.  This number may be different
	 * than the total number stated in the header of the ace file
	 * for two reasons:
	 * <ol>
	 * <li>Some contigs may be filtered out of this datastore depending
	 * on the parameters used when constructing this {@link AceFileDataStore}
	 * instance.</li>
	 * <li>Some reads in a contig may be excluded because they are either invalid
	 * or don't provide any coverage</li>
	 * </ol>
	 * @return the total number of 
	 * reads in all the contigs in this datastore, will always be &ge; 0.
	 * 
	 * @throws DataStoreException if there is a problem reading the 
	 * data from this datastore.
	 */
	long getNumberOfTotalReads() throws DataStoreException;
	 /**
     * Get a new instance of a {@link StreamingIterator}
     * of all the {@link WholeAssemblyAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link StreamingIterator}; never null.
     * 
     * @throws DataStoreException if there is a problem reading the 
	 * data from this datastore.
     */
	StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() throws DataStoreException;
	/**
     * Get a new instance of a {@link StreamingIterator}
     * of all the {@link ReadAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link StreamingIterator}; never null.
     * 
     * @throws DataStoreException if there is a problem reading the 
	 * data from this datastore.
     */
	StreamingIterator<ReadAceTag> getReadTagIterator() throws DataStoreException;
	/**
     * Get a new instance of a {@link StreamingIterator}
     * of all the {@link ConsensusAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link StreamingIterator}; never null.
     * 
     * @throws DataStoreException if there is a problem reading the 
	 * data from this datastore.
     */
	StreamingIterator<ConsensusAceTag> getConsensusTagIterator()  throws DataStoreException;
}
