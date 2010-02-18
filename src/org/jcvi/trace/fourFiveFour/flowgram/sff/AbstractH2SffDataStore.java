/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;

public abstract class AbstractH2SffDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>, SffFileVisitor{

    private final AbstractH2EncodedGlyphDataStore<G, E> datastore;
    private final boolean trim;
    private SFFReadHeader currentReadHeader;
    /**
     * @param datastore
     * @throws FileNotFoundException 
     * @throws SFFDecoderException 
     */
    public AbstractH2SffDataStore(File sffFile,AbstractH2EncodedGlyphDataStore<G, E> datastore, boolean trim) throws SFFDecoderException, FileNotFoundException {
        this.datastore = datastore;
        this.trim = trim;
        SffParser.parseSFF(sffFile, this);
    }
    public AbstractH2SffDataStore(File sffFile,AbstractH2EncodedGlyphDataStore<G, E> datastore) throws SFFDecoderException, FileNotFoundException {
        this(sffFile, datastore, false);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }
    @Override
    public E get(String id) throws DataStoreException {
        return datastore.get(id);
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return datastore.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return datastore.size();
    }
    @Override
    public void close() throws IOException {
        datastore.close();
        
    }
    @Override
    public Iterator<E> iterator() {
        return datastore.iterator();
    }
    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        return true;
    }
    protected abstract String getDataRecord(SFFReadHeader readHeader,SFFReadData readData, boolean shouldTrim);
    
    @Override
    public boolean visitReadData(SFFReadData readData) {
        
        try {
            datastore.insertRecord(currentReadHeader.getName(), getDataRecord(currentReadHeader,readData,trim));
        } catch (DataStoreException e) {
            throw new IllegalStateException("Could not insert read into datastore",e);
        }
        return true;
    }
    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        currentReadHeader = readHeader;
        return true;
    }
    @Override
    public void visitEndOfFile() {
        
    }
    @Override
    public void visitFile() {
        
    }
    
}
