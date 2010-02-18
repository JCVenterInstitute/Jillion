/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultFastQFileDataStore extends AbstractFastQFileDataStore<FastQRecord>{

   private DefaultFastQDataStore dataStore;
   private final DefaultFastQDataStore.Builder<FastQRecord> builder = new DefaultFastQDataStore.Builder<FastQRecord>();
    /**
     * @param qualityCodec
     */
    public DefaultFastQFileDataStore(FastQQualityCodec qualityCodec) {
        super(qualityCodec);        
    }

    @Override
    protected boolean visitFastQRecord( String id, 
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities,
            String optionalComment) {
        builder.put(new DefaultFastQRecord(id, nucleotides, qualities, optionalComment));
        return true;
    }

    
    @Override
    public void visitEndOfFile() {
        super.visitEndOfFile();
        dataStore = builder.build();
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public FastQRecord get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return dataStore.size();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();        
    }

    @Override
    public Iterator<FastQRecord> iterator() {
        return dataStore.iterator();
    }
}
