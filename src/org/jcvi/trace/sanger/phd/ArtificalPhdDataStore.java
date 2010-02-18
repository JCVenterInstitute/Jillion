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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ArtificalPhdDataStore extends AbstractDataStore<Phd>{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern(
    "EEE MMM dd kk:mm:ss yyyy");
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
        comments.put("TIME", DATE_FORMAT.print(phdDate));
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
    return new ArtificialPhd(basecalls, 
                qualDataStore.get(id),
                comments,Collections.<PhdTag>emptyList(),
                12);
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

}
