/*
 * Created on Feb 2, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.AbstractH2EncodedGlyphDataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public abstract class AbstractH2FastQDataStore<G extends Glyph, E extends EncodedGlyphs<G>> implements DataStore<E>, FastQFileVisitor{
    private final AbstractH2EncodedGlyphDataStore<G, E> datastore;
    private final FastQQualityCodec qualityCodec;
    private String currentId;
    public AbstractH2FastQDataStore(File fastQFile,FastQQualityCodec qualityCodec,AbstractH2EncodedGlyphDataStore<G, E> datastore) throws FileNotFoundException {
        this.datastore = datastore;
        this.qualityCodec = qualityCodec;
        FastQFileParser.parse(fastQFile, this);
    }
    
    public AbstractH2EncodedGlyphDataStore<G, E> getDatastore() {
        return datastore;
    }

    public FastQQualityCodec getQualityCodec() {
        return qualityCodec;
    }

    public String getCurrentId() {
        return currentId;
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
    public boolean visitBeginBlock(String id, String optionalComment) {
        currentId = id;
        return true;
    }


    @Override
    public boolean visitEndBlock() {
        return true;
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
    @Override
    public boolean visitEncodedQualities(String encodedQualities) {
        return true;
    }

    @Override
    public boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        return true;
    }
    
}
