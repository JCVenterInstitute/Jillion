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

package org.jcvi.common.core.seq.read.trace.sanger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.common.core.datastore.AbstractDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.FileUtil;
import org.jcvi.common.core.seq.read.trace.TraceDataStore;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

/**
 * @author dkatzel
 *
 *
 */
public class SingleSangerTraceFileDataStore extends AbstractDataStore<SangerTrace> implements TraceDataStore<SangerTrace> {
    private final String id;
    private final SangerTrace trace;

    public SingleSangerTraceFileDataStore(File traceFile) throws IOException{
        this.id = FileUtil.getBaseName(traceFile);
        this.trace =SangerTraceParser.INSTANCE.decode(traceFile);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return this.id.equals(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized SangerTrace get(String id) throws DataStoreException {
        super.get(id);
        if(contains(id)){
            return trace;
        }
        return null;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized  CloseableIterator<String> getIds() throws DataStoreException {
        super.getIds();
        return CloseableIteratorAdapter.adapt(Collections.singleton(id).iterator());
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized  int size() throws DataStoreException {
        super.size();
        return 1;
    }
	@Override
	protected void handleClose() throws IOException {
		//no-op
		
	}


   
}
