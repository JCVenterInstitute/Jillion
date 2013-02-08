package org.jcvi.jillion.assembly.asm;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.trace.frg.Fragment;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

public class TestIndexedAsmFileContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws IOException {
		return IndexedAsmFileContigDataStore.create(asmFile, DataStoreUtil.adapt(NucleotideSequenceDataStore.class, frgDataStore, 
				new DataStoreUtil.AdapterCallback<Fragment, NucleotideSequence>() {

					@Override
					public NucleotideSequence get(Fragment from) {
						return from.getNucleotideSequence();
					}
			
		}),
		DataStoreFilters.alwaysAccept());
	}

}
