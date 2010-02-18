/*
 * Created on Nov 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;


public class TestOntheFlyAceBestSegmentMap extends AbstractTestAceBestSegmentMap{

    @Override
    protected AceBestSegmentMap createSut(SliceMap sliceMap,
            NucleotideEncodedGlyphs consensus) {
        return new OnTheFlyAceBestSegmentMap(sliceMap, consensus);
    }
   
}
