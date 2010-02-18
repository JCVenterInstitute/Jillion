/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import java.nio.ShortBuffer;
import java.util.List;

public class ShortGlyph extends DefaultNumericGlyph implements Comparable<ShortGlyph>{


    public ShortGlyph(short s){
        super(Short.valueOf(s));
    }
    @Override
    public Short getNumber() {
        return (Short)super.getNumber();
    }
    @Override
    public int compareTo(ShortGlyph o) {
        return getNumber().compareTo(o.getNumber());
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    public static short[] toArray(List<ShortGlyph> shorts){
        ShortBuffer buf = ShortBuffer.allocate(shorts.size());
        for(ShortGlyph aShort : shorts){
            buf.put(aShort.getNumber());
        }
        return buf.array();
    }
}
