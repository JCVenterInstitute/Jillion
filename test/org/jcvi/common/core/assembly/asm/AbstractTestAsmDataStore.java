package org.jcvi.common.core.assembly.asm;

import java.io.File;

import org.jcvi.common.core.seq.trace.frg.FragmentDataStore;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.internal.ResourceHelper;

public abstract class AbstractTestAsmDataStore<D extends DataStore> {

	ResourceHelper resources = new ResourceHelper(AbstractTestAsmDataStore.class);
	
	protected abstract D createDataStore(File asmFile, FragmentDataStore frgDataStore) throws Exception;
	
}
