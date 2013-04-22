package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestDefaultPhdDataStore2 extends AbstractTestPhdDataStore{

    @Override
    protected PhdDataStore createPhdDataStore(File phdfile) throws IOException{
        return DefaultPhdDataStore.create(phdfile, DataStoreFilters.alwaysAccept());
    }
}
