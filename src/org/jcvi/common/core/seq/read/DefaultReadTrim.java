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
/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.Range;

public class DefaultReadTrim implements ReadTrim {

    private final String id;
    private final Map<TrimType, Range> trimRanges = new EnumMap<TrimType, Range>(TrimType.class);
    
    public DefaultReadTrim(String id, Map<TrimType, Range> trimRanges){
        this.id = id;
        for(Entry<TrimType, Range> entry : trimRanges.entrySet()){
            this.trimRanges.put(entry.getKey(), entry.getValue());
        }
    }
    @Override
    public String getReadId() {
        return id;
    }

    @Override
    public Range getTrimRange(TrimType trimType) {
        return trimRanges.get(trimType);
    }

}
