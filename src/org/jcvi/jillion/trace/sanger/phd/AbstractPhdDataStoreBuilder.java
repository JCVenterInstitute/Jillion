/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sanger.phd;

import java.util.List;
import java.util.Properties;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.trace.sanger.PositionSequence;


/**
 * {@code AbstractPhdDataStoreBuilder} is an abstract class that adds {@link PhdDataStoreBuilder}
 * methods to an {@link AbstractPhdFileVisitor} this allows a builder to get all the parsing
 * help from AbstractPhdFileVisitor.
 * @author dkatzel
 *
 *
 */
abstract class AbstractPhdDataStoreBuilder extends AbstractPhdFileVisitor implements PhdDataStoreBuilder{
    /**
     * Create a new AbstractPhdDataStoreBuilder
     * that will visit all phd records (no filter).
     */
    public AbstractPhdDataStoreBuilder(){
       super();
    }
    /**
     * Create a new AbstractPhdDataStoreBuilder with the given filter.  Any
     * phd records that are not accepted by this filter will not get
     * the {@link #visitPhd(String, List, List, PositionSequence, Properties, List)}
     * method called on it.
     * @param filter the DataStoreFilter to use; can not be null.
     * @throws NullPointerException if filter is null.
     */
    public AbstractPhdDataStoreBuilder(DataStoreFilter filter){
       super(filter);
    }
}
