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
 * {@code DefaultIncludeDataStoreFilter} is a default implementation
 * of a DataStoreFilter in which all the given ids should be 
 * accepted by this filter.
 * @author dkatzel
 *
 *
 */
public class DefaultIncludeDataStoreFilter implements DataStoreFilter{

    private final Collection<String> ids;

    /**
     * Create a new IncludeDataStoreFilter.
     * @param ids this list of ids that should be accepted
     * by this filter.
     */
    public DefaultIncludeDataStoreFilter(Collection<String> ids) {
        this.ids = ids;
    }

    @Override
    public boolean accept(String id) {
        return ids.contains(id);
    }
}
