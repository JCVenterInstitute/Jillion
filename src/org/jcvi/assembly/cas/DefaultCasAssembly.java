/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.DefaultCasFileQualityDataStore;
import org.jcvi.assembly.cas.read.ReadCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.MultipleWrapper;

public class DefaultCasAssembly implements CasAssembly{
    private final DataStore<CasContig> casDataStore;
    private final DataStore<NucleotideEncodedGlyphs> nucleotideDataStore;
    private final DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore;
    private final CasIdLookup referenceIdLookup;
    private final CasIdLookup traceIdLookup;
    
    /**
     * @param casDataStore
     * @param nucleotideDataStore
     * @param nucleotideFiles
     * @param qualityDataStore
     * @param qualityFiles
     */
    public DefaultCasAssembly(DataStore<CasContig> casDataStore,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
            CasIdLookup traceIdLookup,CasIdLookup referenceIdLookup) {
        this.casDataStore = casDataStore;
        this.nucleotideDataStore = nucleotideDataStore;
        this.qualityDataStore = qualityDataStore;
        this.traceIdLookup = traceIdLookup;
        this.referenceIdLookup = referenceIdLookup;
    }

    @Override
    public CasIdLookup getReadIdLookup() {
        return traceIdLookup;
    }

    @Override
    public CasIdLookup getReferenceIdLookup() {
        return referenceIdLookup;
    }

    @Override
    public DataStore<CasContig> getContigDataStore() {
        return casDataStore;
    }

    @Override
    public List<File> getNuceotideFiles() {
        return traceIdLookup.getFiles();
    }

    @Override
    public DataStore<NucleotideEncodedGlyphs> getNucleotideDataStore() {
        return nucleotideDataStore;
    }

    @Override
    public DataStore<EncodedGlyphs<PhredQuality>> getQualityDataStore() {
        return qualityDataStore;
    }

    @Override
    public List<File> getQualityFiles() {
        return traceIdLookup.getFiles();
    }
    @Override
    public List<File> getReferenceFiles(){
        return referenceIdLookup.getFiles();
    }

    public static class Builder implements org.jcvi.Builder<DefaultCasAssembly>{
        public static final int DEFAULT_CACHE_SIZE = 2000;
        private final File casFile;
        private final CasDataStoreFactory casDataStoreFactory;
        
       
        /**
         * @param casFile
         * @param casDataStoreFactory
         * @param consensusCaller
         * @param sliceMapFactory
         * @param solexaQualityCodec
         */
        public Builder(File casFile, CasDataStoreFactory casDataStoreFactory) {
            this.casFile = casFile;
            this.casDataStoreFactory = casDataStoreFactory;
        }

        @Override
        public DefaultCasAssembly build() {
            AbstractDefaultCasFileLookup readIdLookup = new DefaultReadCasFileLookup();
            AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup();
            AbstractCasFileNucleotideDataStore nucleotideDataStore = new ReadCasFileNucleotideDataStore(casDataStoreFactory);
            AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(casDataStoreFactory);
            
            DefaultCasFileQualityDataStore qualityDataStore = new DefaultCasFileQualityDataStore(casDataStoreFactory);
            
            try {
                CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(
                        CasFileVisitor.class, 
                        readIdLookup, referenceIdLookup,nucleotideDataStore,referenceNucleotideDataStore,qualityDataStore));
            
            
            DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
            CasParser.parseCas(casFile, gappedReferenceMap);
           
            
            DefaultCasFileContigDataStore casDatastore = new DefaultCasFileContigDataStore(
                    referenceIdLookup, 
                    readIdLookup, 
                    gappedReferenceMap, 
                    nucleotideDataStore);
            
            CasParser.parseCas(casFile, casDatastore);
            return new DefaultCasAssembly(casDatastore, 
                    MultipleDataStoreWrapper.createMultipleDataStoreWrapper(DataStore.class, 
                            nucleotideDataStore, referenceNucleotideDataStore), 
                    qualityDataStore, 
                    readIdLookup, referenceIdLookup);
            } catch (IOException e) {
                throw new IllegalStateException("error building CasAssembly",e);
            }
        }
        
    }
}
