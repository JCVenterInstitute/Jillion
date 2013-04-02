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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreClosedException;
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
final class LargeFastqFileDataStore implements FastqDataStore {
    private final FastqQualityCodec qualityCodec;
    private final File fastQFile;
    private Long size=null;
    private volatile boolean closed;
    private final DataStoreFilter filter;
    
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
     * @return a new {@link FastqDataStore} instance, will never be null.
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     * @see #create(File, DataStoreFilter, FastqQualityCodec)
     */
    public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws FileNotFoundException{
    	return new LargeFastqFileDataStore(fastqFile, qualityCodec,DataStoreFilters.alwaysAccept());
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
     * @param filter the {@link DataStoreFilter} used to filter out records
     * from the datastore. If this value is null,
     * then all records in the file will be included in the datastore.
     * @param qualityCodec the {@link FastqQualityCodec} that should be used
     * to decode the fastq file.  If this value is null, then 
     * the datastore implementation will try to guess the codec used which might
     * have a performance penalty associated with it.
     * @return a new {@link FastqDataStore} instance, will never be null.
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     */
    public static FastqDataStore create(File fastqFile, DataStoreFilter filter, FastqQualityCodec qualityCodec) throws FileNotFoundException{
    	return new LargeFastqFileDataStore(fastqFile, qualityCodec,filter);
    }
    /**
     * @param qualityCodec
     * @throws FileNotFoundException 
     */
    private LargeFastqFileDataStore(File fastQFile, FastqQualityCodec qualityCodec,DataStoreFilter filter) throws FileNotFoundException {
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
        this.qualityCodec = qualityCodec;
        this.fastQFile = fastQFile;        
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
    			
                FastqFileParser.create(fastQFile).accept(visitor);
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
							return new AbstractFastqRecordVisitor(id,optionalComment,qualityCodec) {
								
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
    			
                FastqFileParser.create(fastQFile).accept(visitor);
           } catch (IOException e) {
                
                //should never happen
                throw new RuntimeException(e);
            }
    		
    	}
    }
  
}
