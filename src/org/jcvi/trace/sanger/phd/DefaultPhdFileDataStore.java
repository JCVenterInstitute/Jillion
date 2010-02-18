/*
 * Created on Nov 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jcvi.datastore.DataStoreException;
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
