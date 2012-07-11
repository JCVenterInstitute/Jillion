/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.ace;

import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code AceFileContigDataStore} is a {@link ContigDataStore}
 * for {@link AceContig}s that are contained in a single .ace file.
 * 
 * @author dkatzel
 *
 *
 */
public interface AceFileContigDataStore extends ContigDataStore<AcePlacedRead,AceContig>{
	/**
	 * Get the total number of reads over all the contigs
	 * in the datastore.  This number may be different
	 * than the total number stated in the header of the ace file
	 * for two reasons:
	 * <ol>
	 * <li>Some contigs may be filtered out of this datastore depending
	 * on the implementation and parameters passed to the factory method.</li>
	 * <li>Some reads in a contig may be excluded because they are either invalid
	 * or don't provide any coverage</li>
	 * </ol>
	 * @return the total number of 
	 * reads in all the contigs in this datastore, will always be >= 0.
	 */
	long getNumberOfTotalReads() throws DataStoreException;
	 /**
     * Get a new instance of a {@link CloseableIterator}
     * of all the {@link WholeAssemblyAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link CloseableIterator}; never null.
     */
	CloseableIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() throws DataStoreException;
	/**
     * Get a new instance of a {@link CloseableIterator}
     * of all the {@link ReadAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link CloseableIterator}; never null.
     */
	CloseableIterator<ReadAceTag> getReadTagIterator() throws DataStoreException;
	/**
     * Get a new instance of a {@link CloseableIterator}
     * of all the {@link ConsensusAceTag}s
     * in the ace file in the order they are
     * declared in the file.
     * @return a new {@link CloseableIterator}; never null.
     */
	CloseableIterator<ConsensusAceTag> getConsensusTagIterator()  throws DataStoreException;
}
