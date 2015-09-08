/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileParser;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileVisitor;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigVisitor;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;

public class AceAdapterContigFileDataStore implements AceFileDataStore{

    private final Map<String, AceContig> map = new HashMap<String, AceContig>();
    private DataStore<AceContig> dataStore;
    private long totalNumberOfReads=0L;

    public static AceFileDataStore create(final QualityFastaDataStore fullLengthQualityDataStore, final Date phdDate, File contigFile) throws IOException{
        return create(fullLengthQualityDataStore, phdDate, contigFile,false);
    }
    public static AceFileDataStore create(final QualityFastaDataStore fullLengthQualityDataStore, final Date phdDate, File contigFile, final boolean computeConsedConsensusQualities) throws IOException{
    	final AceAdapterContigFileDataStore datastore = new AceAdapterContigFileDataStore();
    	TigrContigFileVisitor visitor =new TigrContigFileVisitor() {
			
			@Override
			public void halted() {
				//no-op
				
			}
			
			@Override
			public void visitEnd() {
				datastore.dataStore = DataStoreUtil.adapt(datastore.map);
			    datastore.map.clear();
				
			}
			
			@Override
			public TigrContigVisitor visitContig(TigrContigVisitorCallback callback,
					String contigId) {
				final QualitySequenceDataStore qualitySequences = DataStoreUtil.adapt(QualitySequenceDataStore.class, 
						fullLengthQualityDataStore, 
						new DataStoreUtil.AdapterCallback<QualityFastaRecord, QualitySequence>() {

							@Override
							public QualitySequence get(
									QualityFastaRecord from) {
								return from.getSequence();
							}
					
				});
				return new AbstractAceAdaptedContigVisitor(contigId,qualitySequences, phdDate) {
					
					@Override
					protected void visitContig(AceContigBuilder contigBuilder) {
						if(computeConsedConsensusQualities){
							contigBuilder.computeConsensusQualities(qualitySequences);
						}
						AceContig contig = contigBuilder.build();
						datastore.map.put(contig.getId(), contig);  
						datastore.totalNumberOfReads += contig.getNumberOfReads();
						
					}
				};
			}
		};
		
		TigrContigFileParser.create(contigFile).parse(visitor);
		return datastore;
    }
    
    
   
    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    @Override
    public AceContig get(String id) throws DataStoreException {
        return dataStore.get(id);
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return dataStore.idIterator();
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return dataStore.getNumberOfRecords();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public StreamingIterator<AceContig> iterator() throws DataStoreException {
        return dataStore.iterator();
    }
    
    
    @Override
	public StreamingIterator<DataStoreEntry<AceContig>> entryIterator()
			throws DataStoreException {
		return dataStore.entryIterator();
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return dataStore.isClosed();
    }
	@Override
	public long getNumberOfTotalReads() {
		return totalNumberOfReads;
	}
	@Override
	public StreamingIterator<WholeAssemblyAceTag> getWholeAssemblyTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}
	@Override
	public StreamingIterator<ReadAceTag> getReadTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}
	@Override
	public StreamingIterator<ConsensusAceTag> getConsensusTagIterator() {
		return IteratorUtil.createEmptyStreamingIterator();
	}

}
