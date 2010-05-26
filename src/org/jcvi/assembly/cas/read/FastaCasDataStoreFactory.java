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
import org.jcvi.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.fasta.LargeNucleotideFastaFileDataStore;
import org.jcvi.fasta.LargeQualityFastaFileDataStore;
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
public class FastaCasDataStoreFactory implements
        CasDataStoreFactory {

    private final int cacheSize;
    private final CasTrimMap trimToUntrimmedMap;
    /**
     * Create a FastaCasDataStoreFactory with the given cacheSize.
     * @param cacheSize the max number of fasta records to store in memory. 
     */
    public FastaCasDataStoreFactory(int cacheSize){
        this(EmptyCasTrimMap.getInstance(),cacheSize);
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
        this.trimToUntrimmedMap = trimToUntrimmedMap;
        this.cacheSize = cacheSize;
    }
    @Override
    public NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException {
        File actualDataStore = trimToUntrimmedMap.getUntrimmedFileFor(new File(pathToDataStore));     
        return CachedDataStore.createCachedDataStore(NucleotideDataStore.class, 
                     new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(new LargeNucleotideFastaFileDataStore(actualDataStore))),
                     cacheSize);            
    }
    @Override
    public QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        File actualDataStore = trimToUntrimmedMap.getUntrimmedFileFor(new File(pathToDataStore));   
        return CachedDataStore.createCachedDataStore(QualityDataStore.class, 
                new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(actualDataStore))),
                cacheSize);  
        
    }

    
}
