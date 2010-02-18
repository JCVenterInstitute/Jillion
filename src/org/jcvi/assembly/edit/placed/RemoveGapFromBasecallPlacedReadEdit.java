/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.placed;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.edit.glyph.EncodedGlyphEdit;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public abstract class RemoveGapFromBasecallPlacedReadEdit<P extends PlacedRead> extends ModifyBaseCallPlacedReadEdit<P> {

    public RemoveGapFromBasecallPlacedReadEdit(
            EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit) {
        super(basecallEdit);
    }

    @Override
    protected Range editValidRange(Range originalValidRange,NucleotideEncodedGlyphs original,
            NucleotideEncodedGlyphs edited) {
        // removing a gap does not change valid range
        return originalValidRange;
    }

    
    

}
