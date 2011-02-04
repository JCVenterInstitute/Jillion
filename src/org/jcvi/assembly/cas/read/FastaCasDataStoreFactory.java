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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

import java.io.File;

import org.jcvi.assembly.cas.CasTrimMap;
import org.jcvi.assembly.cas.EmptyCasTrimMap;
import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.fastX.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fastX.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.fastX.fasta.qual.QualityFastaRecordDataStoreAdapter;
import org.jcvi.fastX.fasta.seq.LargeNucleotideFastaFileDataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.datastore.NucleotideDataStoreAdapter;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.datastore.QualityDataStoreAdapter;
/**
 * {@code FastaCasDataStoreFactory} is a {@link CasDataStoreFactory}
 * implementation for .fasta files.
 * @author dkatzel
 *
 *
 */
public class FastaCasDataStoreFactory extends AbstractCasDataStoreFactory
        {

    private final int cacheSize;
    /**
     * Create a FastaCasDataStoreFactory with the given cacheSize.
     * @param cacheSize the max number of fasta records to store in memory. 
     */
    public FastaCasDataStoreFactory(int cacheSize){
        this(EmptyCasTrimMap.getInstance(),cacheSize);
    }
    public FastaCasDataStoreFactory(File workingDir,int cacheSize){
        this(workingDir,EmptyCasTrimMap.getInstance(),cacheSize);
    }
    /**
     * Create a FastaCasDataStoreFactory which will automatically
     * trim any records with the given {@link CasTrimMap} and using
     * the given cacheSize.
     * @param trimToUntrimmedMap a non-null CasTrimMap which may trim any
     * records parsed.
     * @param cacheSize the max number of (trimmed) fasta records to store in memory. 
     */
    public FastaCasDataStoreFactory(CasTrimMap trimToUntrimmedMap,int cacheSize){
        this(null, trimToUntrimmedMap, cacheSize);
    }
    public FastaCasDataStoreFactory(File workingDir,CasTrimMap trimToUntrimmedMap,int cacheSize){
        super(workingDir,trimToUntrimmedMap);
        this.cacheSize = cacheSize;
    }
    
    /**
     * @param workingDir
     * @param trimMap
     * @param filter
     */
    public FastaCasDataStoreFactory(File workingDir, CasTrimMap trimMap,
            DataStoreFilter filter, int cacheSize) {
        super(workingDir, trimMap, filter);
        this.cacheSize = cacheSize;
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(File pathToDataStore, DataStoreFilter filter) throws CasDataStoreFactoryException {  
        return CachedDataStore.createCachedDataStore(NucleotideDataStore.class, 
                     new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeNucleotideFastaFileDataStore(pathToDataStore))),
                     cacheSize);            
    }
    @Override
    public QualityDataStore getQualityDataStoreFor(
            File fastaFile,DataStoreFilter filter) throws CasDataStoreFactoryException { 
        return CachedDataStore.createCachedDataStore(QualityDataStore.class, 
                QualityFastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(fastaFile)),
                cacheSize);  
        
    }

    
}
