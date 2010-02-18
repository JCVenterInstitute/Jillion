/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;

public class DefaultSliceElement implements SliceElement {
    private final NucleotideGlyph base;
    private final PhredQuality quality;
    private final SequenceDirection direction;
    private final String name;
    /**
     * @param name
     * @param base
     * @param quality
     * @param direction
     * @throws IllegalArgumentException if any parameter is null.
     */
    public DefaultSliceElement(String name, NucleotideGlyph base, PhredQuality quality,
            SequenceDirection direction) {
        if(name ==null ||base ==null || quality ==null || direction == null){
            throw new IllegalArgumentException("fields can not be null");
        }
        this.name = name;
        this.base = base;
        this.quality = quality;
        this.direction = direction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + base.hashCode();
        result = prime * result
                + direction.hashCode();
        result = prime * result + quality.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SliceElement)){
            return false;
        }
        SliceElement other = (SliceElement) obj;
        return 
        CommonUtil.similarTo(getName(), other.getName()) &&
        CommonUtil.similarTo(getBase(), other.getBase()) &&
        CommonUtil.similarTo(getQuality(), other.getQuality()) &&
        CommonUtil.similarTo(getSequenceDirection(), other.getSequenceDirection());
       
    }

    @Override
    public NucleotideGlyph getBase() {
        return base;
    }

    @Override
    public PhredQuality getQuality() {
        return quality;
    }

    @Override
    public SequenceDirection getSequenceDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("name %s %s (%d) %s",
                getName(),
                base.getName(),
                quality.getNumber(),
                direction);
        
    }

    @Override
    public String getName() {
        return name;
    }

    
}
