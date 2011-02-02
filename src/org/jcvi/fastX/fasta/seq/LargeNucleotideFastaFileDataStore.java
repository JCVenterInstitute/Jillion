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
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fastX.fasta.seq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fasta.FastaParser;
import org.jcvi.fastX.fasta.SequenceFastaRecordUtil;
import org.jcvi.io.IOUtil;
import org.jcvi.util.AbstractLargeIdIterator;
import org.jcvi.util.CloseableIterator;
/**
 * {@code LargeNucleotideFastaFileDataStore} is an implementation
 * of {@link AbstractNucleotideFastaFileDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 *
 *
 */
public class LargeNucleotideFastaFileDataStore extends AbstractNucleotideFastaFileDataStore{
    private static final Pattern NEXT_ID_PATTERN = Pattern.compile("^>(\\S+)");
    private final File fastaFile;

    private Integer size;
    
   
    /**
     * Construct a {@link LargeNucleotideFastaFileDataStore}
     * for the given Fasta file and the given {@link NucleotideFastaRecordFactory}.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
    public LargeNucleotideFastaFileDataStore(File fastaFile,
            NucleotideFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    /**
     * Convenience constructor using the {@link DefaultNucleotideFastaRecordFactory}.
     * This call is the same as {@link #LargeNucleotideFastaFileDataStore(File,NucleotideFastaRecordFactory)
     * new LargeNucleotideFastaFileDataStore(fastaFile,DefaultNucleotideFastaRecordFactory.getInstance());}
     * @see LargeNucleotideFastaFileDataStore#LargeQualityFastaFileDataStore(File, NucleotideFastaRecordFactory)
     */
    public LargeNucleotideFastaFileDataStore(File fastaFile) {
        super();
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    
    @Override
    public boolean visitRecord(String id, String comment, String entireBody) { 
        return true;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        try {
            in= getRecordFor(id);
            return in !=null;
        } catch (FileNotFoundException e) {
           throw new DataStoreException("could not get record for "+id,e);
        }finally{
           IOUtil.closeAndIgnoreErrors(in);
        }
    }

    @Override
    public synchronized NucleotideSequenceFastaRecord get(String id)
            throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        DefaultNucleotideFastaFileDataStore datastore=null;
        try {
            in = getRecordFor(id);
        
            if(in ==null){
                return null;
            }
      
            datastore= new DefaultNucleotideFastaFileDataStore(getFastaRecordFactory());
            FastaParser.parseFasta(in, datastore);
            
            return datastore.get(id);
           
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get record for "+id, e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in,datastore);
        }
    }

    @Override
    public synchronized CloseableIterator<String> getIds() throws DataStoreException {
        checkNotYetClosed();
        try {
            return new LargeFastaIdIterator();
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not get id iterator",e);
        }
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                Scanner scanner = new Scanner(fastaFile);
                int counter =0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    Matcher matcher = NEXT_ID_PATTERN.matcher(line);
                    if(matcher.find()){
                        counter++;
                    }
                }
                size= counter;            
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("could not get record count");
            }
        }   
        return size;

    }

    @Override
    public synchronized CloseableIterator<NucleotideSequenceFastaRecord> iterator() {
        checkNotYetClosed();
        try {
            return new FastaIterator();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getRecordFor(String id) throws FileNotFoundException{
        Scanner scanner = null;
        try{
            scanner = new Scanner(fastaFile);
            String expectedHeader = String.format(">%s", id);
            String line = scanner.nextLine();
            boolean done=false;
            //we have to do this while loop to make sure we find
            //the actual read instead of a different read which is happens
            //to include our id as a prefix (for example a TIGR "B" read
            while(!done){
                if(line.startsWith(expectedHeader)){
                    String currentId= SequenceFastaRecordUtil.parseIdentifierFromIdLine(line);
                    if(id.equals(currentId)){
                        done=true;
                        //done
                        continue;
                    }
                }
                if(!scanner.hasNextLine()){
                    done=true;
                    continue;
                }
                line = scanner.nextLine(); 
            }
            
            if(!scanner.hasNextLine()){
                return null;
            }
            StringBuilder record = new StringBuilder(line).append("\n");
            line =scanner.nextLine();
            while(!line.startsWith(">") && scanner.hasNextLine()){
                record.append(line).append("\n");
                line = scanner.nextLine();
            }
            //add final line if needed
            if(!scanner.hasNextLine()){
                record.append(line).append("\n");
            }
            return new ByteArrayInputStream(record.toString().getBytes());
        }finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }


    private class LargeFastaIdIterator extends AbstractLargeIdIterator{
        protected LargeFastaIdIterator() throws FileNotFoundException {
            super(fastaFile);
        }

        @Override
        protected void advanceToNextId(Scanner scanner) {
            //bo-op
            
        }

        @Override
        protected Object getNextId(Scanner scanner) {
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                Matcher matcher = NEXT_ID_PATTERN.matcher(line);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }            
            return getEndOfIterating();
        }
        
    }
    
    private class FastaIterator extends AbstractNucleotideFastaVisitor implements CloseableIterator<NucleotideSequenceFastaRecord>{
        private Object endOfFileToken = new Object();
        private BlockingQueue<Object> queue = new LinkedBlockingQueue<Object>(1);
        private Object nextRecord=null;
        private boolean isClosed=false;
        private FastaIterator() throws InterruptedException{
            new Thread(){

                @Override
                public void run() {
                    try {
                        FastaParser.parseFasta(fastaFile, FastaIterator.this);
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
        public boolean hasNext() {
            return nextRecord !=endOfFileToken;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceFastaRecord next() {
            if(!hasNext()){
                throw new NoSuchElementException("no more fasta records");
            }
            NucleotideSequenceFastaRecord next = (NucleotideSequenceFastaRecord)nextRecord;
            try {
                blockingGetNextRecord();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return next;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void remove() {
            // TODO Auto-generated method stub
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected boolean visitNucleotideFastaRecord(
                NucleotideSequenceFastaRecord fastaRecord) {
            blockingPut(fastaRecord);
            return isClosed;
            
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
         
    }
}
