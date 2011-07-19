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

package org.jcvi.common.core.assembly.trim;

import java.io.IOException;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.common.core.util.CloseableIteratorAdapter;
import org.jcvi.common.core.util.EmptyIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class TrimDataStoreUtil {
    /**
     * Returns an {@link TrimDataStore} that
     * contains no data.
     */
    public static final TrimDataStore EMPTY_DATASTORE = new TrimDataStore() {
        
        @Override
        public CloseableIterator<Range> iterator() {
            return CloseableIteratorAdapter.adapt(EmptyIterator.<Range>createEmptyIterator());
        }
        
        @Override
        public void close() throws IOException {
            // no op
            
        }
        
        @Override
        public int size() throws DataStoreException {
            return 0;
        }
        
        @Override
        public CloseableIterator<String> getIds() throws DataStoreException {
            return CloseableIteratorAdapter.adapt(EmptyIterator.<String>createEmptyIterator());
            
        }
        
        @Override
        public Range get(String id) throws DataStoreException {
            return null;
        }
        
        @Override
        public boolean contains(String id) throws DataStoreException {
            return false;
        }

        @Override
        public boolean isClosed() throws DataStoreException {
            //always open
            return true;
        }
    };
}
