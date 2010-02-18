/*
 * Created on Jan 7, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultContigAssembly implements ContigAssembly{

    private final DataStore<Contig<PlacedRead>> contigDataStore;
    private final DataStore<NucleotideEncodedGlyphs> nucleotideDataStore;
    private final DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore;
    private final List<File> nucleotideFiles;
    private final List<File> qualityFiles;
    
    
    /**
     * @param contigDataStore
     * @param nucleotideDataStore
     * @param nucleotideFiles
     * @param qualityDataStore
     * @param qualityFiles
     */
    public DefaultContigAssembly(DataStore<Contig<PlacedRead>> contigDataStore,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,
            List<File> nucleotideFiles,
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
            List<File> qualityFiles) {
        this.contigDataStore = contigDataStore;
        this.nucleotideDataStore = nucleotideDataStore;
        this.nucleotideFiles = nucleotideFiles;
        this.qualityDataStore = qualityDataStore;
        this.qualityFiles = qualityFiles;
    }

    @Override
    public DataStore<Contig<PlacedRead>> getContigDataStore() {
        return contigDataStore;
    }

    @Override
    public List<File> getNuceotideFiles() {
        return nucleotideFiles;
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
        return qualityFiles;
    }
    
    public static class Builder implements org.jcvi.Builder<DefaultContigAssembly>{
        private final DataStore<Contig<PlacedRead>> contigDataStore;
        private final DataStore<NucleotideEncodedGlyphs> nucleotideDataStore;
        private final DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore;
        private final List<File> nucleotideFiles = new ArrayList<File>();
        private final List<File> qualityFiles = new ArrayList<File>();
        
        
        /**
         * @param contigDataStore
         * @param nucleotideDataStore
         * @param qualityDataStore
         */
        public Builder(DataStore<Contig<PlacedRead>> contigDataStore,
                DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,
                DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore) {
            if(contigDataStore ==null || nucleotideDataStore ==null || qualityDataStore ==null){
                throw new NullPointerException("parameters can not be null");
            }
            this.contigDataStore = contigDataStore;
            this.nucleotideDataStore = nucleotideDataStore;
            this.qualityDataStore = qualityDataStore;
        }

        public Builder addNucleotideFile(File file){
            nucleotideFiles.add(file);
            return this;
        }
        public Builder addQualityFile(File file){
            qualityFiles.add(file);
            return this;
        }
        @Override
        public DefaultContigAssembly build() {
            return new DefaultContigAssembly(contigDataStore,nucleotideDataStore, 
                    Collections.unmodifiableList(nucleotideFiles),qualityDataStore, Collections.unmodifiableList(qualityFiles));
        }
        
    }
}
