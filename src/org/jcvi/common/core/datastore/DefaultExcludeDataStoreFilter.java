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

package org.jcvi.common.core.datastore;

import java.util.Collection;

/**
 * {@code DefaultExcludeDataStoreFilter} is a default implementation
 * of a {@link DataStoreFilter} where all the given ids
 * should NOT be accepted by the filter.
 * 
 * @author dkatzel
 *
 *
 */
public class DefaultExcludeDataStoreFilter implements DataStoreFilter{

    private final DataStoreFilter filter;
    /**
     * Create a new ExcludeDataStoreFilter.
     * @param ids this list of ids that should NOT be accepted
     * by this filter.
     */
    public DefaultExcludeDataStoreFilter(Collection<String> ids) {
        //its just easier to invert an include filter
        //rather than copy and paste code
        filter = new InverseDataStoreFilter(
                new DefaultIncludeDataStoreFilter(ids));
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean accept(String id) {
        return filter.accept(id);
    }
    
    
}
