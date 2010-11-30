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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
import org.jcvi.util.AbstractLargeIdIterator;
import org.jcvi.util.CloseableIterator;
/**
 * {@code LargeQualityFastaFileDataStore} is an implementation
 * of {@link AbstractQualityFastaFileDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 *
 *
 */
public class LargeQualityFastaFileDataStore extends AbstractQualityFastaFileDataStore implements QualityFastaDataStore{
    private static final Pattern NEXT_ID_PATTERN = Pattern.compile("^>(\\S+)");
    private final File fastaFile;

    private Integer size;
    private boolean closed;
    
    private synchronized void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    /**
     * Construct a {@link LargeQualityFastaFileDataStore}
     * for the given Fasta file and the given {@link QualityFastaRecordFactory}.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the QualityFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
    public LargeQualityFastaFileDataStore(File fastaFile,
            QualityFastaRecordFactory fastaRecordFactory) {
        super(fastaRecordFactory);
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        this.fastaFile = fastaFile;
    }
    /**
     * Convenience constructor using the {@link DefaultQualityFastaRecordFactory}.
     * This call is the same as {@link #LargeQualityFastaFileDataStore(File,QualityFastaRecordFactory)
     * new LargeQualityFastaFileDataStore(fastaFile,DefaultQualityFastaRecordFactory.getInstance());}
     * @see LargeQualityFastaFileDataStore#LargeQualityFastaFileDataStore(File, QualityFastaRecordFactory)
     */
    public LargeQualityFastaFileDataStore(File fastaFile) {
        super();
        this.fastaFile = fastaFile;
    }
    
    @Override
    public boolean visitRecord(String id, String comment, String entireBody) {
        return true;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        try {
            return getRecordFor(id)!=null;
        } catch (FileNotFoundException e) {
           throw new DataStoreException("could not get record for "+id,e);
        }
    }

    @Override
    public synchronized QualityFastaRecord get(String id)
            throws DataStoreException {
        checkNotYetClosed();
        InputStream in=null;
        DefaultQualityFastaFileDataStore datastore=null;
        try {
            in = getRecordFor(id);
        
        if(in ==null){
            return null;
        }
        datastore = new DefaultQualityFastaFileDataStore(getFastaRecordFactory());
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
        Scanner scanner=null;
        try {
            if(size ==null){
           
                scanner = new Scanner(fastaFile);
                int counter =0;
                while(scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    Matcher matcher = NEXT_ID_PATTERN.matcher(line);
                    if(matcher.find()){
                        counter++;
                    }
                }
                size= counter;           
            
            } 
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("could not get record count");
        }finally{
            IOUtil.closeAndIgnoreErrors(scanner);
        }
        return size;

    }

    @Override
    public synchronized void close() throws IOException {
        closed =true;
        
    }

    @Override
    public synchronized CloseableIterator<QualityFastaRecord> iterator() {
        checkNotYetClosed();
        return new FastaIterator();
    }

    private InputStream getRecordFor(String id) throws FileNotFoundException{
        Scanner scanner=null;
        try{
            scanner= new Scanner(fastaFile);
        
            String expectedHeader = String.format(">%s", id);
            String line = scanner.nextLine();
            
            while(!line.startsWith(expectedHeader) && scanner.hasNextLine()){
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
            //no-op
            
        }

        @Override
        protected Object getNextId(Scanner scanner) {
            
            String block= scanner.findWithinHorizon(NEXT_ID_PATTERN, 0);
            if(block !=null){
                Matcher matcher = NEXT_ID_PATTERN.matcher(block);
                if(matcher.find()){
                    return matcher.group(1);
                }
            }
            return getEndOfIterating();
        }
        
    }
    
    private class FastaIterator implements CloseableIterator<QualityFastaRecord>{
        private final CloseableIterator<String> identifierIterator;

        private FastaIterator(){
            try {
                identifierIterator = getIds();
            } catch (DataStoreException e) {
               throw new IllegalStateException("could not get id iterator",e);
            }
        }
         @Override
         public boolean hasNext() {
             return identifierIterator.hasNext();
         }
     
         @Override
         public QualityFastaRecord next() {
             try {
                return get(identifierIterator.next());
            } catch (DataStoreException e) {
               throw new IllegalStateException("could not get next fasta record",e);
            }
         }
     
         @Override
         public void remove() {
             throw new UnsupportedOperationException("can not remove from iterator");
             
         }
        /**
        * {@inheritDoc}
        */
        @Override
        public void close() throws IOException {
            identifierIterator.close();
            
        }

    }
}
