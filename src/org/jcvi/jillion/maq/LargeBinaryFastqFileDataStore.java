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
/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.maq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.trace.fastq.AbstractFastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordVisitor;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
/**
 * {@code LargeBinaryFastqFileDataStore} is a {@link FastqDataStore} implementation
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
final class LargeBinaryFastqFileDataStore implements FastqDataStore {
    private final File bfqFile;
    private Long size=null;
    private volatile boolean closed;
    private final DataStoreFilter filter;
    private final ByteOrder endian;
 
    /**
     * Create a new {@link FastqDataStore} instance for the given
     * fastqFile which will only contain all the
     * records in the file that are accepted by the given filter.  
     * This implementation will use the given
     * {@link FastqQualityCodec} to decode the qualities of the fastq record
     * (if provided)
     * @param fastqFile the fastq file to create a {@link FastqDataStore}
     * for (can not be null, and must exist).
     * @param filter the {@link DataStoreFilter} used to filter out records
     * from the datastore. If this value is null,
     * then all records in the file will be included in the datastore.
     * 
     * @return a new {@link FastqDataStore} instance, will never be null.
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     */
    public static FastqDataStore create(File fastqFile, DataStoreFilter filter, ByteOrder endian) throws FileNotFoundException{
    	return new LargeBinaryFastqFileDataStore(fastqFile, filter, endian);
    }
    /**
     * @param qualityCodec
     * @throws FileNotFoundException 
     */
    private LargeBinaryFastqFileDataStore(File fastQFile,DataStoreFilter filter, ByteOrder endian) throws FileNotFoundException {
    	if(fastQFile==null){
    		throw new NullPointerException("fastq file can not be null");
    	}
    	if(!fastQFile.exists()){
    		throw new FileNotFoundException("could not find " + fastQFile.getAbsolutePath());
    	}
    	if(filter==null){
    		throw new NullPointerException("filter can not be null");
    	}
    	
    	this.filter = filter;
        this.bfqFile = fastQFile;  
        this.endian = endian;
    }

    @Override
    public synchronized void close() throws IOException {
        closed = true;        
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
        if(!filter.accept(id)){
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
        if(!filter.accept(id)){
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
        FastqIdIterator iterator = new FastqIdIterator(filter);
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
        LargeFastqFileIterator iter = new LargeFastqFileIterator(filter);
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

    	private final DataStoreFilter filter;
    	

    	public FastqIdIterator(DataStoreFilter filter) {
			this.filter = filter;
		}


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
						if (filter.accept(id)) {
							blockingPut(id);
						}
						if(FastqIdIterator.this.isClosed()){
							callback.haltParsing();							
						}
						return null;
					}
				};    			
               
                BinaryFastqFileParser.create(bfqFile, endian).parse(visitor);
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

    	private final DataStoreFilter filter;
    	

    	public LargeFastqFileIterator(DataStoreFilter filter) {
			this.filter = filter;
		}


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
						if (filter.accept(id)) {
							//don't use the quality codec but need to provide one
							//to compile...
							return new AbstractFastqRecordVisitor(id,optionalComment,FastqQualityCodec.ILLUMINA) {
								
								@Override
								protected void visitRecord(FastqRecord record) {
									blockingPut(record);
									if(LargeFastqFileIterator.this.isClosed()){
										callback.haltParsing();
									}
								}
							};
						}
						return null;
					}
				};
    			
                BinaryFastqFileParser.create(bfqFile, endian).parse(visitor);
           } catch (IOException e) {
                
                //should never happen
                throw new RuntimeException(e);
            }
    		
    	}
    }
  
}
