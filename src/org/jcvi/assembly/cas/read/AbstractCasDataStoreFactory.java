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

package org.jcvi.assembly.cas.read;

import java.io.File;

import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;

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
    
    /**
     * @param workingDir
     */
    public AbstractCasDataStoreFactory(File workingDir) {
        this.workingDir = workingDir;
    }

    @Override
    public final NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        
        return getNucleotideDataStoreFor(new File(workingDir, pathToDataStore));
    }

    /**
     * @param file
     * @return
     */
    protected abstract NucleotideDataStore getNucleotideDataStoreFor(File file) throws CasDataStoreFactoryException;

    @Override
    public final QualityDataStore getQualityDataStoreFor(String pathToDataStore)
            throws CasDataStoreFactoryException {
        return getQualityDataStoreFor(new File(workingDir, pathToDataStore));
    }
    /**
     * @param file
     * @return
     */
    protected abstract QualityDataStore getQualityDataStoreFor(File file) throws CasDataStoreFactoryException;

    
}
