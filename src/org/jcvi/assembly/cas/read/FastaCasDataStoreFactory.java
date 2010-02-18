/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeNucleotideFastaFileDataStore;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.qualClass.QualityDataStoreAdapter;

public class FastaCasDataStoreFactory implements
        CasDataStoreFactory {

    private final int cacheSize;
    public FastaCasDataStoreFactory(int cacheSize){
        this.cacheSize = cacheSize;
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException {
             return CachedDataStore.createCachedDataStore(NucleotideDataStore.class, 
                     new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeNucleotideFastaFileDataStore(new File(pathToDataStore)))),
                     cacheSize);            
    }
    @Override
    public QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        return CachedDataStore.createCachedDataStore(QualityDataStore.class, 
                new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(new File(pathToDataStore)))),
                cacheSize);  
        
    }

}
