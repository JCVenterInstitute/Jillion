package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestLargeIndexedAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

    public TestLargeIndexedAceFileDataStore() throws IOException, DataStoreException {
        super();        
    }

    @BeforeClass
    public static void turnOffMemoryMapFlag(){
    	IndexedAceFileDataStore.allowMemoryMapping(false);
    }
    @AfterClass
    public static void turnOnMemoryMapFlag(){
    	IndexedAceFileDataStore.allowMemoryMapping(true);
    }
	@Override
	protected AceFileContigDataStore createDataStoreFor(File aceFile)
			throws IOException {
		return IndexedAceFileDataStore.create(aceFile);
	}

}
