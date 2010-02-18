/*
 * Created on Feb 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;

public class H2PhdQualityDataStore implements QualityDataStore, PhdFileVisitor{

    private final H2QualityDataStore qualityDataStore;
    private StringBuilder currentQuailtiesBuilder;
    private String currentId;
    public H2PhdQualityDataStore(File phdFile,H2QualityDataStore qualityDataStore) throws FileNotFoundException{
        this.qualityDataStore = qualityDataStore;
        PhdParser.parsePhd(phdFile, this);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return qualityDataStore.contains(id);
    }

    @Override
    public EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException {
        return qualityDataStore.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return qualityDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return qualityDataStore.size();
    }

    @Override
    public void close() throws IOException {
        qualityDataStore.close();
        
    }

    @Override
    public Iterator<EncodedGlyphs<PhredQuality>> iterator() {
        return qualityDataStore.iterator();
    }

    @Override
    public void visitBasecall(NucleotideGlyph base, PhredQuality quality,
            int tracePosition) {
        currentQuailtiesBuilder.append(quality.getNumber()).append(" ");
        
    }

    @Override
    public void visitBeginDna() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitBeginSequence(String id) {
        currentId = id;
        currentQuailtiesBuilder = new StringBuilder();
        
    }

    @Override
    public void visitBeginTag(String tagName) {
        
    }

    @Override
    public void visitComment(Properties comments) {
        
    }

    @Override
    public void visitEndDna() {
        
    }

    @Override
    public void visitEndSequence() {
        try {
            qualityDataStore.insertRecord(currentId, currentQuailtiesBuilder.toString());
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not insert qualities into datastore",e);
        }
        
    }

    @Override
    public void visitEndTag() {
        
    }

    @Override
    public void visitLine(String line) {
        
    }

    @Override
    public void visitEndOfFile() {
        
    }

    @Override
    public void visitFile() {
        
    }

}
