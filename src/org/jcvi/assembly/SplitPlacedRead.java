/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class SplitPlacedRead extends DefaultPlacedRead{

    private final PlacedRead leftOfOrigin, rightOfOrigin;


    public SplitPlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read,
            long start, SequenceDirection dir, PlacedRead leftOfOrigin, PlacedRead rightOfOrigin) {
        super(read, start, dir);
        this.leftOfOrigin = leftOfOrigin;
        this.rightOfOrigin = rightOfOrigin;
    }
    
    public PlacedRead getLeftOfOrigin() {
        return leftOfOrigin;
    }

    public PlacedRead getRightOfOrigin() {
        return rightOfOrigin;
    }
   
}
