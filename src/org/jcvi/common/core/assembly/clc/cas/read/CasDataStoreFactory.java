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

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
/**
 * {@code CasDataStoreFactory} is a way to get {@link NucleotideSequenceDataStore}s
 * and {@link QualitySequenceDataStore}s from the file paths
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
     * path as a {@link NucleotideSequenceDataStore}.
     * @param pathToDataStore the file path (may be relative) to 
     * a file which has nucleotide data.
     * @return the data encoded in the given 
     * file as a {@link NucleotideSequenceDataStore}.
     * @throws CasDataStoreFactoryException if there is a problem reading the 
     * file.
     * @throws NullPointerException if the pathToDataStore is {@code null}.
     */
    NucleotideSequenceDataStore getNucleotideDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
    /**
     * Get the data encoded in the given file
     * path as a {@link QualitySequenceDataStore}.
     * @param pathToDataStore the file path (may be relative) to 
     * a file which has quality data.
     * @return the data encoded in the given 
     * file as a {@link QualitySequenceDataStore}.
     * @throws CasDataStoreFactoryException if there is a problem reading the 
     * file.
     * @throws NullPointerException if the pathToDataStore is {@code null}.
     */
    QualitySequenceDataStore getQualityDataStoreFor(String pathToDataStore) throws CasDataStoreFactoryException;
}
