/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;

import org.jcvi.datastore.CachedDataStore;
import org.jcvi.datastore.DataStoreException;

public class LargePhdDataStoreFactory implements PhdDataStoreFactory{
    public static final int DEFAULT_CACHE_SIZE =2000;
    private final int cacheSize;
    
    public LargePhdDataStoreFactory(){
        this(DEFAULT_CACHE_SIZE);
    }
    /**
     * @param cacheSize
     */
    public LargePhdDataStoreFactory(int cacheSize) {
        this.cacheSize = cacheSize;
    }


    @Override
    public PhdDataStore createPhdDataStoreFactoryFor(File phdBall)
            throws DataStoreException {
        LargePhdDataStore datastore = new LargePhdDataStore(phdBall);
        
        return CachedDataStore.createCachedDataStore(PhdDataStore.class, datastore, cacheSize);
    }
}
