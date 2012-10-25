/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.common.core.seq.fastx;

import java.util.Collection;

/**
 * {@code AbstractFastXIdFilter} is an abstract implementation
 * of {@link FastXFilter} to accept or not accept
 * fastX records based on def lines.
 * @author dkatzel
 *
 *
 */
abstract class AbstractFastXIdFilter implements FastXFilter{

    private final Collection<String> ids;

    /**
     * @param ids
     */
    public AbstractFastXIdFilter(Collection<String> ids) {
        this.ids = ids;
    }
    /**
     * Should the given Id be accepted?
     * @param idContainedInList is the id contained in the list
     * provided.
     * @return {@code true} if the id should be accepted; {@code false}
     * otherwise.
     */
    protected abstract boolean accept(boolean idContainedInList);

    
    /**
     * Same as {@link #accept(String, String) accept(id, null)}
     * @see #accept(String, String)
     */
    @Override
    public boolean accept(String id) {
        return accept(id,null);
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public boolean accept(String id, String optionalComment) {
        return accept(ids.contains(id));
    }
    
    
}
