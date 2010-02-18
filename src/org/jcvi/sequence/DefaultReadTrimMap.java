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
package org.jcvi.sequence;

import java.util.HashMap;
import java.util.Map;


public class DefaultReadTrimMap implements ReadTrimMap{
    private final Map<String, ReadTrim> map;
    
    
    /**
     * @param map
     */
    private DefaultReadTrimMap(Map<String, ReadTrim> map) {
        this.map = map;
    }


    @Override
    public ReadTrim getReadTrimFor(String id) {
        return map.get(id);
    }
    
    public static class Builder implements org.jcvi.Builder<DefaultReadTrimMap>{
        private final Map<String, ReadTrim> map = new HashMap<String, ReadTrim>();
        
        public Builder addReadTrim(String id, ReadTrim trim){
            map.put(id, trim);
            return this;
        }
        @Override
        public DefaultReadTrimMap build() {
            return new DefaultReadTrimMap(map);
        }
        
    }

}
