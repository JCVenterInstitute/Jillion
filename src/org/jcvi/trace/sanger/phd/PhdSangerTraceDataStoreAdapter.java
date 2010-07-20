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

package org.jcvi.trace.sanger.phd;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.sanger.FileSangerTrace;
import org.joda.time.DateTime;

/**
 * @author dkatzel
 *
 *
 */
public class PhdSangerTraceDataStoreAdapter<S extends FileSangerTrace> implements PhdDataStore{

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
        FileSangerTrace trace = delegate.get(id); 
        if(trace ==null && id.startsWith("JGOA")){
            System.out.println("here");
        } 
        return new DefaultPhd(trace.getBasecalls(), trace.getQualities(), trace.getPeaks(),
                comments,Collections.<PhdTag>emptyList());
        }catch(Throwable t){
            if(id.startsWith("JGOA")){
                t.printStackTrace();
                System.out.println("here");
                
            }
            throw new RuntimeException(t);
        }
    }
    @Override
    public Iterator<String> getIds() throws DataStoreException {
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
    public Iterator<Phd> iterator() {
        return new DataStoreIterator<Phd>(this);
    }
    
    
}
