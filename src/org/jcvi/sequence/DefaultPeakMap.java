/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.util.Map;

import org.jcvi.glyph.num.EncodedShortGlyph;

public class DefaultPeakMap implements PeakMap{

    private final Map<String, EncodedShortGlyph> map;
    
    public DefaultPeakMap(Map<String, EncodedShortGlyph> map){
        this.map = map;
    }

    @Override
    public EncodedShortGlyph getPeaksFor(String id) {
        return map.get(id);
    }
}
