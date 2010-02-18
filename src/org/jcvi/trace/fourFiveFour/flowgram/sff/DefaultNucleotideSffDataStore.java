/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class DefaultNucleotideSffDataStore implements NucleotideDataStore{

    private final SffDataStore flowgramDataStore;
    private final boolean trim;
    /**
     * @param flowgramDataStore
     */
    public DefaultNucleotideSffDataStore(
            SffDataStore flowgramDataStore, boolean trim) {
        this.flowgramDataStore = flowgramDataStore;
        this.trim = trim;
    }
    public DefaultNucleotideSffDataStore(
            SffDataStore flowgramDataStore) {
        this(flowgramDataStore, false);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return flowgramDataStore.contains(id);
    }

    @Override
    public NucleotideEncodedGlyphs get(String id) throws DataStoreException {
        final Flowgram flowgram = flowgramDataStore.get(id);
        NucleotideEncodedGlyphs fullRange= flowgram.getBasecalls();
        if(trim){
           
            return new DefaultNucleotideEncodedGlyphs(
                    fullRange.decode(SFFUtil.getTrimRangeFor(flowgram)));
        }
        return fullRange;
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
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
    public Iterator<NucleotideEncodedGlyphs> iterator() {
        return new DataStoreIterator<NucleotideEncodedGlyphs>(this);
    }

}
