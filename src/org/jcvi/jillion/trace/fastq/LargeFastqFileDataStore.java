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
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.util.iter.AbstractBlockingStreamingIterator;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code LargeFastqFileDataStore} is a {@link FastqDataStore} implementation
 * to be used a very large Fastq Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each call to {@link FastqDataStore#get(String)}
 * or {@link FastqDataStore#contains(String)} requires re-parsing the fastq file
 * which can take some time.  
 * It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.common.core.datastore.DataStore, int)}.
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
     * {@link #create(File, FastXFilter, FastqQualityCodec) create(fastqFile, qualityCodec, null)}
     * @param fastqFile the fastq file to create a {@link FastqDataStore}
     * for (can not be null, and must exist).
     * @param qualityCodec the {@link FastqQualityCodec} that should be used
     * to decode the fastq file.  If this value is null, then 
     * the datastore implementation will try to guess the codec used which might
     * have a performance penalty associated with it.
     * @return a new {@link FastqDataStore} instance, will never be null.
     * @throws FileNotFoundException if the given Fastq file does not exist.
     * @throws NullPointerException if fastqFile is null.
     * @see #create(File, FastXFilter, FastqQualityCodec)
     */
    public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws FileNotFoundException{
    	return new LargeFastqFileDataStore(fastqFile, qualityCodec,null);
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
     * @param filter the {@link FastXFilter} used to filter out records
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
    		this.filter = DataStoreFilters.alwaysAccept();
    	}else{
    		this.filter = filter;
    	}
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
            throw new IllegalStateException("datastore is closed");
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
        
        throw new DataStoreException("could not find fastq record for "+id);
    }

    @Override
    public synchronized StreamingIterator<String> idIterator() throws DataStoreException {
        throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,new FastqIdIterator());        
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
    
    
    private final class FastqIdIterator implements StreamingIterator<String>{
        private final StreamingIterator<FastqRecord> iter;
        private FastqIdIterator(){
                iter = iterator();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String next() {
            return iter.next().getId();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
        	throw new UnsupportedOperationException();	           
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            iter.close();            
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
    			/**
    			 * This visitor implementation will put each
    			 * non-filtered record into the blocking queue.
    			 * 
    			 */
            	FastqFileVisitor visitor = new FastqFileVisitor() {
            		private String currentId, currentComment;

            	    private NucleotideSequence nucleotides;
            	    private QualitySequence qualities;
            	    
            	    @Override
					public FastqFileVisitor.DeflineReturnCode visitDefline(
							String id, String optionalComment) {
						currentId = id;
						currentComment = optionalComment;
						if (filter.accept(id)) {
							return FastqFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD;
						}
						return FastqFileVisitor.DeflineReturnCode.SKIP_CURRENT_RECORD;
					}
            	    
            	    @Override
            	    public void visitNucleotides(NucleotideSequence nucleotides) {
            	        this.nucleotides = nucleotides;            	        
            	    }
            	    @Override
            	    public void visitEncodedQualities(String encodedQualities) {
            	    	this.qualities = qualityCodec.decode(encodedQualities);        
            	    }
            	    @Override
            	    public FastqFileVisitor.EndOfBodyReturnCode visitEndOfBody() {
            	    	 FastqRecord record = new FastqRecordBuilder(currentId,nucleotides, qualities)
            	    	 						.comment(currentComment)
            	    	 						.build();
               	         blockingPut(record);
               	         return LargeFastqFileIterator.this.isClosed() ? FastqFileVisitor.EndOfBodyReturnCode.STOP_PARSING : FastqFileVisitor.EndOfBodyReturnCode.KEEP_PARSING;
               	  
            	    }
            	    @Override
            	    public void visitEndOfFile() { 
            	    	//no-op
            	    }

            	    @Override
            	    public void visitLine(String line) {
            	    	//no-op
            	    }

            	    @Override
            	    public void visitFile() {       
            	    	//no-op
            	    }
            	};
                FastqFileParser.parse(fastQFile, visitor);
           } catch (IOException e) {
                
                //should never happen
                throw new RuntimeException(e);
            }
    		
    	}
    }
  
}
