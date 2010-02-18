/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.Range;

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
