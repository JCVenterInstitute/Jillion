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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;

/**
 * {@code IndexedFastqFileDataStore} is an implementation of 
 * {@link FastqDataStore} that only stores an index containing
 * file offsets to the various {@link FastqRecord}s contained
 * inside the fastq file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fastq record
 * must be seeked to and then re-parsed each time and the fastq file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedFastqFileDataStore{

    /**
	 * Creates a new {@link IndexedFastqFileDataStore}
	 * instance using the given fastqFile which uses has its quality
	 * values encoded in a manner that can be decoded by the given
	 * {@link FastqQualityCodec}.
	 * @param file the fastq file to create an {@link IndexedFastqFileDataStore}
	 * for.
	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @return a new instance of {@link FastqFileDataStore};
	 * never null.
	 * @throws IOException if the input fastq file does not exist or 
	 * if there is a problem parsing the file.
	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
	 */
    public static FastqFileDataStore create(File file,FastqQualityCodec qualityCodec) throws IOException{
    	return create(file, qualityCodec, DataStoreFilters.alwaysAccept());
    }
    
    /**
   	 * Creates a new {@link IndexedFastqFileDataStore}
   	 * instance using the given fastqFile which uses has its quality
   	 * values encoded in a manner that can be decoded by the given
   	 * {@link FastqQualityCodec} which only contains the records
   	 * in the file that are accepted by the given filter.
   	 * @param file the fastq file to create an {@link IndexedFastqFileDataStore}
   	 * for.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @param filter a {@link DataStoreFilter} that will be used
	 * to filter out some (possibly all or none) of the records from
	 * the fastq file so they will not be included in the {@link FastqDataStore}.
	 * Only records which cause {@link DataStoreFilter#accept(String)}
	 * to return {@code true} will be added to this datastore.
   	 * @return a new instance of {@link FastqFileDataStore};
   	 * never null.
   	 * @throws IOException if the input fastq file does not exist or 
   	 * if there is a problem parsing the file.
   	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
   	 */
    public static FastqFileDataStore create(File file,FastqQualityCodec qualityCodec,DataStoreFilter filter) throws IOException{
    	
    	FastqParser parser = FastqFileParser.create(file);
    	
    	return create(parser, qualityCodec, filter);
    }
    /**
   	 * Creates a new {@link IndexedFastqFileDataStore}
   	 * instance using the given {@link FastqParser} which uses has its quality
   	 * values encoded in a manner that can be decoded by the given
   	 * {@link FastqQualityCodec} which only contains the records
   	 * in the file that are accepted by the given filter.
   	 * @param parser the {@link FastqParser} instance used to 
   	 * to create an {@link IndexedFastqFileDataStore}.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @param filter a {@link DataStoreFilter} that will be used
	 * to filter out some (possibly all or none) of the records from
	 * the fastq file so they will not be included in the {@link FastqDataStore}.
	 * Only records which cause {@link DataStoreFilter#accept(String)}
	 * to return {@code true} will be added to this datastore.
   	 * @return a new instance of {@link FastqFileDataStore};
   	 * never null.
   	 * @throws IOException if the input fastq file does not exist or 
   	 * if there is a problem parsing the file.
   	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
   	 */
	public static FastqFileDataStore create(FastqParser parser,
			FastqQualityCodec qualityCodec, DataStoreFilter filter)
			throws IOException {
		MementoedFastqDataStoreBuilderVisitor visitor = new MementoedFastqDataStoreBuilderVisitor(parser, qualityCodec, filter);
    	
    	parser.parse(visitor);
    	return visitor.build();
	}

    
    
    
    
    private static final class MementoedFastqDataStoreBuilderVisitor extends AbstractFastqVisitor{
    	private final Map<String, FastqVisitorMemento> mementos = new LinkedHashMap<String,FastqVisitorMemento>();
    	private final FastqQualityCodec qualityCodec;
    	 private final FastqParser parser;
    	 private final DataStoreFilter filter;
    	 
    	 
		public MementoedFastqDataStoreBuilderVisitor(FastqParser parser,
				FastqQualityCodec qualityCodec, DataStoreFilter filter) {
			this.parser = parser;
			this.qualityCodec = qualityCodec;
			this.filter = filter;
		}
		public FastqDataStore build() {
			return new IndexedFastqFileDataStoreImpl(parser, qualityCodec, filter, mementos);
		}
		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.accept(id)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("can not create memento for " + id);
				}
				FastqVisitorMemento createMemento = callback.createMemento();
				mementos.put(id, createMemento);
			}
			//always skip record bodies
			return null;
		}
		
    }
    
    private static final class IndexedFastqFileDataStoreImpl implements FastqFileDataStore{
    	private final Map<String, FastqVisitorMemento> mementos;
    	private final FastqQualityCodec qualityCodec;
    	 private final FastqParser parser;
    	 private final DataStoreFilter filter;
    	 private volatile boolean closed;
    	 
    	 
    	public IndexedFastqFileDataStoreImpl(FastqParser parser,
				FastqQualityCodec qualityCodec,
				DataStoreFilter filter,
				Map<String, FastqVisitorMemento> mementos) {
			this.qualityCodec = qualityCodec;
			this.parser = parser;
			this.mementos = mementos;
			this.filter=filter;
		}
		@Override
        public StreamingIterator<String> idIterator() throws DataStoreException {
        	throwExceptionIfClosed();
            return DataStoreStreamingIterator.create(this,mementos.keySet().iterator());
        }
        @Override
        public FastqRecord get(String id) throws DataStoreException {
        	throwExceptionIfClosed();
        	FastqVisitorMemento memento =mementos.get(id);
            if(memento ==null){
            	//not in datastore
            	return null;
            }
            SingleFastqRecordVistior visitor = new SingleFastqRecordVistior();
            try {
				parser.parse(visitor, memento);
			} catch (IOException e) {
				 throw new DataStoreException("error reading fastq file",e);
			}
           return visitor.getRecord();            
        }
        @Override
        public boolean contains(String id) throws DataStoreException {
        	throwExceptionIfClosed();
            return mementos.containsKey(id);
        }
        @Override
        public long getNumberOfRecords() throws DataStoreException {
        	throwExceptionIfClosed();
            return mementos.size();
        }
        @Override
        public void close(){
        	closed=true;
        	mementos.clear();
            
        }
        
        private void throwExceptionIfClosed(){
        	if(closed){
        		throw new DataStoreClosedException("datastore is closed");
        	}
        }
        @Override
        public StreamingIterator<FastqRecord> iterator() throws DataStoreException {
        	throwExceptionIfClosed();
        	try {
        		StreamingIterator<FastqRecord> iter = LargeFastqFileDataStore.create(parser, qualityCodec, filter)
    					.iterator();
        		//iter has a different lifecylce than this datastore
        		//so wrap it
        		return DataStoreStreamingIterator.create(this,iter);
    		} catch (IOException e) {
    			throw new IllegalStateException("fastq file no longer exists! : ", e);
    		}
        }
        
        @Override
    	public StreamingIterator<DataStoreEntry<FastqRecord>> entryIterator()
    			throws DataStoreException {
        	throwExceptionIfClosed();
        	StreamingIterator<DataStoreEntry<FastqRecord>> iter = new StreamingIterator<DataStoreEntry<FastqRecord>>(){

        		StreamingIterator<FastqRecord> fastqs = iterator();
    			@Override
    			public boolean hasNext() {
    				return fastqs.hasNext();
    			}

    			@Override
    			public void close() {
    				fastqs.close();
    			}

    			@Override
    			public DataStoreEntry<FastqRecord> next() {
    				FastqRecord record = fastqs.next();
    				return new DataStoreEntry<FastqRecord>(record.getId(), record);
    			}

    			@Override
    			public void remove() {
    				throw new UnsupportedOperationException();
    			}
        		
        	};
    		return DataStoreStreamingIterator.create(this,iter);
    	}
        
        
        @Override
        public FastqQualityCodec getQualityCodec() {
            return qualityCodec;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isClosed() {
            return closed;
        }
        
        private class SingleFastqRecordVistior extends AbstractFastqVisitor{
        	private FastqRecord record;
    		@Override
    		public FastqRecordVisitor visitDefline(final FastqVisitorCallback callback,
    				String id, String optionalComment) {
    			//we assume the first record we get to
    			//is the one we want.
    			return new AbstractFastqRecordVisitor(id,optionalComment,qualityCodec) {
    				
    				@Override
    				protected void visitRecord(FastqRecord record) {
    					setRecord(record);
    					callback.haltParsing();    					
    				}
    			};
    		}

    		
			public final FastqRecord getRecord() {
				return record;
			}

			public final void setRecord(FastqRecord record) {
				this.record = record;
			}        	
        }
    }
}
