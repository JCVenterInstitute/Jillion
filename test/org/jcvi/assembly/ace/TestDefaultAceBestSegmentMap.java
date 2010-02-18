/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;



import org.jcvi.assembly.slice.SliceMap;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class TestDefaultAceBestSegmentMap extends AbstractTestAceBestSegmentMap{

    @Override
    protected AceBestSegmentMap createSut(SliceMap sliceMap,
            NucleotideEncodedGlyphs consensus) {
        return new DefaultAceBestSegmentMap(sliceMap, consensus);
    }

   
    
}
