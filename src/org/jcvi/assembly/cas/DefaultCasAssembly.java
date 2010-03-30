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
package org.jcvi.assembly.cas;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.jcvi.assembly.cas.read.CasDataStoreFactory;
import org.jcvi.assembly.cas.read.AbstractCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.DefaultCasFileQualityDataStore;
import org.jcvi.assembly.cas.read.ReadCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.ReferenceCasFileNucleotideDataStore;
import org.jcvi.assembly.cas.read.SffTrimDataStore;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.MultipleDataStoreWrapper;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.jcvi.util.MultipleWrapper;

public class DefaultCasAssembly implements CasAssembly{
    private final DataStore<CasContig> casDataStore;
    private final NucleotideDataStore nucleotideDataStore;
    private final QualityDataStore qualityDataStore;
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
            NucleotideDataStore nucleotideDataStore,
            QualityDataStore qualityDataStore,
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
    public NucleotideDataStore getNucleotideDataStore() {
        return nucleotideDataStore;
    }

    @Override
    public QualityDataStore getQualityDataStore() {
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
        private final TrimDataStore externalTrimDataStore;
        private final Map<String, String> trimToUntrimmedMap;
        /**
         * @param casFile
         * @param casDataStoreFactory
         * @param consensusCaller
         * @param sliceMapFactory
         * @param solexaQualityCodec
         */
        public Builder(File casFile, CasDataStoreFactory casDataStoreFactory,TrimDataStore externalTrimDataStore,Map<String, String> trimToUntrimmedMap) {
            this.casFile = casFile;
            this.casDataStoreFactory = casDataStoreFactory;
            this.externalTrimDataStore = externalTrimDataStore;
            this.trimToUntrimmedMap = trimToUntrimmedMap;
        }

        @Override
        public DefaultCasAssembly build() {
            AbstractDefaultCasFileLookup readIdLookup = new DefaultReadCasFileLookup(trimToUntrimmedMap);
            AbstractDefaultCasFileLookup referenceIdLookup = new DefaultReferenceCasFileLookup();
            AbstractCasFileNucleotideDataStore nucleotideDataStore = new ReadCasFileNucleotideDataStore(casDataStoreFactory);
            AbstractCasFileNucleotideDataStore referenceNucleotideDataStore = new ReferenceCasFileNucleotideDataStore(casDataStoreFactory);
            
            DefaultCasFileQualityDataStore qualityDataStore = new DefaultCasFileQualityDataStore(casDataStoreFactory);
            
            try {
                CasParser.parseCas(casFile, MultipleWrapper.createMultipleWrapper(
                        CasFileVisitor.class, 
                        readIdLookup, referenceIdLookup,nucleotideDataStore,referenceNucleotideDataStore,qualityDataStore));
            SffTrimDataStore sffTrimDatastore = new SffTrimDataStore();
            for(File readFile : readIdLookup.getFiles()){
                String extension =FilenameUtils.getExtension(readFile.getName());
                if("sff".equals(extension)){
                    SffParser.parseSFF(readFile, sffTrimDatastore);
                }
            }
            DefaultCasGappedReferenceMap gappedReferenceMap = new DefaultCasGappedReferenceMap(referenceNucleotideDataStore, referenceIdLookup);
            CasParser.parseCas(casFile, gappedReferenceMap);
           
            TrimDataStore multiTrimDataStore =MultipleDataStoreWrapper.createMultipleDataStoreWrapper(TrimDataStore.class, this.externalTrimDataStore, sffTrimDatastore);
            
            DefaultCasFileContigDataStore casDatastore = new DefaultCasFileContigDataStore(
                    referenceIdLookup, 
                    readIdLookup, 
                    gappedReferenceMap, 
                    nucleotideDataStore,
                    multiTrimDataStore);
            
            CasParser.parseCas(casFile, casDatastore);
            return new DefaultCasAssembly(casDatastore, 
                    MultipleDataStoreWrapper.createMultipleDataStoreWrapper(NucleotideDataStore.class, 
                            nucleotideDataStore, referenceNucleotideDataStore), 
                    qualityDataStore, 
                    readIdLookup, referenceIdLookup);
            } catch (Exception e) {
                throw new IllegalStateException("error building CasAssembly",e);
            }
        }
        
    }
}
