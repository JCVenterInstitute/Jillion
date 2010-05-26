/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class DefaultContigAssembly implements ContigAssembly{

    private final DataStore<Contig<PlacedRead>> contigDataStore;
    private final NucleotideDataStore nucleotideDataStore;
    private final QualityDataStore qualityDataStore;
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
            NucleotideDataStore nucleotideDataStore,
            List<File> nucleotideFiles,
            QualityDataStore qualityDataStore,
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
    public NucleotideDataStore getNucleotideDataStore() {
        return nucleotideDataStore;
    }

    @Override
    public QualityDataStore getQualityDataStore() {
        return qualityDataStore;
    }

    @Override
    public List<File> getQualityFiles() {
        return qualityFiles;
    }
    
    public static class Builder implements org.jcvi.Builder<DefaultContigAssembly>{
        private final DataStore<Contig<PlacedRead>> contigDataStore;
        private final NucleotideDataStore nucleotideDataStore;
        private final QualityDataStore qualityDataStore;
        private final List<File> nucleotideFiles = new ArrayList<File>();
        private final List<File> qualityFiles = new ArrayList<File>();
        
        
        /**
         * @param contigDataStore
         * @param nucleotideDataStore
         * @param qualityDataStore
         */
        public Builder(DataStore<Contig<PlacedRead>> contigDataStore,
                NucleotideDataStore nucleotideDataStore,
                QualityDataStore qualityDataStore) {
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
