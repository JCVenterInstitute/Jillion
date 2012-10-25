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

package org.jcvi.common.core.assembly.clc.cas.read;

import java.io.File;

import org.jcvi.common.core.assembly.clc.cas.CasTrimMap;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;

/**
 * {@code AbstractCasDataStoreFactory} is an abstract
 * implementation of {@link CasDataStoreFactory} that
 * takes into account the cas working directory to correctly
 * look up data files the cas file refers to.
 * <p/>
 * Cas files often use relative paths which makes parsing a cas file 
 * impossible unless you use the same working directory, this class
 * handles the relative pathing issues for you so subclasses
 * always have the references to the correct Files.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasDataStoreFactory implements CasDataStoreFactory{

    private final File workingDir;
    private final CasTrimMap trimMap;
    private final DataStoreFilter filter;
    /**
     * @param workingDir
     */
    public AbstractCasDataStoreFactory(File workingDir, CasTrimMap trimMap) {
        this(workingDir, trimMap, DataStoreFilters.alwaysAccept());
    }
    
    public AbstractCasDataStoreFactory(File workingDir, CasTrimMap trimMap, DataStoreFilter filter) {
        this.workingDir = workingDir;
        this.trimMap = trimMap;
        this.filter = filter;
    }

    @Override
    public final NucleotideSequenceDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        File trimmedDataStore = getTrimmedFileFor(pathToDataStore);   
        return getNucleotideDataStoreFor(trimmedDataStore,filter);
    }

    private File getTrimmedFileFor(String pathToDataStore) {
        final File dataStoreFile;
        if(pathToDataStore.startsWith("/")){
            dataStoreFile = new File(pathToDataStore);
        }else{
            dataStoreFile = new File(workingDir, pathToDataStore);
        }
        return trimMap.getUntrimmedFileFor(dataStoreFile);
    }

    /**
     * @param file
     * @return
     */
    protected abstract NucleotideSequenceDataStore getNucleotideDataStoreFor(File file,DataStoreFilter filter) throws CasDataStoreFactoryException;

    @Override
    public final QualitySequenceDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        File trimmedDataStore = getTrimmedFileFor(pathToDataStore);   
        return getQualityDataStoreFor(trimmedDataStore,filter);
    }
    /**
     * @param file
     * @return
     */
    protected abstract QualitySequenceDataStore getQualityDataStoreFor(File file,DataStoreFilter filter) throws CasDataStoreFactoryException;

    
}
