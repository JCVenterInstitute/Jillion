package org.jcvi.common.core.assembly.asm;

import java.io.File;

import org.jcvi.common.core.seq.trace.frg.FragmentDataStore;

public class TestDefaultUnitigDataStore extends AbstractTestAsmUnitigDataStore{

	@Override
	protected UnitigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return DefaultUnitigDataStore.create(asmFile, frgDataStore);
	}

}
