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
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.FastXFileVisitor;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.iter.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * {@code LargeFastqFileDataStore} is a {@link FastqDataStore} implementation
 * to be used a very large FastQ Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances of 
 * {@link LargeFastqFileDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 *
 */
public final class LargeFastqFileDataStore implements FastqDataStore {
    private final FastqQualityCodec qualityCodec;
    private final File fastQFile;
    private Integer size=null;
    private volatile boolean closed;
    /**
     * Create a new {@link FastqDataStore}
     * @param fastqFile
     * @param qualityCodec
     * @return
     * @throws FileNotFoundException 
     */
    public static FastqDataStore create(File fastqFile, FastqQualityCodec qualityCodec) throws FileNotFoundException{
    	return new LargeFastqFileDataStore(fastqFile, qualityCodec);
    }
    /**
     * @param qualityCodec
     * @throws FileNotFoundException 
     */
    private LargeFastqFileDataStore(File fastQFile, FastqQualityCodec qualityCodec) throws FileNotFoundException {
    	if(fastQFile==null){
    		throw new NullPointerException("fastq file can not be null");
    	}
    	if(qualityCodec==null){
    		throw new NullPointerException("qualityCodec can not be null");
    	}
    	if(!fastQFile.exists()){
    		throw new FileNotFoundException("could not find " + fastQFile.getAbsolutePath());
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
     public synchronized boolean isClosed() throws DataStoreException {
         return closed;
     }
    
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        CloseableIterator<FastqRecord> iter = iterator();
        while(iter.hasNext()){
            FastqRecord fastQ = iter.next();
            if(fastQ.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return true;
            }
        }
        return false;
    }
    private void throwExceptionIfClosed(){
        if(closed){
            throw new IllegalStateException("datastore is closed");
        }
    }
    @Override
    public synchronized FastqRecord get(String id) throws DataStoreException {
        if(closed){
            throw new DataStoreException("datastore is closed");
        }
        CloseableIterator<FastqRecord> iter = iterator();
        while(iter.hasNext()){
            FastqRecord fastQ = iter.next();
            if(fastQ.getId().equals(id)){
                IOUtil.closeAndIgnoreErrors(iter);
                return fastQ;
            }
        }
        throw new DataStoreException("could not find fastq record for "+id);
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        return new FastQIdIterator();        
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        if(size ==null){
            int count=0;
            CloseableIterator<FastqRecord> iter = iterator();
            while(iter.hasNext()){
                count++;
                iter.next();
            }
            size = Integer.valueOf(count);
        }
        return size;
    }
    
    @Override
    public synchronized CloseableIterator<FastqRecord> iterator() {
        throwExceptionIfClosed();
        LargeFastqFileIterator iter = new LargeFastqFileIterator();
    	iter.start();
    	
    	return iter;
  
    }
    
    
    private final class FastQIdIterator implements CloseableIterator<String>{
        private final CloseableIterator<FastqRecord> iter;
        private FastQIdIterator(){
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
    private final class LargeFastqFileIterator extends AbstractBlockingCloseableIterator<FastqRecord> implements CloseableIterator<FastqRecord>{


    	@Override
    	protected void backgroundThreadRunMethod() {
    		try {
            	FastqFileVisitor visitor = new AbstractFastqFileVisitor(qualityCodec) {
    				
            		 @Override
            	     protected FastXFileVisitor.EndOfBodyReturnCode visitFastQRecord(String id,
            	             NucleotideSequence nucleotides,
            	             QualitySequence qualities, String optionalComment) {
            	         FastqRecord record = new DefaultFastqRecord(id,nucleotides, qualities,optionalComment);
            	         blockingPut(record);
            	         return LargeFastqFileIterator.this.isClosed() ? FastXFileVisitor.EndOfBodyReturnCode.STOP_PARSING : FastXFileVisitor.EndOfBodyReturnCode.KEEP_PARSING;
            	     }
            		 @Override
            		    public FastXFileVisitor.DeflineReturnCode visitDefline(String id, String optionalComment) {
            		        super.visitDefline(id, optionalComment);
            		        return FastXFileVisitor.DeflineReturnCode.VISIT_CURRENT_RECORD;
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
