package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;

import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

public class TestIndexedUnitigDataStore extends AbstractTestAsmUnitigDataStore{

	@Override
	protected AsmUnitigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return new AsmFileUnitigDataStoreBuilder(asmFile, frgDataStore)
					.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
					.build();
	}

}
