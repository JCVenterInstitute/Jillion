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
package org.jcvi.assembly.ace;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.seq.nuc.NucleotideDataStore;
import org.jcvi.common.core.seq.qual.QualityDataStore;
import org.jcvi.common.core.seq.read.trace.TraceNucleotideDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.TraceQualityDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.datastore.DataStore;

public class DefaultAceAssembly<A extends AceContig> implements AceAssembly<A>{

    private final AceTagMap tagMap;
    private final DataStore<A> aceDataStore;
    private final List<File> phdFiles;
    private final PhdDataStore phdDataStore;
    private final NucleotideDataStore nucleotideDataStore;
    private final QualityDataStore qualityDataStore;
    
    /**
     * @param aceDataStore
     * @param phdDataStore
     * @param phdFiles
     * @param tagMap
     */
    public DefaultAceAssembly(DataStore<A> aceDataStore,
            PhdDataStore phdDataStore, List<File> phdFiles,
            AceTagMap tagMap) {
        this.aceDataStore = aceDataStore;
        this.phdDataStore = phdDataStore;
        this.phdFiles = phdFiles;
        this.tagMap = tagMap;
        this.nucleotideDataStore = new TraceNucleotideDataStoreAdapter<Phd>(phdDataStore);
        this.qualityDataStore = new TraceQualityDataStoreAdapter<Phd>(phdDataStore);
    }
    public DefaultAceAssembly(DataStore<A> aceDataStore,
            PhdDataStore phdDataStore, List<File> phdFiles){
        this(aceDataStore, phdDataStore, phdFiles, DefaultAceTagMap.EMPTY_MAP);
    }
    public DefaultAceAssembly(DataStore<A> aceDataStore,
            PhdDataStore phdDataStore){
        this(aceDataStore, phdDataStore, Collections.<File>emptyList(), DefaultAceTagMap.EMPTY_MAP);
    }
    @Override
    public AceTagMap getAceTagMap() {
        return tagMap;
    }

    @Override
    public PhdDataStore getPhdDataStore() {
        return phdDataStore;
    }

    @Override
    public List<File> getPhdFiles() {
        return phdFiles;
    }

    @Override
    public DataStore<A> getContigDataStore() {
        return aceDataStore;
    }

    @Override
    public List<File> getNuceotideFiles() {
        return phdFiles;
    }

    @Override
    public NucleotideDataStore getNucleotideDataStore() {
        return nucleotideDataStore;
    }

    @Override
    public QualityDataStore getQualityDataStore() {
        return qualityDataStore;
    }

    @Override
    public List<File> getQualityFiles() {
        return phdFiles;
    }
    
    

}
