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

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.nuc.NucleotideDataStore;
import org.jcvi.common.core.seq.qual.QualityDataStore;
/**
 * {@code CasDataStoreFactory} is a way to get {@link NucleotideDataStore}s
 * and {@link QualityDataStore}s from the file paths
 * encoded inside a CAS file.  Since the CAS file does not know
 * how this data is encoded, it is this Factory's job
 * of decoding the files into {@link DataStore}s.
 * @author dkatzel
 *
 *
 */
public interface CasDataStoreFactory {
    /**
     * Get the data encoded in the given file
     * path as a {@link NucleotideDataStore}.
     * @param pathToDataStore the file path (may be relative) to 
     * a file which has nucleotide data.
     * @return the data encoded in the given 
     * file as a {@link NucleotideDataStore}.
     * @throws CasDataStoreFactoryException if there is a problem reading the 
     * file.
     * @throws NullPointerException if the pathToDataStore is {@code null}.
     */
    NucleotideDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
    /**
     * Get the data encoded in the given file
     * path as a {@link QualityDataStore}.
     * @param pathToDataStore the file path (may be relative) to 
     * a file which has quality data.
     * @return the data encoded in the given 
     * file as a {@link QualityDataStore}.
     * @throws CasDataStoreFactoryException if there is a problem reading the 
     * file.
     * @throws NullPointerException if the pathToDataStore is {@code null}.
     */
    QualityDataStore getQualityDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
}
