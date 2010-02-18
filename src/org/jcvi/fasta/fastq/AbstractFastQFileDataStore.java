/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.IOException;
import java.util.Iterator;


import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public abstract class AbstractFastQFileDataStore<T extends FastQRecord> extends AbstractFastQFileVisitor<T> implements FastQDataStore<T>{

    private String currentId, currentComment;
    private EncodedGlyphs<PhredQuality> qualities;
    private NucleotideEncodedGlyphs nucleotides;
    private final FastQQualityCodec qualityCodec;
    private boolean isClosed =false;
    public AbstractFastQFileDataStore(FastQQualityCodec qualityCodec){
        this.qualityCodec = qualityCodec;
    }
   
    public synchronized boolean isClosed() {
        return isClosed;
    }

    protected synchronized void throwExceptionIfClosed() throws DataStoreException{
        if(isClosed()){
            throw new DataStoreException("datastore is closed");
        }
    }
    protected FastQQualityCodec getQualityCodec() {
        return qualityCodec;
    }

    @Override
    public Iterator<T> iterator() {
        try {
            throwExceptionIfClosed();
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not create iterator", e);
        }
        return new DataStoreIterator<T>(this);
    }

    @Override
    public synchronized void close() throws IOException {
        isClosed =true;        
    }

    @Override
    public void visitFile() {       
        checkNotYetInitialized();
    }

    protected abstract boolean visitFastQRecord(
            String id, 
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities,
            String optionalComment);
    @Override
    public boolean visitBeginBlock(String id, String optionalComment) {
        checkNotYetInitialized();
        currentId = id;
        currentComment = optionalComment;
        return true;
    }

    @Override
    public boolean visitEncodedQualities(String encodedQualities) {
        checkNotYetInitialized();
        qualities = qualityCodec.decode(encodedQualities);  
        return true;
    }

    @Override
    public boolean visitEndBlock() {
        return visitFastQRecord(currentId, nucleotides, qualities, currentComment);
        
        
    }

    @Override
    public boolean visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
        checkNotYetInitialized();
        this.nucleotides = nucleotides;
        return true;
        
    }



}
