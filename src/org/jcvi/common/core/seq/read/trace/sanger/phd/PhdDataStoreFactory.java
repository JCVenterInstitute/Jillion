/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.phd;

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
