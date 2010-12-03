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
package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.AbstractLargeIdIterator;
import org.jcvi.util.CloseableIterator;
/**
 * {@code LargeFastQFileDataStore} is a {@link FastQDataStore} implementation
 * to be used a very large FastQ Files.  No data contained in this
 * fastq file is stored in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances of 
 * {@link LargeFastQFileDataStore} are wrapped by {@link CachedDataStore}
 * @author dkatzel
 *
 *
 */
public class LargeFastQFileDataStore extends AbstractFastQFileDataStore<FastQRecord> {
    private static final Pattern BEGIN_SEQ_PATTERN = Pattern.compile("^@(\\S+)(\\s+)?(.+$)?");
    private final File fastQFile;
    private Integer size=null;
    
    /**
     * @param qualityCodec
     */
    public LargeFastQFileDataStore(File fastQFile, FastQQualityCodec qualityCodec) {
        super(qualityCodec);
        this.fastQFile = fastQFile;        
    }

    @Override
    protected void visitFastQRecord(String id,
            NucleotideEncodedGlyphs nucleotides,
            EncodedGlyphs<PhredQuality> qualities, String optionalComment) {
        
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return get(id)!=null;
    }

    @Override
    public FastQRecord get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        try {
            
            DefaultFastQFileDataStore dataStore = new SingleFastQDataStore(id,this.getQualityCodec());
            FastQFileParser.parse(fastQFile, dataStore);
            return dataStore.get(id);
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse fasta q file",e);
        }
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        try {
            return new FastQIdIterator();
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse fast q file",e);
        }
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        if(size !=null){
            return size;
        }
        int count=0;
        Scanner scanner;
        try {
            scanner = new Scanner(fastQFile);
        
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("@")){
                count++;
            }
        }
        size = count;
        return size;
        } catch (FileNotFoundException e) {
            size = 0;
            throw new DataStoreException("could not determine size",e);
        }
    }
    
    @Override
    public CloseableIterator<FastQRecord> iterator() {
        try {
            return new FastQIterator();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
    
    private class FastQIterator extends AbstractFastQFileVisitor<FastQRecord> implements CloseableIterator<FastQRecord>{

        private Object endOfFileToken = new Object();
        private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(1);
        private Object nextRecord=null;
        private boolean isClosed=false;
        private String currentId = null;
        private String currentComment=null;
        private NucleotideEncodedGlyphs currentBasecalls;
        private EncodedGlyphs<PhredQuality> currentQualities;
        private 
        FastQIterator() throws InterruptedException{
            new Thread(){

                @Override
                public void run() {
                    try {
                        FastQFileParser.parse(fastQFile, FastQIterator.this);
                    } catch (FileNotFoundException e) {
                        //should never happen
                        throw new RuntimeException(e);
                    }
                }
                
            }.start();
            blockingGetNextRecord();
        }
        /**
         * @throws InterruptedException 
         * 
         */
        private void blockingGetNextRecord() throws InterruptedException {
            nextRecord = queue.take();            
        }
        @Override
        public boolean visitBeginBlock(String id, String optionalComment) {
            currentId=id;
            currentComment=optionalComment;
            return !isClosed;
        }
        @Override
        public void visitEndOfFile() {
            blockingPut(endOfFileToken);
        }
        private void blockingPut(Object obj){
            try {
                queue.put(obj);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        @Override
        public void visitEncodedQualities(String encodedQualities) {
            currentQualities = qualityCodec.decode(encodedQualities);
        }
        @Override
        public void visitEndBlock() {
            FastQRecord record = new DefaultFastQRecord(currentId,currentBasecalls, currentQualities,currentComment);
            blockingPut(record);
        }
        @Override
        public void visitNucleotides(NucleotideEncodedGlyphs nucleotides) {
            currentBasecalls = nucleotides;
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
        public boolean hasNext() {
            return nextRecord !=endOfFileToken;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            isClosed=true;
            nextRecord=endOfFileToken;
            //remove element from queue
            queue.poll();            
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public FastQRecord next() {
            if(!hasNext()){
                throw new NoSuchElementException("no more fasta records");
            }
            FastQRecord next = (FastQRecord)nextRecord;
            try {
                blockingGetNextRecord();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return next;
        }
        
        
    }
    private class FastQIdIterator extends AbstractLargeIdIterator{

        private FastQIdIterator() throws FileNotFoundException{
                super(fastQFile);
        }
        
        
        @Override
        protected void advanceToNextId(Scanner scanner) {
          //skip basecalls line
            scanner.nextLine();
            //skip begin quality block
            scanner.nextLine();
            //skip qualities line
            scanner.nextLine();
            
        }


        @Override
        protected String getNextId(Scanner scanner) {
            Matcher matcher = BEGIN_SEQ_PATTERN.matcher(scanner.nextLine());
            matcher.find();
            return  matcher.group(1);
        }


        
        
    }
    
    private static final class SingleFastQDataStore extends DefaultFastQFileDataStore{

        private final String idToLookFor;
        private boolean found=false;
        
        public SingleFastQDataStore(String idToLookFor,FastQQualityCodec qualityCodec) {
            super(qualityCodec);
            this.idToLookFor = idToLookFor;
        }
        @Override
        public boolean visitBeginBlock(String id, String optionalComment) {
            if(!found && idToLookFor.equals(id)){
                found=true;
                return super.visitBeginBlock(id, optionalComment);
            }
            return !found;
        }
        
    }
}
