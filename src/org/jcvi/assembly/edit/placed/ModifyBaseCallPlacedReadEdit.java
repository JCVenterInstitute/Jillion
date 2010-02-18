/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.placed;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.edit.EditException;
import org.jcvi.assembly.edit.glyph.EncodedGlyphEdit;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public abstract class ModifyBaseCallPlacedReadEdit<P extends PlacedRead> extends AbstractPlacedReadEdit<P> {

    private final EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit;

    /**
     * @param basecallEdit
     */
    public ModifyBaseCallPlacedReadEdit(
            EncodedGlyphEdit<NucleotideGlyph, NucleotideEncodedGlyphs> basecallEdit) {
        this.basecallEdit = basecallEdit;
    }

    @Override
    public P performEdit(P original) throws EditException {
        final NucleotideEncodedGlyphs originalBasecalls = original.getEncodedGlyphs();
        NucleotideEncodedGlyphs editedBasecalls = basecallEdit.performEdit(originalBasecalls);
        Range editedValidRange = editValidRange(original.getValidRange(),originalBasecalls,editedBasecalls);
        
        return createNewPlacedRead(editedBasecalls, 
                original.getStart(), 
                editedValidRange, 
                original.getSequenceDirection());
    }

    protected abstract Range editValidRange(Range originalValidRange,NucleotideEncodedGlyphs original, NucleotideEncodedGlyphs edited);
}
