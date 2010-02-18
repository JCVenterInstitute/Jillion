/*
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultSffFileDataStore implements SffDataStore, SffFileVisitor{

    private final Map<String, SFFFlowgram> map = new HashMap<String, SFFFlowgram>();
    private boolean initialized=false;
    private boolean closed = false;
    private final GlyphCodec<PhredQuality> phredQualityGlyphCodec;

    private SFFReadHeader currentReadHeader;
    /**
     * @param phredQualityGlyphCodec
     */
    public DefaultSffFileDataStore(
            GlyphCodec<PhredQuality> phredQualityGlyphCodec) {
        this.phredQualityGlyphCodec = phredQualityGlyphCodec;
    }
    public DefaultSffFileDataStore(File sffFile,
            GlyphCodec<PhredQuality> phredQualityGlyphCodec) throws SFFDecoderException, FileNotFoundException {
        this.phredQualityGlyphCodec = phredQualityGlyphCodec;
        SffParser.parseSFF(sffFile, this);
    }
    private void throwExceptionIfNotInitialized(){
        if(!initialized){
            throw new IllegalStateException("Not initialized");
        }
    }
    private void throwExceptionIfInitialized(){
        if(initialized){
            throw new IllegalStateException("Not initialized");
        }
    }
    private void throwExceptionIfClosed(){
        if(closed){
            throw new IllegalStateException("is closed");
        }
    }
    private void throwExceptionIfNotInitializedOrClosed(){
        throwExceptionIfClosed();
        throwExceptionIfNotInitialized();
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return map.containsKey(id);
    }

    @Override
    public SFFFlowgram get(String id) throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();       
        return map.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return map.keySet().iterator();
    }

    @Override
    public int size() throws DataStoreException {
        throwExceptionIfNotInitializedOrClosed();
        return map.size();
    }

    @Override
    public void close() throws IOException {        
        closed = true;
        map.clear();
    }

    @Override
    public Iterator<SFFFlowgram> iterator() {
        throwExceptionIfNotInitializedOrClosed();
        return new DataStoreIterator<SFFFlowgram>(this);
    }

    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        throwExceptionIfInitialized();
        return true;
    }

    @Override
    public boolean visitReadData(SFFReadData readData) {
       throwExceptionIfInitialized();
       map.put(currentReadHeader.getName(), buildSFFFlowgramFrom(currentReadHeader, readData));
       currentReadHeader=null;
       return true;
    }

    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        throwExceptionIfInitialized();
        currentReadHeader = readHeader;
        return true;
    }

    @Override
    public void visitEndOfFile() {
       throwExceptionIfInitialized();
       initialized=true;        
    }

    @Override
    public void visitFile() {
        throwExceptionIfInitialized();
        
    }
    protected SFFFlowgram buildSFFFlowgramFrom(SFFReadHeader readHeader,
            SFFReadData readData) {
        return new SFFFlowgram(
                new DefaultNucleotideEncodedGlyphs(
                        NucleotideGlyph.getGlyphsFor(readData.getBasecalls())),
                        new DefaultEncodedGlyphs<PhredQuality>(phredQualityGlyphCodec,
                                PhredQuality.valueOf(readData.getQualities())),
                SFFUtil.computeValues(readData),
                readHeader.getQualityClip(),
                readHeader.getAdapterClip());
    }
}
