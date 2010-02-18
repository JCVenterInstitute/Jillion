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
