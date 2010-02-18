/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.mate;

import java.util.Map;

public class DefaultMapMap implements MateMap {
    private final Map<String, String> mates;
    public DefaultMapMap(Map<String, String> mates){
        this.mates = mates;
    }
    @Override
    public String getMateOf(String id) {
        return mates.get(id);
    }

}
