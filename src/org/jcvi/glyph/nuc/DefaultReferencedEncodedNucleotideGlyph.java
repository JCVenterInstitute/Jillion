/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.jcvi.Range;
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
public class DefaultReferencedEncodedNucleotideGlyph extends AbstractReferenceEncodedNucleotideGlyphs implements ReferencedEncodedNucleotideGlyphs{

    private final NucleotideEncodedGlyphs reference;

    
    /**
     * Constructor.
     * @param reference the reference to map against.
     * @param toBeEncoded glyphs to be encoded.
     * @param startOffset the startOffset in the reference where
     * this sequence starts.
     */
    public DefaultReferencedEncodedNucleotideGlyph(NucleotideEncodedGlyphs reference,
            String toBeEncoded, int startOffset, Range validRange){
        super(reference, toBeEncoded, startOffset, validRange);
        this.reference = reference;
    }

    @Override
    protected NucleotideGlyph getFromReference(int referenceIndex) {
        return reference.get(referenceIndex);
    }

    

}
