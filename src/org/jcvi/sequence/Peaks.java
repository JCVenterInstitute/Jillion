/*
 * Created on Oct 24, 2007
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.nio.ShortBuffer;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.DefaultShortGlyphCodec;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;


/**
 * <code>Peaks</code> contains the position
 * data of each peak in a <code>Trace</code>.
 *
 * @author dkatzel
 *
 *
 */
public class Peaks{
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private static final DefaultShortGlyphCodec CODEC = DefaultShortGlyphCodec.getInstance();
    private EncodedGlyphs<ShortGlyph> data;

    public Peaks(short[] data){
        this(FACTORY.getGlyphsFor(data));
       
    }
    public Peaks(List<ShortGlyph> data){
        this.data = new DefaultEncodedGlyphs<ShortGlyph>(CODEC, data);
       
    }
    /**
     * @param data
     */
    public Peaks(ShortBuffer data) {
        this(data.array());
    }

    /**
     * @return the data
     */
    public EncodedGlyphs<ShortGlyph> getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
       if(obj == this){
           return true;
       }
       if(!(obj instanceof Peaks)){
           return false;
       }
       Peaks other = (Peaks) obj;
       return CommonUtil.bothNull(getData(), other.getData())  
                   ||        
            (!CommonUtil.onlyOneIsNull(getData(), other.getData()) 
                   && 
            CommonUtil.similarTo(getData(), other.getData()));
       
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getData() == null) ? 0 :getData().hashCode());

        return result;
    }

}
