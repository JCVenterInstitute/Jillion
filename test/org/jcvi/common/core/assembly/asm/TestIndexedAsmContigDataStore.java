package org.jcvi.common.core.assembly.asm;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.frg.FragmentDataStore;

public class TestIndexedAsmContigDataStore extends AbstractTestAsmContigDataStore{

	@Override
	protected AsmContigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return IndexedAsmContigDataStore.createDataStore(asmFile, frgDataStore);
	}

}
