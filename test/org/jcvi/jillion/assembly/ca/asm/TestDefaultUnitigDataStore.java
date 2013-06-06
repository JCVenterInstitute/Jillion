package org.jcvi.jillion.assembly.ca.asm;

import java.io.File;

import org.jcvi.jillion.assembly.ca.frg.FragmentDataStore;

public class TestDefaultUnitigDataStore extends AbstractTestAsmUnitigDataStore{

	@Override
	protected AsmUnitigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return new AsmFileUnitigDataStoreBuilder(asmFile, frgDataStore)
					.build();
	}

}
