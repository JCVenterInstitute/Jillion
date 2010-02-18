/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;

import org.jcvi.datastore.DataStoreException;
/**
 * {@code PhdDataStoreFactory} is a Factory that can create
 * implementations of {@link PhdDataStore}s.
 * @author dkatzel
 *
 *
 */
public interface PhdDataStoreFactory {
    /**
     * Create a {@link PhdDataStore} for the given phd file.
     * @param phdBall the phd file to turn into a DataStore.
     * @return a PhdDataStore implementation of the given file, will never be null.
     * @throws DataStoreException if there is a problem creating the datastore.
     */
    PhdDataStore createPhdDataStoreFactoryFor(File phdBall) throws DataStoreException;
}
