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
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.sanger.PositionSequence;
/**
 * {@code IndexedPhdFileDataStore} is an implementation of 
 * {@link PhdDataStore} that only stores an index containing
 * file offsets to the various phd records contained
 * inside the phdball file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is every time {@link #get(String)}
 * is called, the phd file must be re-read to seek to the appropriate
 * offset in order to re-parse the portion of the file that pertains
 * to that one particular phd record.
 * @author dkatzel
 *
 */
public final class IndexedPhdFileDataStore implements PhdDataStore{
    private final Map<String, Range> recordLocations;   
    private final File phdBall;
    private volatile boolean closed;
    /**
     * Create a new {@link PhdDataStoreBuilder} for the given
     * {@literal phd.ball} file.  The returned builder
     * can only parse one phd ball file.  If a second
     * phd file is parsed (or the same file parsed twice)
     * then the implementation will throw a IllegalStateException.
     * @param phdBall the {@literal phd.ball} to parse.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall is null.
     */
    public static PhdDataStoreBuilder createBuilder(File phdBall){
        return new IndexedPhdDataStoreBuilder(phdBall);
    }
    /**
     * Create a new {@link PhdDataStoreBuilder} for the given
     * {@literal phd.ball} file.  The returned builder
     * can only parse one phd ball file.  If a second
     * phd file is parsed (or the same file parsed twice)
     * then the implementation will throw a IllegalStateException.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param initialCapacity the initial capacity of the index used
     * to store/lookup file offsets into the phd file.  If the initialCapacity
     * is larger than the number of phd records parsed, then there will be no
     * performance hit to resize the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall is null.
     * @throws IllegalArgumentException if {@code initialCapacity < 0}
     */
    public static PhdDataStoreBuilder createBuilder(File phdBall,int initialCapacity){
        return new IndexedPhdDataStoreBuilder(phdBall,initialCapacity);
    }
    /**
     * Create a new {@link PhdDataStoreBuilder} for the given
     * {@literal phd.ball} file.  The returned builder
     * can only parse one phd ball file.  If a second
     * phd file is parsed (or the same file parsed twice)
     * then the implementation will throw a IllegalStateException.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param filter the {@link DataStoreFilter} to use to filter
     * which reads get stored in the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall or filter are null.
     */
    public static PhdDataStoreBuilder createBuilder(File phdBall, DataStoreFilter filter){
        return new IndexedPhdDataStoreBuilder(phdBall, filter);
    }
    /**
     * Create a new {@link PhdDataStoreBuilder} for the given
     * {@literal phd.ball} file.  The returned builder
     * can only parse one phd ball file.  If a second
     * phd file is parsed (or the same file parsed twice)
     * then the implementation will throw a IllegalStateException.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param filter the {@link DataStoreFilter} to use to filter
     * which reads get stored in the index.
     * @param initialCapacity the initial capacity of the index used
     * to store/lookup file offsets into the phd file.  If the initialCapacity
     * is larger than the number of phd records parsed, then there will be no
     * performance hit to resize the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall or filter are null.
     * @throws IllegalArgumentException if {@code initialCapacity < 0}
     */
    public static PhdDataStoreBuilder createBuilder(File phdBall, DataStoreFilter filter, int initialCapacity){
        return new IndexedPhdDataStoreBuilder(phdBall, filter,initialCapacity);
    }
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param initialCapacity the initial capacity of the index used
     * to store/lookup file offsets into the phd file.  If the initialCapacity
     * is larger than the number of phd records parsed, then there will be no
     * performance hit to resize the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall is null.
     */
    public static PhdDataStore create(File phdBall) throws FileNotFoundException{
        PhdDataStoreBuilder builder = createBuilder(phdBall);
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param initialCapacity the initial capacity of the index used
     * to store/lookup file offsets into the phd file.  If the initialCapacity
     * is larger than the number of phd records parsed, then there will be no
     * performance hit to resize the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall is null.
     * @throws IllegalArgumentException if {@code initialCapacity < 0}
     */
    public static PhdDataStore create(File phdBall, int initialCapacity) throws FileNotFoundException{
        PhdDataStoreBuilder builder = createBuilder(phdBall,initialCapacity);
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param filter the {@link DataStoreFilter} to use to filter
     * which reads get stored in the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall or filter are null.
     */
    public static PhdDataStore create(File phdBall,DataStoreFilter filter) throws FileNotFoundException{
        PhdDataStoreBuilder builder = createBuilder(phdBall,filter);
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    
    /**
     * Create a new {@link PhdDataStore} for the given
     * {@literal phd.ball} file.
     * @param phdBall the {@literal phd.ball} to parse.
     * @param filter the {@link DataStoreFilter} to use to filter
     * which reads get stored in the index.
     * @param initialCapacity the initial capacity of the index used
     * to store/lookup file offsets into the phd file.  If the initialCapacity
     * is larger than the number of phd records parsed, then there will be no
     * performance hit to resize the index.
     * @return a new IndexedPhdFileDataStore; never null.
     * @throws FileNotFoundException if the given file
     * does not exist.
     * @throws NullPointerException if phdBall or filter are null.
     * @throws IllegalArgumentException if {@code initialCapacity < 0}
     */
    public static PhdDataStore create(File phdBall,DataStoreFilter filter, int initialCapacity) throws FileNotFoundException{
        PhdDataStoreBuilder builder = createBuilder(phdBall,filter,initialCapacity);
        PhdParser.parsePhd(phdBall, builder);
        return builder.build();
    }
    
    private IndexedPhdFileDataStore(File phdBall,Map<String,Range> recordLocations){
        this.recordLocations = recordLocations;
        this.phdBall = phdBall;        
    }
    
    private static final class IndexedPhdDataStoreBuilder extends AbstractPhdDataStoreBuilder{
        private final Map<String,Range> recordLocations;
        private long currentStartOffset=0;
        private long currentOffset=currentStartOffset;
        private final File phdBall;
        private int currentLineLength;
        private boolean firstPhd=true;
        
        
        private IndexedPhdDataStoreBuilder(File phdBall) {   
            super();
            this.phdBall = phdBall;
            this.recordLocations = new LinkedHashMap<String, Range>();
            
        }
        private IndexedPhdDataStoreBuilder(File phdBall, int initialSizeOfIndex){
            super();
            int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(initialSizeOfIndex);
            this.phdBall = phdBall;
            this.recordLocations = new LinkedHashMap<String, Range>(capacity);
        }
        private IndexedPhdDataStoreBuilder(File phdBall,DataStoreFilter filter) {
            super(filter);
            this.phdBall = phdBall;
            this.recordLocations = new LinkedHashMap<String, Range>();
        }
        
        private IndexedPhdDataStoreBuilder(File phdBall,DataStoreFilter filter, int initialSizeOfIndex){
            super(filter);
            this.phdBall = phdBall;
            int capacity = MapUtil.computeMinHashMapSizeWithoutRehashing(initialSizeOfIndex);
            this.recordLocations = new LinkedHashMap<String, Range>(capacity);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitFile() {
            if(!firstPhd){
                throw new IllegalStateException("can only read 1 phd (or phd.ball) file");
            }
            firstPhd=false;
            super.visitFile();
            
        }
        @Override
        public synchronized void visitLine(String line) {
            super.visitLine(line);
            currentLineLength = line.length();
            currentOffset +=currentLineLength;
        }
        
        @Override
        protected synchronized boolean visitPhd(String id, NucleotideSequence bases,
                QualitySequence qualities, PositionSequence positions,
                Properties comments, List<PhdTag> tags) {
            long endOfOldRecord = currentOffset-currentLineLength-1;
            recordLocations.put(id, Range.of(currentStartOffset,endOfOldRecord));
            currentStartOffset=endOfOldRecord;
            return true;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PhdDataStore build() {
            return new IndexedPhdFileDataStore(phdBall, recordLocations);
        }
        
        
    }
    
    

    @Override
    public boolean contains(String id) throws DataStoreException {
    	throwExceptionIfClosed();
        return recordLocations.containsKey(id);
    }

    @Override
    public Phd get(String id) throws DataStoreException {
    	throwExceptionIfClosed();
        FileChannel fastaFileChannel=null;
        PhdDataStore dataStore=null;
        InputStream in=null;
        FileInputStream fileInputStream=null;
        try{
            Range range = recordLocations.get(id);
            if(range ==null){
            	 throw new DataStoreException(id +" does not exist");
            }
            in = IOUtil.createInputStreamFromFile(phdBall, (int)range.getBegin(), (int)range.getLength());  
            
            PhdDataStoreBuilder builder =  DefaultPhdFileDataStore.createBuilder();            
            PhdParser.parsePhd(in, builder);
            dataStore = builder.build();
            return dataStore.get(id);
            
        } catch (IOException e) {
           throw new DataStoreException("error getting "+ id, e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(fastaFileChannel);
            IOUtil.closeAndIgnoreErrors(dataStore);
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(fileInputStream);
        }
    }

    private void throwExceptionIfClosed(){
    	if(closed){
    		throw new IllegalStateException("datastore is closed");
    	}
    }
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
    	throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,recordLocations.keySet().iterator());
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
    	throwExceptionIfClosed();
        return recordLocations.size();
    }

    @Override
    public synchronized void close() throws IOException {        
        closed=true;      
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<Phd> iterator() {
    	throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,new IndexedIterator());
    }
    /**
     * Wrapper around {@link LargePhdIterator} to filter
     * out any phds that we don't have in our
     * index.
     * @author dkatzel
     */
    private class IndexedIterator implements StreamingIterator<Phd>{
        private final LargePhdIterator iterator = LargePhdIterator.createNewIterator(phdBall);
        private final Object endOfIterator= new Object();
        private Object next;
        
        public IndexedIterator(){
            updateNext();
        }
        private void updateNext(){
            Object newNext=endOfIterator;
            while(iterator.hasNext() && newNext ==endOfIterator){
                //need to check if this phd
                //is in our records (if not, then we skip it)
                Phd nextCandidate =iterator.next();
                if(recordLocations.containsKey(nextCandidate.getId())){
                    newNext=nextCandidate;
                }
            }
            next= newNext;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
           throw new UnsupportedOperationException("remove not supported");
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized boolean hasNext() {
            return next !=endOfIterator;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void close() throws IOException {
            iterator.close();
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized Phd next() {
            if(!hasNext()){
                throw new NoSuchElementException("no more elements in iterator");
            }
            Phd ret = (Phd)next;
            updateNext();
            return ret;
        }
    
        
    }



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (closed ? 1231 : 1237);
		result = prime * result + phdBall.hashCode();
		result = prime * result
				+ recordLocations.hashCode();
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IndexedPhdFileDataStore)) {
			return false;
		}
		IndexedPhdFileDataStore other = (IndexedPhdFileDataStore) obj;
		if (closed != other.closed) {
			return false;
		}
		if (!phdBall.equals(other.phdBall)) {
			return false;
		}
		if (!recordLocations.equals(other.recordLocations)) {
			return false;
		}
		return true;
	}
    
    

}
