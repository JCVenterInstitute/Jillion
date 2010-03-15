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
import java.util.HashMap;
import java.util.Map;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.TraceNucleotideDataStoreAdapter;
import org.jcvi.trace.TraceQualityDataStoreAdapter;
import org.jcvi.trace.sanger.SangerTrace;
import org.jcvi.trace.sanger.SingleSangerTraceFileDataStore;

/**
 * {@code SangerTraceCasDataStoreFactory} is a {@link CasDataStoreFactory}
 * implementation for Sanger Trace files which contain only 1 trace per file.
 * Current supported file types are {@code .ztr , .scf and .phd (but not .phd.ball)}.
 * @author dkatzel
 *
 *
 */
public class SingleSangerTraceCasDataStoreFactory implements CasDataStoreFactory{
    private final Map<String, TraceDataStore<SangerTrace>> sangerTraceDataStores = new HashMap<String, TraceDataStore<SangerTrace>>();

    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public synchronized NucleotideDataStore getNucleotideDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        
        addDataStoreIfNeeded(pathToDataStore);
        return new TraceNucleotideDataStoreAdapter<SangerTrace>(sangerTraceDataStores.get(pathToDataStore));
    }

    private void addDataStoreIfNeeded(String pathToDataStore)
            throws CasDataStoreFactoryException {
        if(!sangerTraceDataStores.containsKey(pathToDataStore)){            
            TraceDataStore<SangerTrace> dataStore = parseSangerTraceDataStore(pathToDataStore);
            sangerTraceDataStores.put(pathToDataStore, dataStore);
        }
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public synchronized QualityDataStore getQualityDataStoreFor(
            String pathToDataStore) throws CasDataStoreFactoryException {
        addDataStoreIfNeeded(pathToDataStore);
        return new TraceQualityDataStoreAdapter<SangerTrace>(sangerTraceDataStores.get(pathToDataStore));
    }

    private TraceDataStore<SangerTrace> parseSangerTraceDataStore(String pathToDataStore)
            throws CasDataStoreFactoryException {
        
        try {
            return new SingleSangerTraceFileDataStore(new File(pathToDataStore));           
           
        } catch (Exception e) {
            throw new CasDataStoreFactoryException("could not create sff nucleotide datastore", e);
        }
    }

}
