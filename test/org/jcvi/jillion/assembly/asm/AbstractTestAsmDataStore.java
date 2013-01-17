package org.jcvi.jillion.assembly.asm;

import java.io.File;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.frg.FragmentDataStore;

public abstract class AbstractTestAsmDataStore<D extends DataStore> {

	ResourceHelper resources = new ResourceHelper(AbstractTestAsmDataStore.class);
	
	protected abstract D createDataStore(File asmFile, FragmentDataStore frgDataStore) throws Exception;
	
}
