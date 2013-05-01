package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.phd.IndexedPhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestIndexedPhdDataStore2 extends AbstractTestPhdDataStore{

    @Override
    protected PhdDataStore createPhdDataStore(File phdfile) throws IOException{
        return IndexedPhdDataStore.create(phdfile, DataStoreFilters.alwaysAccept());
    }

}
