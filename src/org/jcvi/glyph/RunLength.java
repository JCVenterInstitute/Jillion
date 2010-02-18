/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import org.jcvi.CommonUtil;

public class RunLength<T> {
    private final int length;
    private final  T value;
    /**
     * @param length
     * @param value
     */
    public RunLength(T value,int length) {
        this.length = length;
        this.value = value;
    }
    public int getLength() {
        return length;
    }
    public T getValue() {
        return value;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RunLength))
            return false;
        RunLength other = (RunLength) obj;
        return length == other.length && CommonUtil.similarTo(getValue(), other.getValue());
    }
    @Override
    public String toString() {
       StringBuilder builder = new StringBuilder();
       builder.append(value)
               .append("x ")
               .append(getLength());
        return builder.toString();
    }

}