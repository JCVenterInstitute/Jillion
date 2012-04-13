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
/**
 * QualityDataStoreAdapter.java
 *
 * Created: Jan 26, 2010 (9:15:24 AM) by jsitz@jcvi.org
 * Copyright 2010 - J. Craig Venter Institute
 */
package org.jcvi.common.core.symbol.residue.nt;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.iter.CloseableIterator;


/**
 * A <code>NucleotideDataStoreAdapter</code> adapts the heavily parameterized
 * <code>{@link DataStore}&lt;{@link NucleotideSequence}&gt;</code>
 * interface to its simplified equivalent {@link NucleotideDataStore}.
 *
 * @author jsitz@jcvi.org
 */
public class NucleotideDataStoreAdapter implements NucleotideDataStore
{
    /** The datastore being wrapped and adapted. */
    private final DataStore<NucleotideSequence> datastore;

    /**
     * Constructs a new <code>NucleotideDataStoreAdapter</code>.
     *
     * @param datastore The {@link DataStore} being wrapped and adapted.
     */
    public NucleotideDataStoreAdapter(DataStore<NucleotideSequence> datastore)
    {
        super();

        this.datastore = datastore;
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#contains(java.lang.String)
     */
    @Override
    public boolean contains(String id) throws DataStoreException
    {
        return this.datastore.contains(id);
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#get(java.lang.String)
     */
    @Override
    public NucleotideSequence get(String id) throws DataStoreException
    {
        return this.datastore.get(id);
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#getIds()
     */
    @Override
    public CloseableIterator<String> idIterator() throws DataStoreException
    {
        return datastore.idIterator();
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#size()
     */
    @Override
    public long getNumberOfRecords() throws DataStoreException
    {
        return this.datastore.getNumberOfRecords();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException
    {
        this.datastore.close();
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public CloseableIterator<NucleotideSequence> iterator() throws DataStoreException
    {
        return this.datastore.iterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return datastore.isClosed();
    }
}
