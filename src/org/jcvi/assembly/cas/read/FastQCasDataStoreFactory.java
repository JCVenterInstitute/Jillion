/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.fasta.fastq.FastQNucleotideDataStoreAdapter;
import org.jcvi.fasta.fastq.FastQQualitiesDataStoreAdapter;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.fasta.fastq.LargeFastQFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class FastQCasDataStoreFactory implements
CasDataStoreFactory {
    private final int cacheSize;
    private final Map<String, DataStore<FastQRecord>> fastQDataStores = new HashMap<String, DataStore<FastQRecord>>();
    private final FastQQualityCodec quailtyCodec;
    /**
     * @param cacheSize
     */
    public FastQCasDataStoreFactory(FastQQualityCodec quailtyCodec, int cacheSize) {
        this.cacheSize = cacheSize;
        this.quailtyCodec = quailtyCodec;
    }

    @Override
    public synchronized NucleotideDataStore getNucleotideDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(pathToDataStore);
        return new FastQNucleotideDataStoreAdapter(fastQDataStores.get(pathToDataStore));
    }
    private void addDataStoreIfNeeded(String pathToDataStore) throws CasDataStoreFactoryException{
        if(!fastQDataStores.containsKey(pathToDataStore)){ 
            if(!"fastq".equals(FilenameUtils.getExtension(pathToDataStore))){
                throw new CasDataStoreFactoryException("not a fastq file");
            }
            DataStore<FastQRecord> dataStore = new LargeFastQFileDataStore(new File(pathToDataStore),quailtyCodec );
            
            DataStore<FastQRecord> cachedDataStore = CachedDataStore.createCachedDataStore(
                    DataStore.class,
                    dataStore,
                    cacheSize);
            fastQDataStores.put(pathToDataStore, cachedDataStore);
        }
    }
    @Override
    public synchronized QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(pathToDataStore);
        return new FastQQualitiesDataStoreAdapter(fastQDataStores.get(pathToDataStore));
    }

}
