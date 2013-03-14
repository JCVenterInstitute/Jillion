package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;

public interface CasGappedReferenceDataStore extends NucleotideSequenceDataStore{

	NucleotideSequence getReferenceByIndex(long index) throws DataStoreException;
	
	String getIdByIndex(long index);
	
	long getIndexById(String id);
}
