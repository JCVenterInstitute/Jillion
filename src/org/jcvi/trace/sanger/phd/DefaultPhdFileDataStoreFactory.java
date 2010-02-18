/*
 * Created on Jan 25, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
/**
 * {@code DefaultPhdDataStoreFactory} is an implementation
 * of {@link PhdDataStoreFactory} that creates {@link DefaultPhdFileDataStore}s.
 * @author dkatzel
 *
 *
 */
public class DefaultPhdFileDataStoreFactory implements PhdDataStoreFactory {

    @Override
    public PhdDataStore createPhdDataStoreFactoryFor(File phdBall)
            throws DataStoreException {
        DefaultPhdFileDataStore datastore = new DefaultPhdFileDataStore();
        FileInputStream in=null;
        try {
            in = new FileInputStream(phdBall);
            PhdParser.parsePhd(in, datastore);
            return datastore;
        } catch (FileNotFoundException e) {
            throw new DataStoreException("could not parse phd ball", e);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
        
    }

}
