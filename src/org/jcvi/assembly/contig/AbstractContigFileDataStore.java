/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractContigFileDataStore extends AbstractContigFileVisitor{

    private DefaultContig.Builder currentContigBuilder;
    
    protected abstract void  addContig(Contig contig);

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, SequenceDirection dir) {
        currentContigBuilder.addRead(readId, offset, validRange,basecalls,dir); 
        
        
    }

    @Override
    protected void visitEndOfContig() {
        addContig(currentContigBuilder.build());
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        currentContigBuilder = new DefaultContig.Builder(contigId,
                encodeBasecalls(consensus.toString()));
    }
    private DefaultNucleotideEncodedGlyphs encodeBasecalls(String basecalls) {
        return new DefaultNucleotideEncodedGlyphs(NucleotideGlyph.getGlyphsFor(basecalls), Range.buildRange(0, basecalls.length()));
    }
}
