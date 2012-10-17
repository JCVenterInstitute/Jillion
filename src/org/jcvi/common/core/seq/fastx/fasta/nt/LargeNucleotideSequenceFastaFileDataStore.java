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
package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.FastaUtil;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 */
final class LargeNucleotideSequenceFastaFileDataStore implements NucleotideSequenceFastaDataStore{

    private final File fastaFile;
    private final DataStoreFilter filter;
    private Long size;
    private volatile boolean closed=false;
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideSequenceFastaDataStore create(File fastaFile){
		return create(fastaFile, AcceptingDataStoreFilter.INSTANCE);
	}
	 /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter){
		return new LargeNucleotideSequenceFastaFileDataStore(fastaFile, filter);
	}
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
    private LargeNucleotideSequenceFastaFileDataStore(File fastaFile, DataStoreFilter filter) {
        if(fastaFile ==null){
            throw new NullPointerException("fasta file can not be null");
        }
        if(filter ==null){
            throw new NullPointerException("filter file can not be null");
        }
        this.filter =filter;
        this.fastaFile = fastaFile;
    }
    
    private void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("already closed");
        }
    }
    @Override
    public  void close() throws IOException {
        closed=true;
        
    }

    @Override
    public  boolean isClosed() {
        return closed;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        return get(id)!=null;
    }

    @Override
    public NucleotideSequenceFastaRecord get(String id)
            throws DataStoreException {
        StreamingIterator<NucleotideSequenceFastaRecord> iter = iterator();
        try{
	        while(iter.hasNext()){
	        	NucleotideSequenceFastaRecord next = iter.next();
	        	if(next.getId().equals(id)){
	        		return next;
	        	}
	        }
	        //we get here if we didn't find it
	        return null;
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }

    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,LargeFastaIdIterator.createNewIteratorFor(fastaFile,filter));
        
    }

    @Override
    public synchronized long getNumberOfRecords() throws DataStoreException {
        checkNotYetClosed();
        if(size ==null){
            try {
                size= countFilteredIds();            
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("could not get record count");
            }
        }   
        return size;

    }
	private long countFilteredIds() throws FileNotFoundException {
		Scanner scanner = new Scanner(fastaFile, IOUtil.UTF_8_NAME);
		long counter =0;
		while(scanner.hasNextLine()){
		    String line = scanner.nextLine();
		    
		    Matcher matcher = FastaUtil.ID_LINE_PATTERN.matcher(line);
		    if(matcher.find()){
		    	String id = matcher.group(1);
		    	final boolean accept;
		    	if(filter instanceof FastXFilter){
		    		accept = ((FastXFilter)filter).accept(id, matcher.group(2));
		    	}else{
		    		accept = filter.accept(id);
		    	}
		    	if(accept){
		    		counter++;
		    	}
		    }
		}
		return counter;
	}

    @Override
    public StreamingIterator<NucleotideSequenceFastaRecord> iterator() {
        checkNotYetClosed();
        return DataStoreStreamingIterator.create(this,
        		LargeNucleotideSequenceFastaIterator.createNewIteratorFor(fastaFile));
       
    }
   


   
}
