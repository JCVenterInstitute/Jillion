/**
 * QualityDataStoreAdapter.java
 *
 * Created: Jan 26, 2010 (9:15:24 AM) by jsitz@jcvi.org
 * Copyright 2010 - J. Craig Venter Institute
 */
package org.jcvi.glyph.qualClass;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

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
    private final DataStore<EncodedGlyphs<PhredQuality>> datastore;

    /**
     * Constructs a new <code>QualityDataStoreAdapter</code>.
     *
     * @param datastore The {@link DataStore} being wrapped and adapted.
     */
    public QualityDataStoreAdapter(DataStore<EncodedGlyphs<PhredQuality>> datastore)
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
    public EncodedGlyphs<PhredQuality> get(String id) throws DataStoreException
    {
        return this.datastore.get(id);
    }

    /* (non-Javadoc)
     * @see org.jcvi.datastore.DataStore#getIds()
     */
    @Override
    public Iterator<String> getIds() throws DataStoreException
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
    public Iterator<EncodedGlyphs<PhredQuality>> iterator()
    {
        return this.datastore.iterator();
    }
}
