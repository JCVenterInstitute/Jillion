/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.nuc.NucleotideDataStore;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class TraceNucleotideDataStoreAdapter <T extends Trace> extends AbstractTraceDataStoreAdapter<T,NucleotideEncodedGlyphs> implements NucleotideDataStore {

    public TraceNucleotideDataStoreAdapter(DataStore<T> delegate) {
        super(delegate);
    }

    @Override
    protected NucleotideEncodedGlyphs adapt(T delegate) {
        return delegate.getBasecalls();
    }

}
