/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.placed;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.edit.glyph.EncodedGlyphEdit;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public abstract class RemoveNonGapFromBasecallsPlacedReadEdit<P extends PlacedRead> extends ModifyBaseCallPlacedReadEdit<P> {

    public RemoveNonGapFromBasecallsPlacedReadEdit(
            EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit) {
        super(basecallEdit);
    }

    @Override
    protected Range editValidRange(Range originalValidRange,
            NucleotideEncodedGlyphs original, NucleotideEncodedGlyphs edited) {
        long differenceInSize = original.getLength() - edited.getLength();
        return Range.buildRange(originalValidRange.getStart(), 
                originalValidRange.getEnd()-differenceInSize)
                .convertRange(CoordinateSystem.RESIDUE_BASED);
    }

    
}
