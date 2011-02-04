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
package org.jcvi.glyph.phredQuality.datastore;

import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.util.CloseableIterator;

/**
 * A <code>QualityDataStoreAdapter</code> adapts the heavily parameterized
 * <code>{@link DataStore}&lt;{@link EncodedGlyphs}&lt;{@link PhredQuality}&gt;&gt;</code>
 * interface to its simplified equivalent {@link QualityDataStore}.
 *
 * @author jsitz@jcvi.org
 */
public class QualityDataStoreAdapter implements QualityDataStore
{
    /** The datastore being wrapped and adapted. */
    private final DataStore<QualityEncodedGlyphs> datastore;

    /**
     * Constructs a new <code>QualityDataStoreAdapter</code>.
     *
     * @param datastore The {@link DataStore} being wrapped and adapted.
     */
    public QualityDataStoreAdapter(DataStore<QualityEncodedGlyphs> datastore)
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
    public QualityEncodedGlyphs get(String id) throws DataStoreException
    {
        return this.datastore.get(id);
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#getIds()
     */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException
    {
        return datastore.getIds();
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#size()
     */
    @Override
    public int size() throws DataStoreException
    {
        return this.datastore.size();
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
    public CloseableIterator<QualityEncodedGlyphs> iterator()
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
