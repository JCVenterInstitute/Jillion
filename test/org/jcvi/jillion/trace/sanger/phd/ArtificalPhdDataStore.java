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
package org.jcvi.jillion.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.internal.datastore.AbstractDataStore;
import org.jcvi.jillion.core.internal.datastore.DataStoreIterator;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sanger.phd.ArtificialPhd;
import org.jcvi.jillion.trace.sanger.phd.Phd;
import org.jcvi.jillion.trace.sanger.phd.PhdDataStore;
import org.jcvi.jillion.trace.sanger.phd.PhdTag;
import org.jcvi.jillion.trace.sanger.phd.PhdUtil;
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
            DataStore<QualitySequence> qualDataStore, Date phdDate) {
        this.seqDataStore = seqDataStore;
        this.qualDataStore = qualDataStore;
        comments.putAll(PhdUtil.createPhdTimeStampCommentFor(phdDate));
    }

    @Override
	protected boolean containsImpl(String id) throws DataStoreException {
		return seqDataStore.contains(id);
	}

	@Override
	protected Phd getImpl(String id) throws DataStoreException {
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
	protected long getNumberOfRecordsImpl() throws DataStoreException {
		return seqDataStore.getNumberOfRecords();
	}

	@Override
	protected StreamingIterator<String> idIteratorImpl() throws DataStoreException {
		return seqDataStore.idIterator();
	}

	@Override
	protected StreamingIterator<Phd> iteratorImpl() {
		return DataStoreStreamingIterator.create(this, 
				new DataStoreIterator<Phd>(this));
		
	}




	@Override
	protected void handleClose() throws IOException {
		 seqDataStore.close();
	        qualDataStore.close();
	        comments.clear();
		
	}

    
}
