/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.trace.fastq.AbstractFastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqParser;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;

/**
 * {@code IndexedBinaryFastqFileDataStore} is an implementation of 
 * {@link FastqDataStore} that only stores an index containing
 * file offsets to the various {@link FastqRecord}s contained
 * inside the fastq file.  This implementation provides random access
 * to large files taking up much memory.  The downside is each fastq record
 * must be seeked to and then re-parsed each time and the fastq file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
final class IndexedBinaryFastqFileDataStore{
    
    /**
   	 * Creates a new {@link IndexedBinaryFastqFileDataStore}
   	 * instance using the given fastqFile which uses has its quality
   	 * values encoded in a manner that can be decoded by the given
   	 * {@link FastqQualityCodec} which only contains the records
   	 * in the file that are accepted by the given filter.
   	 * @param file the fastq file to create an {@link IndexedBinaryFastqFileDataStore}
   	 * for.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @param filter a {@link DataStoreFilter} that will be used
	 * to filter out some (possibly all or none) of the records from
	 * the fastq file so they will not be included in the {@link FastqDataStore}.
	 * Only records which cause {@link DataStoreFilter#accept(String)}
	 * to return {@code true} will be added to this datastore.
   	 * @return a new instance of {@link FastqDataStore};
   	 * never null.
   	 * @throws IOException if the input fastq file does not exist or 
   	 * if there is a problem parsing the file.
   	 * @throws NullPointerException if the input fastq file or the {@link FastqQualityCodec} is null.
   	 */
    public static FastqDataStore create(File file, DataStoreFilter filter, ByteOrder endian) throws IOException{
    	IndexedFastqFileDataStoreBuilderVisitor2 visitor = new IndexedFastqFileDataStoreBuilderVisitor2(file, filter, endian);
    	FastqParser parser = BinaryFastqFileParser.create(file, endian);
    	parser.parse(visitor);
    	return visitor.build(parser);
    }

    
    
    
    
    private static final class IndexedFastqFileDataStoreBuilderVisitor2 implements FastqVisitor{
    	private final Map<String, FastqVisitorMemento> mementos = new LinkedHashMap<String,FastqVisitorMemento>();
    	 private final File file;
    	 private final DataStoreFilter filter;
    	 private final ByteOrder endian;
    	 
		public IndexedFastqFileDataStoreBuilderVisitor2(File file, DataStoreFilter filter, ByteOrder endian) {
			this.file = file;
			this.endian = endian;
			this.filter = filter;
		}
		public FastqDataStore build(FastqParser parser) {
			return new IndexedFastqFileDataStoreImpl(file,  parser, filter,endian, mementos);
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
		@Override
		public void visitEnd() {
			//no-op			
		}
		@Override
		public void halted(){
			//no-op
    	}
    }
    
    private static final class IndexedFastqFileDataStoreImpl implements FastqDataStore{
    	private final Map<String, FastqVisitorMemento> mementos;
    	 private final File file;
    	 private final FastqParser parser;
    	 private final DataStoreFilter filter;
    	 private volatile boolean closed;
    	 private final ByteOrder endian;
    	 
    	public IndexedFastqFileDataStoreImpl(File file,
				FastqParser parser,
				DataStoreFilter filter,
				ByteOrder endian,
				Map<String, FastqVisitorMemento> mementos) {
			this.file = file;
			this.parser = parser;
			this.mementos = mementos;
			this.filter=filter;
			this.endian = endian;
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
        		StreamingIterator<FastqRecord> iter = LargeBinaryFastqFileDataStore.create(file, filter, endian)
    					.iterator();
        		//iter has a different lifecylce than this datastore
        		//so wrap it
        		return DataStoreStreamingIterator.create(this,iter);
    		} catch (FileNotFoundException e) {
    			throw new IllegalStateException("fastq file no longer exists! : "+ file.getAbsolutePath(), e);
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
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isClosed() {
            return closed;
        }
        
        private class SingleFastqRecordVistior implements FastqVisitor{
        	private FastqRecord record;
    		@Override
    		public FastqRecordVisitor visitDefline(final FastqVisitorCallback callback,
    				String id, String optionalComment) {
    			//we assume the first record we get to
    			//is the one we want.
    			return new AbstractFastqRecordVisitor(id,optionalComment,FastqQualityCodec.ILLUMINA) {
    				
    				@Override
    				protected void visitRecord(FastqRecord record) {
    					setRecord(record);
    					callback.haltParsing();    					
    				}
    			};
    		}

    		@Override
    		public void visitEnd() {
    			//no-op			
    		}
    		@Override
    		public void halted(){
    			//no-op
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
