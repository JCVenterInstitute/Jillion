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
 * Created on Nov 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.IOException;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.EncodedQualitySequence;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.glyph.phredQuality.QualitySequence;
import org.jcvi.glyph.phredQuality.QualityGlyphCodec;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;
import org.jcvi.util.CloseableIterator;

public class QualitySffDataStore implements QualityDataStore{

    private static final QualityGlyphCodec QUALITY_CODEC = 
        RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    private final DataStore<? extends Flowgram> flowgramDataStore;
    private final boolean trim;
    /**
     * @param flowgramDataStore
     */
    public QualitySffDataStore(
            DataStore<? extends Flowgram> flowgramDataStore, boolean trim) {
        this.flowgramDataStore = flowgramDataStore;
        this.trim = trim;
    }
    public QualitySffDataStore(
            DataStore<? extends Flowgram> flowgramDataStore){
        this(flowgramDataStore, false);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return flowgramDataStore.contains(id);
    }

    @Override
    public QualitySequence get(String id) throws DataStoreException {
        final Flowgram flowgram = flowgramDataStore.get(id);
        QualitySequence qualities= flowgram.getQualities();
        if(trim){
         return new EncodedQualitySequence(QUALITY_CODEC, 
                 qualities.decode(SFFUtil.getTrimRangeFor(flowgram)));   
        }
        return qualities;
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return flowgramDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return flowgramDataStore.size();
    }

    @Override
    public void close() throws IOException {
        flowgramDataStore.close();
        
    }

    @Override
    public CloseableIterator<QualitySequence> iterator() {
        return new DataStoreIterator<QualitySequence>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return flowgramDataStore.isClosed();
    }


}
