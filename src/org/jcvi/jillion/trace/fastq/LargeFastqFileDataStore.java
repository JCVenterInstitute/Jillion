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
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
/**
 * {@code LargeFastqFileDataStore} is a {@link FastqDataStore} implementation
 * to be used a very large Fastq Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each call to {@link FastqDataStore#get(String)}
 * or {@link FastqDataStore#contains(String)} requires re-parsing the fastq file
 * which can take some time.  
 * It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, DataStore, int)}.
 * @author dkatzel
 *
 *
 */
final class LargeFastqFileDataStore implements FastqFileDataStore {
    private final FastqQualityCodec qualityCodec;
    private final FastqParser parser;
    
    private Long size=null;
    private volatile boolean closed;
    private final Predicate<String> filter;
    private final Predicate<FastqRecord> recordFilter;
    
    /**
     * Create a new {@link FastqDataStore} instance for the given
     * fastqFile which will contain all the
     * records in the file.  This implementation will use the given
     * {@link FastqQualityCodec} to decode the qualities of the fastq record
     * (if provided)This should return
     * the same data store implementation as
     * {@link #create(File, DataStoreFilter, FastqQualityCodec) create(fastqFile, qualityCodec, DataStoreFilters.alwaysAccept())}
     * @param fastqFile the fastq file to create a {@link FastqDataStore}
     * for (can not be null, and must exist).
     * @param qualityCodec the {@link FastqQualityCodec} that should be used
     * to decode the fastq file.  If this value is null, then 
     * the datastore implementation will try to guess the codec used which might
     * have a performance penalty associated with it.
     * 
     * @return a new {@link FastqFileDataStore} instance, will never be null.
     * 
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     * @see #create(File, FastqQualityCodec, DataStoreFilter)
     */
    public static FastqFileDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws IOException{
    	return create(fastqFile, qualityCodec, DataStoreFilters.alwaysAccept());
    }
    /**
     * Create a new {@link FastqDataStore} instance for the given
     * fastqFile which will only contain all the
     * records in the file that are accepted by the given filter.  
     * This implementation will use the given
     * {@link FastqQualityCodec} to decode the qualities of the fastq record
     * (if provided)
     * @param fastqFile the fastq file to create a {@link FastqDataStore}
     * for (can not be null, and must exist).
     * @param filter the {@link Predicate} used to filter out records
     * from the datastore by ID. If this value is null,
     * then all records in the file will be included in the datastore.
     * @param qualityCodec the {@link FastqQualityCodec} that should be used
     * to decode the fastq file.  If this value is null, then 
     * the datastore implementation will try to guess the codec used which might
     * have a performance penalty associated with it.
     * @return a new {@link FastqFileDataStore} instance, will never be null.
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     */
    public static FastqFileDataStore create(File fastqFile, FastqQualityCodec qualityCodec, Predicate<String> filter) throws IOException{
    	if(fastqFile==null){
    		throw new NullPointerException("fastq file can not be null");
    	} 
    	return new LargeFastqFileDataStore(FastqFileParser.create(fastqFile), qualityCodec,filter, record->true);
    }
  
    /**
     * Create a new {@link FastqDataStore} instance for the given
     * {@link FastqParser} which will only contain all the
     * records in the file that are accepted by the given filter.  
     * This implementation will use the given
     * {@link FastqQualityCodec} to decode the qualities of the fastq record
     * (if provided)
     * @param parser the {@link FastqParser} instance
     * that will parse some fastq data source that will be parsed
     * to create a {@link FastqDataStore}; (can not be null, and must exist).
     * @param filter the {@link Predicate} used to filter out records
     * from the datastore. If this value is null,
     * then all records in the file will be included in the datastore.
     * @param qualityCodec the {@link FastqQualityCodec} that should be used
     * to decode the fastq file.  If this value is null, then 
     * the datastore implementation will try to guess the codec used which might
     * have a performance penalty associated with it.
     * @return a new {@link FastqDataStore} instance, will never be null.
     * @throws NullPointerException if any parameter is null.
     */
    public static FastqFileDataStore create(FastqParser parser, FastqQualityCodec qualityCodec, Predicate<String> filter, Predicate<FastqRecord> recordFilter) throws IOException{
    	
    	return new LargeFastqFileDataStore(parser, qualityCodec,filter, recordFilter);
    }
    /**
     * @param qualityCodec
     * @throws FileNotFoundException 
     */
    private LargeFastqFileDataStore(FastqParser parser, FastqQualityCodec qualityCodec,Predicate<String> filter, Predicate<FastqRecord> recordFilter) throws IOException {
    	if(parser==null){
    		throw new NullPointerException("parser can not be null");
    	}    	
    	if(filter==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	
    	this.parser = parser;
    	this.filter = filter;
        this.qualityCodec = qualityCodec;   
        this.recordFilter = recordFilter;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;        
    }
    
    @Override
    public FastqQualityCodec getQualityCodec() {
        return qualityCodec;
    }
    /**
     * {@inheritDoc}
     */
     @Override
     public synchronized boolean isClosed() {
         return closed;
     }
    
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        if(!filter.test(id)){
        	return false;
        }
        StreamingIterator<FastqRecord> iter =null;        
        try{
        	iter= iterator();
        	while(iter.hasNext()){
                FastqRecord fastQ = iter.next();
                if(fastQ.getId().equals(id)){                    
                    return true;
                }
            }
            return false;
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }
    private void throwExceptionIfClosed(){
        if(closed){
            throw new DataStoreClosedException("datastore is closed");
        }
    }
    @Override
    public synchronized FastqRecord get(String id) throws DataStoreException {
    	 throwExceptionIfClosed();
        if(!filter.test(id)){
        	return null;
        }
        StreamingIterator<FastqRecord> iter =null;
        try{
        	iter= iterator();
        	while(iter.hasNext()){
                FastqRecord fastQ = iter.next();
                if(fastQ.getId().equals(id)){                    
                    return fastQ;
                }
            }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        //not found
       return null;
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        throwExceptionIfClosed();
        FastqIdIterator iterator = new FastqIdIterator();
        iterator.start();
	return DataStoreStreamingIterator.create(this,iterator);        
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        throwExceptionIfClosed();
        if(size ==null){
            long count=0;
            StreamingIterator<FastqRecord> iter = iterator();
            while(iter.hasNext()){
                count++;
                iter.next();
            }
            size = Long.valueOf(count);
        }
        return size;
    }
    
    @Override
    public synchronized StreamingIterator<FastqRecord> iterator() {
        throwExceptionIfClosed();
        LargeFastqFileIterator iter = new LargeFastqFileIterator();
    	iter.start();
    	
    	return DataStoreStreamingIterator.create(this,iter);
  
    }
    
    
    @Override
	public synchronized StreamingIterator<DataStoreEntry<FastqRecord>> entryIterator()
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


	private final class FastqIdIterator extends AbstractBlockingStreamingIterator<String> implements StreamingIterator<String>{

    	


		@Override
    	protected void backgroundThreadRunMethod() {
    		try {
    			
    			FastqVisitor visitor = new FastqVisitor() {
					
					@Override
					public void visitEnd() {
						//no-op						
					}
					@Override
					public void halted(){
						//no-op
			    	}
					@Override
					public FastqRecordVisitor visitDefline(final FastqVisitorCallback callback,
							String id, String optionalComment) {
						if(FastqIdIterator.this.isClosed()){
							callback.haltParsing();							
						}
						if (filter.test(id)) {
						    //our record filter can still filter it out..
						    if(recordFilter ==null){
						        blockingPut(id);
                                                        if(FastqIdIterator.this.isClosed()){
                                                            callback.haltParsing();                                                 
                                                        }
                                                        return null;
						    }
						    return new AbstractFastqRecordVisitor(id, optionalComment, qualityCodec) {
                                                        
                                                        @Override
                                                        protected void visitRecord(FastqRecord record) {
                                                            if(recordFilter.test(record)){
                                                                blockingPut(id);
                                                                if(FastqIdIterator.this.isClosed()){
                                                                    callback.haltParsing();                                                 
                                                                }
                                                            }
                                                            
                                                        }
                                                    };
							
						}
						
						return null;
					}
				};
    			
               parser.parse(visitor);
           } catch (IOException e) {
                
                //should never happen
                throw new RuntimeException(e);
            }
    		
    	}
    }
    
    /**
     * {@code LargeFastQFileIterator} is an Iterator of {@link FastqRecord}s meant for large
     * fastq files (although small fastqs will work too).
     * @author dkatzel
     *
     *
     */
    private final class LargeFastqFileIterator extends AbstractBlockingStreamingIterator<FastqRecord> implements StreamingIterator<FastqRecord>{

    	


		@Override
    	protected void backgroundThreadRunMethod() {
    		try {
    			
    			FastqVisitor visitor = new FastqVisitor() {
					
					@Override
					public void visitEnd() {
						//no-op						
					}
					@Override
					public void halted(){
						//no-op
			    	}
					@Override
					public FastqRecordVisitor visitDefline(final FastqVisitorCallback callback,
							String id, String optionalComment) {
						if(LargeFastqFileIterator.this.isClosed()){
							callback.haltParsing();
							return null;
						}
						if (filter.test(id)) {
							//don't waste CPU time saving a few bytes of memory
							//since we are only iterating through
							//and will probably throw the read away after reading it once.
							callback.turnOffDataCompression(true);
							return new AbstractFastqRecordVisitor(id,optionalComment,qualityCodec, true) {
								
								@Override
								protected void visitRecord(FastqRecord record) {
								        if(recordFilter ==null || recordFilter.test(record)){
        									blockingPut(record);
        									if(LargeFastqFileIterator.this.isClosed()){
        										callback.haltParsing();
        									}
								        }
								}
							};
						}
						return null;
					}
				};
    			
                parser.parse(visitor);
           } catch (IOException e) {
                
                //should never happen
                throw new RuntimeException(e);
            }
    		
    	}
    }
  
}
