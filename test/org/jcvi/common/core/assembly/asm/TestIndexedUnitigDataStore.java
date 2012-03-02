package org.jcvi.common.core.assembly.asm;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.frg.FragmentDataStore;

public class TestIndexedUnitigDataStore extends AbstractTestAsmUnitigDataStore{

	@Override
	protected UnitigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return IndexedAsmUnitigDataStore.createDataStore(asmFile, frgDataStore);
	}

}
