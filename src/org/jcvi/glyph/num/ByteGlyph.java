/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

public class ByteGlyph extends DefaultNumericGlyph implements Comparable<ByteGlyph>{

    public ByteGlyph(byte b){
        super(Byte.valueOf(b));
    }
    @Override
    public Byte getNumber() {
        return (Byte)super.getNumber();
    }
    @Override
    public int compareTo(ByteGlyph o) {
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
    

}
