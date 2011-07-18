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
 * {@code IncludeFastXIdFilter} is an implementation of
 * {@link AbstractFastXIdFilter} that includes
 * any records in the provided list.
 * @author dkatzel
 *
 *
 */
public class IncludeFastXIdFilter extends AbstractFastXIdFilter{
    /**
     * Include any records which have ids contained in the given list.
     * @param ids the list of ids to exclude.
     */
    public IncludeFastXIdFilter(Collection<String> ids) {
        super(ids);
    }

    @Override
    protected boolean accept(boolean idContainedInList) {
        return idContainedInList;
    }

}
