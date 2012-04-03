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
 * Created on Jan 28, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.io.File;

import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code LargePositionFastaFileDataStore} is an implementation
 * of {@link PositionFastaFileDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 */
public final class LargePositionFastaFileDataStore extends AbstractPositionFastaFileDataStore{

	private final File fastaFile;
	
	private Integer size;
	/**
	 * Create a new instance of a {@link LargePositionFastaFileDataStore}.
	 * @param fastaFile the fastaFile to create a datastore of.
	 * @return a new instance; never null.
	 */
	public static PositionFastaDataStore create(File fastaFile){
		return new LargePositionFastaFileDataStore(fastaFile);
	}
	/**
	 * Convenience constructor using the default {@link PositionFastaRecordFactoryde}.
	 * This call is the same as {@link #LargePositionFastaFileDataStore(File,PositionFastaRecordFactory)
	 * new LargePositionFastaFileDataStore(fastaFile,DefaultPositionFastaRecordFactory.getInstance());}
	 * @see LargePositionFastaFileDataStore#LargePositionFastaFileDataStore(File, PositionFastaRecordFactory)
	 */
	private LargePositionFastaFileDataStore(File fastaFile) {
	    super();
	    if(fastaFile ==null){
	        throw new NullPointerException("fasta file can not be null");
	    }
	    this.fastaFile = fastaFile;
	}
	
	
	
	@Override
	public EndOfBodyReturnCode visitEndOfBody() {
		return EndOfBodyReturnCode.KEEP_PARSING;
	}
	@Override
	public boolean contains(String id) throws DataStoreException {
		CloseableIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iter =iterator();
		while(iter.hasNext()){
			PositionSequenceFastaRecord<Sequence<ShortSymbol>> fasta = iter.next();
			if(fasta.getId().equals(id)){
				IOUtil.closeAndIgnoreErrors(iter);
				return true;
			}
		}
		 return false;
	   
	}
	
	@Override
	public synchronized PositionSequenceFastaRecord<Sequence<ShortSymbol>> get(String id)
	        throws DataStoreException {
		
		CloseableIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iter =iterator();
		while(iter.hasNext()){
			PositionSequenceFastaRecord<Sequence<ShortSymbol>> fasta = iter.next();
			if(fasta.getId().equals(id)){
				IOUtil.closeAndIgnoreErrors(iter);
				return fasta;
			}
		}
		 throw new DataStoreException("could not get record for "+id);
	   
	   
	}
	
	@Override
	public synchronized CloseableIterator<String> getIds() throws DataStoreException {
	    checkNotYetClosed();
	    return  LargeFastaIdIterator.createNewIteratorFor(fastaFile);
	}
	
	@Override
	public synchronized int size() throws DataStoreException {
	    checkNotYetClosed();
	    if(size ==null){
	    	CloseableIterator<String> ids = getIds();
	    	int count=0;
	    	while(ids.hasNext()){
	    		ids.next();
	    		count++;
	    	}
	    	size=count;
	    }   
	    return size;
	
	}
	
	
	@Override
	public synchronized CloseableIterator<PositionSequenceFastaRecord<Sequence<ShortSymbol>>> iterator() {
	    checkNotYetClosed();
	    LargePositionFastaRecordIterator iter= new LargePositionFastaRecordIterator(fastaFile);
	        iter.start();
	        return iter;
	   
	    
	}

}
