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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.seq.read.trace.TraceDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.SangerTrace;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.joda.time.DateTime;

/**
 * @author dkatzel
 *
 *
 */
public class PhdSangerTraceDataStoreAdapter<S extends SangerTrace> implements PhdDataStore{

    private final TraceDataStore<S> delegate;
    private final Properties comments;
    /**
     * @param delegate
     * @param phdDate
     */
    public PhdSangerTraceDataStoreAdapter(TraceDataStore<S> delegate,
            DateTime phdDate) {
        this.delegate = delegate;
        this.comments = PhdUtil.createPhdTimeStampCommentFor(phdDate);
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }
    @Override
    public Phd get(String id) throws DataStoreException {
        try{
            SangerTrace trace = delegate.get(id); 
        return new DefaultPhd(id,
        		trace.getBasecalls(), trace.getQualities(), trace.getPeaks(),
                comments,Collections.<PhdTag>emptyList());
        }catch(Throwable t){            
            throw new RuntimeException(t);
        }
    }
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }
    @Override
    public int size() throws DataStoreException {
        return delegate.size();
    }
    @Override
    public void close() throws IOException {
        delegate.close();
        
    }
    @Override
    public CloseableIterator<Phd> iterator() {
        return new DataStoreIterator<Phd>(this);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }
    
    
}
