package org.jcvi.common.core.seq.fastx.fasta.aa.impl;


import java.io.File;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreStreamingIterator;
import org.jcvi.common.core.seq.fastx.fasta.AbstractLargeFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.aa.AminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.aa.AminoAcidSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code LargeAminoAcidSequenceFastaFileDataStore} is an implementation
 * of {@link AminoAcidSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in {@link CachedDataStore}.
 * @author dkatzel
 */
public final class LargeAminoAcidSequenceFastaFileDataStore extends AbstractLargeFastaFileDataStore<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord> implements AminoAcidSequenceFastaDataStore{
	
	
	
    /**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static AminoAcidSequenceFastaDataStore create(File fastaFile){
		return create(fastaFile, AcceptingDataStoreFilter.INSTANCE);
	}
	/**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static AminoAcidSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter){
		return new LargeAminoAcidSequenceFastaFileDataStore(fastaFile,filter);
	}
   
    protected LargeAminoAcidSequenceFastaFileDataStore(File fastaFile,
			DataStoreFilter filter) {
		super(fastaFile, filter);
	}


	@Override
	protected StreamingIterator<AminoAcidSequenceFastaRecord> createNewIterator(
			File fastaFile) {
		return DataStoreStreamingIterator.create(this,LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile));
	       
	}
   
   
}
