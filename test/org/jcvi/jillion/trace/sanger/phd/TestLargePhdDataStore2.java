package org.jcvi.jillion.trace.sanger.phd;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestLargePhdDataStore2 extends AbstractTestPhdDataStore{

    @Override
    protected PhdDataStore createPhdDataStore(File phdfile) throws IOException{
        return new LargePhdballDataStore2(phdfile, DataStoreFilters.alwaysAccept());
    }

}
