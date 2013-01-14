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

package org.jcvi.jillion.core.datastore;

/**
 * {@code DataStoreFilter} is a filter that can be applied
 * to a DataStore to only allow certain Datastore ids.
 * @author dkatzel
 *
 *
 */
public interface DataStoreFilter {
    /**
     * Is the given id accepted by the filter.
     * @param id the id to check.
     * @return {@code true} if the id should be accepted
     * by the filter {@code false} otherwise.
     */
    boolean accept(String id);
}
