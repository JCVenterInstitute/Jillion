/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.util.List;

import org.jcvi.Distance;
import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.TextFileVisitor;
import org.jcvi.sequence.MateOrientation;

public interface Frg2Visitor extends TextFileVisitor{
    
    void visitLibrary(FrgVisitorAction action, 
                        String id,
                        MateOrientation orientation,
                        Distance distance);
    
    void visitFragment(FrgVisitorAction action,
                String fragmentId, 
                String libraryId,
                NucleotideEncodedGlyphs bases,
                EncodedGlyphs<PhredQuality> qualities ,
                Range validRange,
                Range vectorClearRange,
                String source);
    
    void visitLink(FrgVisitorAction action, List<String> fragIds);
}
