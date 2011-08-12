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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.joda.time.DateTime;
/**
 * {@code ArtificialPhdDataStore} is a {@link DataStore} of
 * Nucleotide and PhredQuality data that has been adapted to 
 * match the {@link Phd} interface.
 * @author dkatzel
 *
 *
 */
public class ArtificalPhdDataStore extends AbstractDataStore<Phd> implements PhdDataStore{
    private final DataStore<NucleotideSequence> seqDataStore;
    private final DataStore<QualitySequence> qualDataStore;
    private final Properties comments = new Properties();
    
   
    
    /**
     * @param seqDataStore
     * @param qualDataStore
     * @param phdDate
     */
    public ArtificalPhdDataStore(DataStore<NucleotideSequence> seqDataStore,
            DataStore<QualitySequence> qualDataStore, DateTime phdDate) {
        this.seqDataStore = seqDataStore;
        this.qualDataStore = qualDataStore;
        comments.putAll(PhdUtil.createPhdTimeStampCommentFor(phdDate));
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return seqDataStore.contains(id);
    }

    @Override
    public synchronized Phd get(String id) throws DataStoreException {
        super.get(id);
       final NucleotideSequence basecalls = seqDataStore.get(id);
       if(basecalls ==null){
           throw new NullPointerException("could not find basecalls for "+id);
       }
    final QualitySequence qualities = qualDataStore.get(id);
    if(qualities ==null){
        throw new NullPointerException("could not find qualities for "+id);
    }
    return ArtificialPhd.createNewbler454Phd(id,
    			basecalls, 
                qualities,
                comments,Collections.<PhdTag>emptyList());
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        return seqDataStore.getIds();
    }

    @Override
    public synchronized int size() throws DataStoreException {
        super.size();
        return seqDataStore.size();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        seqDataStore.close();
        qualDataStore.close();
        comments.clear();
    }

    
}
