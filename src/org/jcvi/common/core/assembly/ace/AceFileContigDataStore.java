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
 * @author dkatzel
 *
 *
 */
public interface AceFileContigDataStore extends ContigDataStore<AcePlacedRead,AceContig>{
	/**
	 * Get the total number of reads over all the contigs
	 * in the datastore.
	 * @return
	 */
	long getNumberOfTotalReads() throws DataStoreException;
	
	CloseableIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() throws DataStoreException;
	
	CloseableIterator<ReadAceTag> getReadTagIterator() throws DataStoreException;
	
	CloseableIterator<ConsensusAceTag> getConsensusTagIterator()  throws DataStoreException;
}
