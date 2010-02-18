/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.List;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;

public class PrecomputedReferenceEncodedNucleotideGlyphs extends AbstractReferenceEncodedNucleotideGlyphs{
    private final EncodedGlyphs<NucleotideGlyph> reference;
    public PrecomputedReferenceEncodedNucleotideGlyphs(EncodedGlyphs<NucleotideGlyph> reference,
            Map<Integer, NucleotideGlyph> differentGlyphMap,
            List<Integer> gaps, int startOffset, int length, Range validRange) {
        super(differentGlyphMap, gaps, startOffset, length, validRange);
        this.reference = reference;
    }

    @Override
    protected NucleotideGlyph getFromReference(int referenceIndex) {
        return reference.get(referenceIndex);
    }

}
