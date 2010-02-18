/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class DefaultRead implements Read{
    private final String id;
    private final NucleotideEncodedGlyphs glyphs;
    public DefaultRead(String id, NucleotideEncodedGlyphs glyphs){
        this.id= id;
        this.glyphs = glyphs;
    }
    @Override
    public NucleotideEncodedGlyphs getEncodedGlyphs() {
        return glyphs;
    }

    @Override
    public String getId() {
        return id;
    }
    @Override
    public long getLength() {
        return glyphs.getLength();
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    /**
     * Two Reads are equal if they have the same id.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Read))
            return false;
        Read other = (Read) obj;
        return CommonUtil.similarTo(id, other.getId());
        
    }
    @Override
    public String toString() {
        return "read : " + getId() + "  validRange" + getEncodedGlyphs().getValidRange()+"  " + getEncodedGlyphs().decode().toString();
    }

    

}
