/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class TraceQualityDataStoreAdapter<T extends Trace> extends AbstractTraceDataStoreAdapter<T,EncodedGlyphs<PhredQuality>> implements QualityDataStore{

  
    /**
     * @param delegate
     */
    public TraceQualityDataStoreAdapter(DataStore<T> delegate) {
      super(delegate);
    }

    @Override
    protected EncodedGlyphs<PhredQuality> adapt(T delegate) {
        return delegate.getQualities();
    }

    
    
}
