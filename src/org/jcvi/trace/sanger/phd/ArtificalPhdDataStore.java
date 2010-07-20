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
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.joda.time.DateTime;
/**
 * {@code ArtificialPhdDataStore} is a {@link DataStore} of
 * Nucleotide and PhredQuality data that has been adapted to 
 * match the {@link Phd} interface.
 * @author dkatzel
 *
 *
 */
public class ArtificalPhdDataStore extends AbstractDataStore<Phd> implements PhdDataStore{
    private final DataStore<NucleotideEncodedGlyphs> seqDataStore;
    private final DataStore<EncodedGlyphs<PhredQuality>> qualDataStore;
    private final Properties comments = new Properties();
    
   
    
    /**
     * @param seqDataStore
     * @param qualDataStore
     * @param phdDate
     */
    public ArtificalPhdDataStore(DataStore<NucleotideEncodedGlyphs> seqDataStore,
            DataStore<EncodedGlyphs<PhredQuality>> qualDataStore, DateTime phdDate) {
        this.seqDataStore = seqDataStore;
        this.qualDataStore = qualDataStore;
        comments.putAll(PhdUtil.createPhdTimeStampCommentFor(phdDate));
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return seqDataStore.contains(id);
    }

    @Override
    public Phd get(String id) throws DataStoreException {
        super.get(id);
       final NucleotideEncodedGlyphs basecalls = seqDataStore.get(id);
       if(basecalls ==null){
           throw new NullPointerException("could not find basecalls for "+id);
       }
    final EncodedGlyphs<PhredQuality> qualities = qualDataStore.get(id);
    if(qualities ==null){
        throw new NullPointerException("could not find qualities for "+id);
    }
    return ArtificialPhd.createNewbler454Phd(basecalls, 
                qualities,
                comments,Collections.<PhdTag>emptyList());
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return seqDataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        super.size();
        return seqDataStore.size();
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        seqDataStore.close();
        qualDataStore.close();
        comments.clear();
    }

    
}
