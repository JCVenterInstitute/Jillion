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

package org.jcvi.common.core.seq.trim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.util.iter.CloseableIterator;

/**
 * {@code DefaultTrimFileDataStore} is a implementation
 * that takes parses a single trim file and wraps it 
 * inside a {@link TrimPointsDataStore}.
 * 
 * @author dkatzel
 *
 *
 */
public class DefaultTrimFileDataStore implements TrimPointsDataStore, TrimFileVisitor{
    /**
     * The actual DataStore we delegate to, will
     * be populated from map.
     */
    private DataStore<Range> delegate;
    /**
     * the Map that temporarily contains the trim data while
     * we are parsing the trim file.  This map is cleared
     * and its records given to the delegate when 
     * the parsing is complete.
     */
    private final Map<String, Range> map = new HashMap<String, Range>();
    /**
     * Parse the given trim file and populate this datastore.
     * @param trimFile the trim file to wrap.
     * @throws FileNotFoundException if the given file does not exist.
     */
    public DefaultTrimFileDataStore(File trimFile) throws FileNotFoundException{
        TrimFileUtil.parseTrimFile(trimFile, this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized boolean visitTrim(String id, Range trimRange) {
        map.put(id, trimRange);
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized void visitEndOfFile() {
        delegate = MapDataStoreAdapter.adapt(map);
        map.clear();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized void visitFile() {
        if(delegate !=null){
            throw new IllegalStateException("database already populated!");
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range get(String id) throws DataStoreException {
        return delegate.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        delegate.close();
        
    }

    /**
    * {@inheritDoc}
     * @throws DataStoreException 
    */
    @Override
    public CloseableIterator<Range> iterator() throws DataStoreException {
        return delegate.iterator();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
        //no-op
        
    }

}
