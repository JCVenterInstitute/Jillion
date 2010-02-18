/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;


public class DefaultNumericGlyph implements NumericGlyph{
    private final Number number;
    DefaultNumericGlyph(Number number){
        if(number ==null){
            throw new IllegalArgumentException("number can not be null");
        }
        this.number = number;
    }
    @Override
    public Number getNumber() {
        return number;
    }


    @Override
    public String getName() {
        return number.toString();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Long.valueOf(number.longValue()).hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultNumericGlyph)){
            return false;
        }
        DefaultNumericGlyph other = (DefaultNumericGlyph) obj;
       return number.longValue()==other.number.longValue();
    }
    @Override
    public String toString() {
        return getNumber().toString();
    }
    
    
    
}
