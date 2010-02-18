/*
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.phredQuality;

import org.jcvi.glyph.num.AbstractByteGlyphCodec;

public class DefaultEncodedPhredGlyphCodec extends AbstractByteGlyphCodec<PhredQuality>{

    @Override
    protected PhredQuality getValueOf(byte b) {
        return PhredQuality.valueOf(b);
    }
}
