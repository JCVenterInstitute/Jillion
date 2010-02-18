/*
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jcvi.Builder;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractSinglePhdFileVisitor<T extends Phd> implements Builder<T>, PhdFileVisitor{
    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private List<NucleotideGlyph> bases = new ArrayList<NucleotideGlyph>();
    private List<PhredQuality> qualities = new ArrayList<PhredQuality>();
    private List<ShortGlyph> positions = new ArrayList<ShortGlyph>();
    private boolean initialized = false;
    private Properties comments;
    private String id;
    
    @Override
    public synchronized T build() {
       if(!initialized){
           throw new IllegalStateException("must visit phd file");
       }
        return buildPhd(id, bases, qualities, positions, comments);
    }

    protected abstract T buildPhd(String id,
            List<NucleotideGlyph> bases,
            List<PhredQuality> qualities,
            List<ShortGlyph> positions, 
            Properties comments);

    @Override
    public synchronized void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        checkNotYetInitialized();
       bases.add(base);
       qualities.add(quality);
       positions.add(PEAK_FACTORY.getGlyphFor(tracePosition));            
    }



    private void checkNotYetInitialized() {
        if(initialized){
            throw new IllegalStateException("can only create 1 phd multiple phds detected");
        }
    }



    @Override
    public void visitBeginDna() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitBeginSequence(String id) {
        checkNotYetInitialized();
        this.id = id;
    }



    @Override
    public synchronized void visitComment(Properties comments) {
        checkNotYetInitialized();
        this.comments = comments;
    }



    @Override
    public synchronized void visitEndDna() {
        initialized =true;
        
    }



    @Override
    public synchronized void visitEndSequence() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitLine(String line) {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
    }



    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
    }

}
