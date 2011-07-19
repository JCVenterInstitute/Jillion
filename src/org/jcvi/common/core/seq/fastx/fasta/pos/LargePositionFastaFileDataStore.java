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

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.LargeFastaIdIterator;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaRecordFactory;
import org.jcvi.common.core.seq.fastx.fasta.nuc.LargeNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaRecordFactory;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;
import org.jcvi.common.core.util.CloseableIterator;


public class LargePositionFastaFileDataStore extends AbstractPositionFastaFileDataStore{

private final File fastaFile;

private Integer size;
/**
 * Construct a {@link LargeNucleotideFastaFileDataStore}
 * for the given Fasta file and the given {@link NucleotideFastaRecordFactory}.
 * @param fastaFile the Fasta File to use, can not be null.
 * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
 * @throws NullPointerException if fastaFile is null.
 */
public LargePositionFastaFileDataStore(File fastaFile,
        PositionFastaRecordFactory fastaRecordFactory) {
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
public LargePositionFastaFileDataStore(File fastaFile) {
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
	CloseableIterator<PositionFastaRecord<Sequence<ShortGlyph>>> iter =iterator();
	while(iter.hasNext()){
		PositionFastaRecord<Sequence<ShortGlyph>> fasta = iter.next();
		if(fasta.getId().equals(id)){
			IOUtil.closeAndIgnoreErrors(iter);
			return true;
		}
	}
	 return false;
   
}

@Override
public synchronized PositionFastaRecord<Sequence<ShortGlyph>> get(String id)
        throws DataStoreException {
	
	CloseableIterator<PositionFastaRecord<Sequence<ShortGlyph>>> iter =iterator();
	while(iter.hasNext()){
		PositionFastaRecord<Sequence<ShortGlyph>> fasta = iter.next();
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
public synchronized CloseableIterator<PositionFastaRecord<Sequence<ShortGlyph>>> iterator() {
    checkNotYetClosed();
    LargePositionFastaRecordIterator iter= new LargePositionFastaRecordIterator(fastaFile);
        iter.start();
        return iter;
   
    
}

}
