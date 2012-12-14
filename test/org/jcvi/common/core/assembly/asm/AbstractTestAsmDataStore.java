package org.jcvi.common.core.assembly.asm;

import java.io.File;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.seq.trace.frg.FragmentDataStore;
import org.jcvi.common.io.fileServer.ResourceFileServer;

public abstract class AbstractTestAsmDataStore<D extends DataStore> {

	ResourceFileServer resources = new ResourceFileServer(AbstractTestAsmDataStore.class);
	
	protected abstract D createDataStore(File asmFile, FragmentDataStore frgDataStore) throws Exception;
	
}
