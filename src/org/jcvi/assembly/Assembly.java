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
package org.jcvi.assembly;

import java.io.File;
import java.util.List;

import org.jcvi.common.core.seq.nuc.NucleotideDataStore;
import org.jcvi.common.core.seq.qual.QualityDataStore;
import org.jcvi.datastore.DataStore;
/**
 * {@code Assembly} is an object which contains all 
 * the input and output data from an assembler invocation.
 * @author dkatzel
 * @param <C> the type of {@link Contig}s in this assembly.
 * @param <D> the type of {@link DataStore} of Contigs in this assembly.
 *
 */
public interface Assembly<C extends Contig, D extends DataStore<C>> {
    /**
     * Get the {@link DataStore} which contains all the assembled
     * {@link Contig}s.
     * @return a {@link DataStore} of {@link Contig}s; will never be null.
     */
    D getContigDataStore();
    /**
     * Get the {@link QualityDataStore} for all the quality values
     * for the underyling reads in this assembly.
     * @return a {@link QualityDataStore}; will never be null.
     */
    QualityDataStore getQualityDataStore();
    /**
     * Get the actual File objects of all the input files
     * which contain quality data.  The list of files should be the source
     * of all the quality data contained in the {@link QualityDataStore} returned
     * by {@link #getQualityDataStore()}.
     * @return a List of Files; will never be null.
     */
    List<File> getQualityFiles();
    /**
     * Get the {@link NucleotideDataStore} for all the sequence
     * data for the underlying reads in this assembly.
     * @return a {@link NucleotideDataStore}; will never be null.
     */
    NucleotideDataStore getNucleotideDataStore();
    /**
     * Get the actual File objects of all the input files
     * which contain sequence data.  The list of files should be the source
     * of all the sequence data contained in the {@link NucleotideDataStore} returned
     * by {@link #getNucleotideDataStore()}.
     * @return a List of Files; will never be null.
     */
    List<File> getNuceotideFiles();
    
}
