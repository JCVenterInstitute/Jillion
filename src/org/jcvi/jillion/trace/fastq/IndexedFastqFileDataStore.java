/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
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
   	 * instance using the given {@link FastqParser} which uses has its quality
   	 * values encoded in a manner that can be decoded by the given
   	 * {@link FastqQualityCodec} which only contains the records
   	 * in the file that are accepted by the given filter.
   	 * @param parser the {@link FastqParser} instance used to 
   	 * to create an {@link IndexedFastqFileDataStore}.
   	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file.
	 * @param filter a {@link Predicate} that will be used
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
	static FastqFileDataStore create(FastqParser parser,
			FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter)
			throws IOException {
		MementoedFastqDataStoreBuilderVisitor visitor = new MementoedFastqDataStoreBuilderVisitor(parser, qualityCodec,
		        filter, recordFilter);
    	
    	parser.parse(visitor);
    	return visitor.build();
	}

    
    
    
    
    private static final class MementoedFastqDataStoreBuilderVisitor extends AbstractFastqVisitor{
    	private final Map<String, FastqVisitorMemento> mementos = new LinkedHashMap<String,FastqVisitorMemento>();
    	private final FastqQualityCodec qualityCodec;
    	 private final FastqParser parser;
    	 private final Predicate<String> filter;
    	 private final Predicate<FastqRecord> recordFilter;
    	 
    	 
		public MementoedFastqDataStoreBuilderVisitor(FastqParser parser,
				FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter) {
			this.parser = parser;
			this.qualityCodec = qualityCodec;
			this.filter = filter;
			this.recordFilter = recordFilter;
		}
		public FastqFileDataStore build() {
			return new IndexedFastqFileDataStoreImpl(parser, qualityCodec, filter, recordFilter,mementos);
		}
		@Override
		public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
				String id, String optionalComment) {
			if(filter.test(id)){
				if(!callback.canCreateMemento()){
					throw new IllegalStateException("can not create memento for " + id);
				}
				FastqVisitorMemento createMemento = callback.createMemento();
				if(recordFilter ==null){
				    //no additional filtering
				   mementos.put(id, createMemento);
                                    
				    return null;
				}
				return new AbstractFastqRecordVisitor(id, optionalComment, qualityCodec) {
                                    
                                    @Override
                                    protected void visitRecord(FastqRecord record) {
                                        if(recordFilter.test(record)){
                                            mementos.put(id, createMemento);
                                        }
                                        
                                    }
                                };
				
			}
			//always skip record bodies
			return null;
		}
		
    }
    
    private static final class IndexedFastqFileDataStoreImpl implements FastqFileDataStore{
    	private final Map<String, FastqVisitorMemento> mementos;
    	private final FastqQualityCodec qualityCodec;
    	 private final FastqParser parser;
    	 private final Predicate<String> filter;
    	 private final Predicate<FastqRecord> recordFilter;
    	 private volatile boolean closed;
    	 
    	 
    	public IndexedFastqFileDataStoreImpl(FastqParser parser,
				FastqQualityCodec qualityCodec,
				Predicate<String> filter,
				Predicate<FastqRecord> recordFilter,
				Map<String, FastqVisitorMemento> mementos) {
			this.qualityCodec = qualityCodec;
			this.parser = parser;
			this.mementos = mementos;
			this.filter=filter;
			this.recordFilter = recordFilter;
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
        
        @Override
        public Optional<File> getFile() {
            return parser.getFile();
        }
        private void throwExceptionIfClosed(){
        	if(closed){
        		throw new DataStoreClosedException("datastore is closed");
        	}
        }
        
        
        @Override
        public <E extends Throwable> void forEach(ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E {
            throwExceptionIfClosed();
            LargeFastqFileDataStore.create(parser, qualityCodec, filter, recordFilter).forEach(consumer);
        }
        @Override
        public StreamingIterator<FastqRecord> iterator() throws DataStoreException {
        	throwExceptionIfClosed();
        	try {
        		StreamingIterator<FastqRecord> iter = LargeFastqFileDataStore.create(parser, qualityCodec, filter, recordFilter)
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
