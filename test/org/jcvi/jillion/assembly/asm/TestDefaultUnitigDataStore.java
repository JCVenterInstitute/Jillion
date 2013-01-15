package org.jcvi.jillion.assembly.asm;

import java.io.File;

import org.jcvi.jillion.assembly.asm.DefaultUnitigDataStore;
import org.jcvi.jillion.assembly.asm.UnitigDataStore;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

public class TestDefaultUnitigDataStore extends AbstractTestAsmUnitigDataStore{

	@Override
	protected UnitigDataStore createDataStore(File asmFile,
			FragmentDataStore frgDataStore) throws Exception {
		return DefaultUnitigDataStore.create(asmFile, frgDataStore);
	}

}
