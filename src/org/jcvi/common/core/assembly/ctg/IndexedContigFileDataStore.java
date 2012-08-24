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
 * Created on Apr 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code IndexedContigFileDataStore} is an implementation of 
 * {@link ContigDataStore} that only stores an index containing
 * file offsets to the various contigs contained
 * inside a contig file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time.
 * @author dkatzel
 *
 *
 */
public class IndexedContigFileDataStore implements ContigDataStore<AssembledRead, Contig<AssembledRead>>{

    private final File file;
    private final Map<String, Range> mappedRanges;
    private volatile boolean closed;
    /**
     * Construct an new instance of IndexedContigFileDataStore and create
     * the internal index.
     * @param file the contig file to create the datastore for.
     * @throws FileNotFoundException if the contig file does not exist.
     */
    public IndexedContigFileDataStore(File file) throws FileNotFoundException{
        this.file = file;
        this.mappedRanges = new LinkedHashMap<String, Range>();
        ContigFileParser.parse(file,
                                        new IndexedContigFileVisitor(mappedRanges));
        
    }
    
    private void throwExceptionIfClosed(){
    	if(closed){
    		throw new IllegalStateException("datastore is closed");
    	}
    }
    @Override
    public boolean contains(String contigId) throws DataStoreException {
    	throwExceptionIfClosed();
        return mappedRanges.containsKey(contigId);
    }

    @Override
    public Contig<AssembledRead> get(String contigId)
            throws DataStoreException {
    	throwExceptionIfClosed();
        Range range = mappedRanges.get(contigId);
        if(range==null){
        	throw new DataStoreException(contigId + " does not exist");
        }
        InputStream inputStream=null;
        try {
            SingleContigFileVisitor visitor = new SingleContigFileVisitor();
            inputStream = IOUtil.createInputStreamFromFile(file,range);
            ContigFileParser.parse(inputStream,visitor);
            return visitor.getContigToReturn();
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }
    

    @Override
    public void close() throws IOException {
        closed=true;       
    }
    
    @Override
    public StreamingIterator<String> idIterator() {
    	throwExceptionIfClosed();
        return DataStoreStreamingIterator.create(this,mappedRanges.keySet().iterator());
    }
    @Override
    public long getNumberOfRecords() {
    	throwExceptionIfClosed();
        return mappedRanges.size();
    }
    
    private static class SingleContigFileVisitor extends AbstractContigFileVisitorBuilder{
        private Contig<AssembledRead> contigToReturn;

        @Override
        protected synchronized void addContig(Contig<AssembledRead>  contig) {
            if(contigToReturn !=null){
                throw new IllegalStateException("can not add more than one contig");
            }
            contigToReturn= contig;
        }
        public Contig<AssembledRead>  getContigToReturn(){
            return contigToReturn;
        }

    }
    
    
    private static class IndexedContigFileVisitor extends AbstractContigFileVisitorBuilder{

        private int sizeOfCurrentContig;
        private int currentStartOffset;
        private int currentLineLength;
        private final Map<String, Range> mappedRanges;
        
        IndexedContigFileVisitor(Map<String, Range> mappedRanges){
            resetCurrentContigSize(0);
            this.mappedRanges = mappedRanges;
        }
        private void resetCurrentContigSize(int defLineSize){
            sizeOfCurrentContig=defLineSize;
        }
        @Override
        public void visitLine(String line) {
            super.visitLine(line);
            currentLineLength= line.length();
            sizeOfCurrentContig+=currentLineLength;
        }

        @Override
        protected void addContig(Contig<AssembledRead> contig) {
            int actualLengthOfContig = sizeOfCurrentContig-currentLineLength;
            mappedRanges.put(contig.getId(), Range.createOfLength(currentStartOffset, actualLengthOfContig));
            currentStartOffset+=actualLengthOfContig;
            resetCurrentContigSize(currentLineLength);
        }
        
    }


    @Override
    public StreamingIterator<Contig<AssembledRead>> iterator() {
    	throwExceptionIfClosed();
        return new DataStoreIterator<Contig<AssembledRead>>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() {
        return closed;
    }
    
}
