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
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreFilter;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

public class DefaultPhdFileDataStore extends AbstractPhdFileDataStore{
    private static final GlyphCodec<PhredQuality> QUALITY_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;
    
    private final Map<String, DefaultPhd> map = new HashMap<String, DefaultPhd>();
    private boolean closed = false;
    
    @Override
    protected void visitPhd(String id, List<NucleotideGlyph> bases,
            List<PhredQuality> qualities, List<ShortGlyph> positions,
            Properties comments, List<PhdTag> tags) {
        map.put(id, new DefaultPhd(
                new DefaultNucleotideEncodedGlyphs(bases),
                new DefaultEncodedGlyphs<PhredQuality>(QUALITY_CODEC, qualities),
                new Peaks(positions),
                comments,
                tags));
        
    }

    /**
     * 
     */
    public DefaultPhdFileDataStore() {
        super();
    }

    /**
     * @param filter
     */
    public DefaultPhdFileDataStore(DataStoreFilter filter) {
        super(filter);
    }
    public DefaultPhdFileDataStore(File phdFile, DataStoreFilter filter) throws FileNotFoundException {
        super(filter);
        PhdParser.parsePhd(phdFile, this);
    }
    
    public DefaultPhdFileDataStore(File phdFile) throws FileNotFoundException {
        super();
        PhdParser.parsePhd(phdFile, this);
    }
    private synchronized void checkNotYetClosed(){
        if(closed){
            throw new IllegalStateException("datastore already closed");
        }
    }
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        checkNotYetClosed();
        return map.containsKey(id);
    }

    @Override
    public synchronized Phd get(String id) throws DataStoreException {
        checkNotYetClosed();
        return map.get(id);
    }

    @Override
    public synchronized Iterator<String> getIds() throws DataStoreException {
        checkNotYetClosed();
        return map.keySet().iterator();
    }

    @Override
    public synchronized int size() throws DataStoreException {
        checkNotYetClosed();
        return map.size();
    }

    @Override
    public synchronized void close() throws IOException {
        map.clear();
        closed =true;
        
    }

}
