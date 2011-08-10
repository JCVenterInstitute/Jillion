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
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.AbstractBlockingCloseableIterator;
import org.jcvi.common.core.util.CloseableIterator;
import org.jcvi.common.core.util.DefaultIndexedFileRange;
import org.jcvi.common.core.util.IndexedFileRange;
/**
 * {@code IndexedAceFileDataStore} is an implementation of 
 * {@link AceContigDataStore} that only stores an index containing
 * file offsets to the various contigs contained
 * inside the ace file.  This allows large files to provide random 
 * access without taking up much memory.  The downside is each contig
 * must be re-parsed each time and the ace file must exist and not
 * get altered during the entire lifetime of this object.
 * @author dkatzel
 */
public final class IndexedAceFileDataStore implements AceContigDataStore{
    private final IndexedFileRange indexFileRange;
    private final File file;
    /**
     * Create a new empty {@link AceContigDataStoreBuilder}
     * that will create an {@link IndexedAceFileDataStore} 
     * once the builder has been built.  Only the 
     * given ace file should be used with to populate/index
     * the returned builder.
     * @param aceFile aceFile the aceFile to parse.  NOTE: 
     * the file isn't actually parsed in this method.  The builder
     * will only store a reference to this file for future
     * use when it needs to re-parse after indexing has occurred.
     * @return a new AceContigDataStoreBuilder, never null.
     * throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStoreBuilder createBuilder(File aceFile){
        return new IndexedAceFileDataStoreBuilder(aceFile);
    }
    /**
     * Create a new {@link AceContigDataStore} instance
     * for the contigs in the given aceFile.
     * @param aceFile the aceFile to parse.
     * @return a new  a new {@link AceContigDataStore}
     * that only stores an index containing file offsets to the various contigs contained
     * inside the ace file. 
     * @throws IOException if there is a problem reading the ace file
     * @throws NullPointerException if aceFile is null.
     */
    public static AceContigDataStore create(File aceFile) throws IOException{
        AceContigDataStoreBuilder builder = createBuilder(aceFile);
        AceFileParser.parseAceFile(aceFile, builder);
        return builder.build();
    }
    
    
    private IndexedAceFileDataStore(File file, IndexedFileRange indexFileRange){
        this.indexFileRange = indexFileRange;
        this.file = file;
    }
    
    private IndexedAceFileDataStore(File file){
        this(file, new DefaultIndexedFileRange());
    }

    
    @Override
    public boolean contains(String contigId) throws DataStoreException {
        return indexFileRange.contains(contigId);
    }

    @Override
    public AceContig get(String contigId) throws DataStoreException {
        Range range = indexFileRange.getRangeFor(contigId);
        InputStream inputStream=null;
        try {
            AceContigDataStoreBuilder builder = DefaultAceFileDataStore.createBuilder();
            inputStream = IOUtil.createInputStreamFromFile(file,range);
            AceFileParser.parseAceFile(inputStream,builder);
            return builder.build().get(contigId);
        } catch (Exception e) {
            throw new DataStoreException("error trying to get contig "+ contigId,e);
        }finally{
            IOUtil.closeAndIgnoreErrors(inputStream);
        }
    }

    @Override
    public CloseableIterator<String> getIds() {
        return indexFileRange.getIds();
    }

    @Override
    public int size() {
        return indexFileRange.size();
    }

    @Override
    public void close() throws IOException {
        indexFileRange.close();
        
    }

    

    @Override
    public CloseableIterator<AceContig> iterator() {
        AceFileDataStoreIterator iter= new AceFileDataStoreIterator();
        iter.start();
        return iter;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return indexFileRange.isClosed();
    }
    
    /**
     * Special implementation of a {@link CloseableIterator}
     * that directly parses the ace file.  This allows us
     * to iterate over the entire file in 1 pass.
     * @author dkatzel
     */
    private class AceFileDataStoreIterator extends AbstractBlockingCloseableIterator<AceContig>{

        /**
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() {
            AbstractAceContigBuilder builder = new AbstractAceContigBuilder() {
                
                @Override
                protected void visitContig(AceContig contig) {
                    AceFileDataStoreIterator.this.blockingPut(contig);
                    
                }

                /**
                * {@inheritDoc}
                */
                @Override
                public synchronized void visitEndOfFile() {
                    AceFileDataStoreIterator.this.finishedIterating();
                }
                
                
            };
            try {
                AceFileParser.parseAceFile(file, builder);
            } catch (IOException e) {
                // should never happen
                throw new RuntimeException("error iterating over ace file",e);
            }
            
        }
        
    }
    
    private static class IndexedAceFileDataStoreBuilder extends AbstractAceContigBuilder implements AceContigDataStoreBuilder{
        private IndexedFileRange indexFileRange;
        private final File aceFile;
        private int currentStartOffset;
        private int currentLineLength;
        private int currentFileOffset;
        
     
        public IndexedAceFileDataStoreBuilder(File aceFile){
            if(aceFile==null){
                throw new NullPointerException("ace file cannot be null");
            }           
            this.aceFile = aceFile;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized void visitHeader(int numberOfContigs,
                int totalNumberOfReads) {
            super.visitHeader(numberOfContigs, totalNumberOfReads);
            indexFileRange = new DefaultIndexedFileRange(numberOfContigs);
        }

        @Override
        public synchronized void visitLine(String line) {        
            super.visitLine(line);
            final int length = line.length();
            currentLineLength = length;
            currentFileOffset+=length;
            
        }

        @Override
        public synchronized void visitContigHeader(String contigId, int numberOfBases,
                int numberOfReads, int numberOfBaseSegments,
                boolean reverseComplimented) {
            super.visitContigHeader(contigId, numberOfBases, numberOfReads,
                    numberOfBaseSegments, reverseComplimented);
            currentStartOffset=currentFileOffset-currentLineLength;
        }

        @Override
        protected synchronized void visitContig(AceContig contig) {
            indexFileRange.put(contig.getId(), Range.buildRange(currentStartOffset, currentFileOffset));
            currentStartOffset=currentFileOffset+1;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public AceContigDataStore build() {
            return new IndexedAceFileDataStore(aceFile, indexFileRange);
        }

    }
}
