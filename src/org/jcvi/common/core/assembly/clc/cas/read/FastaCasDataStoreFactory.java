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
package org.jcvi.common.core.assembly.clc.cas.read;

import java.io.File;

import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.assembly.clc.cas.EmptyCasTrimMap;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nt.LargeNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStoreAdapter;
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
     * Create a FastaCasDataStoreFactory which will automatically
     * trim any records with the given {@link CasTrimMap} and using
     * the given cacheSize.
     * @param workingDir the casWorkingDirectory that all files are relative to.
     * @param trimToUntrimmedMap a non-null CasTrimMap which may trim any
     * records parsed.
     * @param cacheSize the max number of (trimmed) fasta records to store in memory. 
     */
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
        return CachedDataStore.create(NucleotideDataStore.class, 
                     new NucleotideDataStoreAdapter( FastaRecordDataStoreAdapter.adapt(LargeNucleotideSequenceFastaFileDataStore.create(pathToDataStore))),
                     cacheSize);            
    }
    @Override
    public QualityDataStore getQualityDataStoreFor(
            File fastaFile,DataStoreFilter filter) throws CasDataStoreFactoryException { 
        return CachedDataStore.create(QualityDataStore.class, 
        		new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(new LargeQualityFastaFileDataStore(fastaFile))),
                cacheSize);  
        
    }

    
}
