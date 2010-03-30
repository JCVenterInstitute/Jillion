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

    public static <T extends Trace, E extends T> TraceQualityDataStoreAdapter<T> adapt(DataStore<E> delegate){
        return (TraceQualityDataStoreAdapter<T>) new TraceQualityDataStoreAdapter<E>(delegate);
    }
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
