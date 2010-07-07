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

package org.jcvi.datastore;

/**
 * {@code InverseDataStoreFilter} is an implentation of
 * {@link DataStoreFilter} which wraps an existing
 * DataStoreFilter instance and returns the opposite
 * of whatever the wrapped filter would return.
 * @author dkatzel
 *
 *
 */
public class InverseDataStoreFilter implements DataStoreFilter{

    private final DataStoreFilter filter;
    
    
    /**
     * Create a new InverseDataStoreFilter which returns
     * the oppoiste answers as the given filter.
     * @param filter the fitler to invert.
     */
    public  InverseDataStoreFilter(DataStoreFilter filter) {
        this.filter = filter;
    }


    /**
     * Accepts the opposite of what the wrapped
     * filter would have accepted.
     * @return {@code true} if the wrapped filter
     * would have returned {@code false}; {@code false} if the wrapped filter
     * would have returned {@code true}.
     */
    @Override
    public boolean accept(String id) {
        return !filter.accept(id);
    }

}
