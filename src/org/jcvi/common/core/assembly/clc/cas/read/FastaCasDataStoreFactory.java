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
import java.io.IOException;

import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaFileDataStoreFactory;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaFileDataStoreFactory;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
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
    public NucleotideSequenceDataStore getNucleotideDataStoreFor(File pathToDataStore, DataStoreFilter filter) throws CasDataStoreFactoryException {  
        try {
			return CachedDataStore.create(NucleotideSequenceDataStore.class, 
			             FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, 
			            		 NucleotideSequenceFastaFileDataStoreFactory.create(pathToDataStore, DataStoreProviderHint.OPTIMIZE_ITERATION)),
			             cacheSize);
		} catch (IOException e) {
			throw new CasDataStoreFactoryException("could not create nucleotide sequence datastore for "+ pathToDataStore.getAbsolutePath(), e);
		}            
    }
    @Override
    public QualitySequenceDataStore getQualityDataStoreFor(
            File fastaFile,DataStoreFilter filter) throws CasDataStoreFactoryException { 
        try {
			return CachedDataStore.create(QualitySequenceDataStore.class, 
					FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, QualitySequenceFastaFileDataStoreFactory.create(fastaFile, DataStoreProviderHint.OPTIMIZE_ITERATION)),
			        cacheSize);
		} catch (IOException e) {
			throw new CasDataStoreFactoryException("error creating quality datastore for "+fastaFile.getAbsolutePath(),e);
		}  
        
    }

    
}
