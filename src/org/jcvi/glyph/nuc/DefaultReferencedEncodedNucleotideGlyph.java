/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
/**
 * <code>ReferenceEncodedNucleotideGlyph</code> is a 
 * glyph encoding that only stores the differences
 * to a reference.  This should keep the memory footprint
 * quite low since an underlying sequence should map to a reference 
 * with a high identity.  If the reference is the consensus,
 * the underlying sequence should map more than 98%.
 * @author dkatzel
 *
 *
 */
public class DefaultReferencedEncodedNucleotideGlyph extends AbstractReferenceEncodedNucleotideGlyphs{

    private final EncodedGlyphs<NucleotideGlyph> reference;

    
    /**
     * Constructor.
     * @param reference the reference to map against.
     * @param toBeEncoded glyphs to be encoded.
     * @param startOffset the startOffset in the reference where
     * this sequence starts.
     */
    public DefaultReferencedEncodedNucleotideGlyph(EncodedGlyphs<NucleotideGlyph> reference,
            String toBeEncoded, int startOffset, Range validRange){
        super(reference, toBeEncoded, startOffset, validRange);
        this.reference = reference;
    }

    @Override
    protected NucleotideGlyph getFromReference(int referenceIndex) {
        return reference.get(referenceIndex);
    }

}
