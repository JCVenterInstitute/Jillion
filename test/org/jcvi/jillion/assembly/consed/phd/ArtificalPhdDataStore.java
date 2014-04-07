/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.AbstractDataStore;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
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
    private final Map<String,String> comments = new HashMap<String, String>();

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
	                comments,Collections.<PhdWholeReadItem>emptyList());
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
	protected StreamingIterator<DataStoreEntry<Phd>> entryIteratorImpl()
			throws DataStoreException {
		
		return new StreamingIterator<DataStoreEntry<Phd>>(){
			StreamingIterator<Phd> iter = iteratorImpl();

			@Override
			public boolean hasNext() {				
				return iter.hasNext();
			}

			@Override
			public void close() {
				iter.close();
				
			}

			@Override
			public DataStoreEntry<Phd> next() {
				Phd next = iter.next();
				return new DataStoreEntry<Phd>(next.getId(), next);
			}

			@Override
			public void remove() {
				iter.next();
				
			}
			
		};
	}

	@Override
	protected void handleClose() throws IOException {
		 seqDataStore.close();
	        qualDataStore.close();
	        comments.clear();
		
	}

    
}
