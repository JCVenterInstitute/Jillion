package org.jcvi.jillion.assembly.consed.phd;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.phd.LargePhdballDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilters;

public class TestLargePhdDataStore2 extends AbstractTestPhdDataStore{

    @Override
    protected PhdDataStore createPhdDataStore(File phdfile) throws IOException{
        return new LargePhdballDataStore(phdfile, DataStoreFilters.alwaysAccept());
    }

}
