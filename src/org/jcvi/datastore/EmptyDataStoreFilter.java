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
 * {@code EmptyDataStoreFilter} is an implementation of
 * {@link DataStoreFilter} which 
 * @author dkatzel
 *
 *
 */
public final class EmptyDataStoreFilter implements DataStoreFilter{
    /**
     * This static singleton is the instance of EmptyDataStoreFilter
     * that should be used.
     */
    public static final EmptyDataStoreFilter INSTANCE = new EmptyDataStoreFilter();
    /**
     * Private constructor.
     */
    private EmptyDataStoreFilter(){};
    /**
    * Every id is always accepted.
    * @return {@code true}.
    */
    @Override
    public boolean accept(String id) {
        return true;
    }

}
