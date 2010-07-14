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
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.jcvi.assembly.cas.CasTrimMap;
import org.jcvi.assembly.cas.EmptyCasTrimMap;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.fasta.fastq.FastQDataStore;
import org.jcvi.fasta.fastq.FastQNucleotideDataStoreAdapter;
import org.jcvi.fasta.fastq.FastQQualitiesDataStoreAdapter;
import org.jcvi.fasta.fastq.FastQQualityCodec;
import org.jcvi.fasta.fastq.FastQRecord;
import org.jcvi.fasta.fastq.LargeFastQFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class LargeFastQCasDataStoreFactory extends AbstractCasDataStoreFactory {
    private final int cacheSize;
    private final Map<File, FastQDataStore<FastQRecord>> fastQDataStores = new HashMap<File, FastQDataStore<FastQRecord>>();
    private final FastQQualityCodec quailtyCodec;
    /**
     * @param cacheSize
     */
    public LargeFastQCasDataStoreFactory(FastQQualityCodec quailtyCodec, int cacheSize) {
       this(null, quailtyCodec,cacheSize);
    }
    /**
     * @param cacheSize
     */
    public LargeFastQCasDataStoreFactory(File workingDir,FastQQualityCodec quailtyCodec, int cacheSize) {
        this(workingDir, EmptyCasTrimMap.getInstance(), quailtyCodec,cacheSize);
    }
    /**
     * @param cacheSize
     */
    public LargeFastQCasDataStoreFactory(File workingDir, CasTrimMap trimMap,FastQQualityCodec quailtyCodec, int cacheSize) {
        super(workingDir,trimMap);
        this.cacheSize = cacheSize;
        this.quailtyCodec = quailtyCodec;
    }
    @Override
    public synchronized NucleotideDataStore getNucleotideDataStoreFor(
            File fastq, DataStoreFilter filter) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(fastq);
        return new FastQNucleotideDataStoreAdapter(fastQDataStores.get(fastq));
    }
    private void addDataStoreIfNeeded(File fastq) throws CasDataStoreFactoryException{
        if(!fastQDataStores.containsKey(fastq)){ 
            if(!fastq.getName().contains("fastq")){
                throw new CasDataStoreFactoryException("not a fastq file");
            }
            FastQDataStore<FastQRecord> dataStore = new LargeFastQFileDataStore(fastq,quailtyCodec );
            
            FastQDataStore<FastQRecord> cachedDataStore = CachedDataStore.createCachedDataStore(
                    FastQDataStore.class,
                    dataStore,
                    cacheSize);
            fastQDataStores.put(fastq, cachedDataStore);
        }
    }
    @Override
    public synchronized QualityDataStore getQualityDataStoreFor(
            File fastq, DataStoreFilter filter) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(fastq);
        return new FastQQualitiesDataStoreAdapter(fastQDataStores.get(fastq));
    }

}
