/*
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractPhdFileDataStore implements PhdDataStore, PhdFileVisitor{

    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    private List<NucleotideGlyph> currentBases = new ArrayList<NucleotideGlyph>();
    private List<PhredQuality> currentQualities = new ArrayList<PhredQuality>();
    private List<ShortGlyph> currentPositions = new ArrayList<ShortGlyph>();
    private List<PhdTag> tags = new ArrayList<PhdTag>();
    private boolean initialized = false;
    private Properties currentComments;
    private String currentId; 
    private String currentTag;
    private boolean inTag=false;
    private StringBuilder currentTagValueBuilder;
    
    protected abstract void visitPhd(String id,
            List<NucleotideGlyph> bases,
            List<PhredQuality> qualities,
            List<ShortGlyph> positions, 
            Properties comments,
            List<PhdTag> tags);
    
    @Override
    public synchronized void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        checkNotYetInitialized();
        currentBases.add(base);
       currentQualities.add(quality);
       currentPositions.add(PEAK_FACTORY.getGlyphFor(tracePosition));            
    }

    protected void resetCurrentValues(){
        currentBases= new ArrayList<NucleotideGlyph>();
        currentQualities= new ArrayList<PhredQuality>();
        currentPositions= new ArrayList<ShortGlyph>();
        tags = new ArrayList<PhdTag>();
    }

    protected void checkNotYetInitialized() {
        if(initialized){
            throw new IllegalStateException("can only create 1 phd multiple phds detected");
        }
    }
    protected void checkAlreadyInitialized() {
        if(!initialized){
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
        if(currentId !=null){
            visitPhd(currentId, currentBases, currentQualities, currentPositions, currentComments,tags);
        }
        this.currentId = id;
        resetCurrentValues();
    }



    @Override
    public synchronized void visitComment(Properties comments) {
        checkNotYetInitialized();
        this.currentComments = comments;
    }



    @Override
    public synchronized void visitEndDna() {        
    }



    @Override
    public synchronized void visitEndSequence() {
        checkNotYetInitialized();
        
    }



    @Override
    public synchronized void visitLine(String line) {
        checkNotYetInitialized();
        if(inTag){
            currentTagValueBuilder.append(line);
        }
    }



    @Override
    public synchronized void visitEndOfFile() {
        checkNotYetInitialized();
        if(currentId !=null){
            visitPhd(currentId, currentBases, currentQualities, currentPositions, currentComments,tags);
        }
        initialized=true;
    }



    @Override
    public synchronized void visitFile() {
        checkNotYetInitialized();
    }


    @Override
    public Iterator<Phd> iterator() {
        return new DataStoreIterator<Phd>(this);
    }

    @Override
    public synchronized void visitBeginTag(String tagName) {
        checkNotYetInitialized();
        currentTag =tagName;
        currentTagValueBuilder = new StringBuilder();
        inTag =true;
    }

    @Override
    public synchronized void visitEndTag() {
        checkNotYetInitialized();
        if(!inTag){
            throw new IllegalStateException("invalid tag");
        }
        tags.add(new DefaultPhdTag(currentTag, currentTagValueBuilder.toString()));
        inTag = false;
    }

}
