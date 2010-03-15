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

package org.jcvi.trace.sanger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.jcvi.datastore.AbstractDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.TraceDecoderException;

/**
 * @author dkatzel
 *
 *
 */
public class SingleSangerTraceFileDataStore extends AbstractDataStore<SangerTrace> implements TraceDataStore<SangerTrace> {
    private final String id;
    private final SangerTrace trace;
    public SingleSangerTraceFileDataStore(File traceFile) throws FileNotFoundException, TraceDecoderException{
        this(traceFile,SangerTraceParser.getInstance());
    }
    public SingleSangerTraceFileDataStore(File traceFile,SangerTraceCodec traceParser) throws FileNotFoundException, TraceDecoderException{
        this.id = FilenameUtils.getBaseName(traceFile.getName());
        InputStream in = new FileInputStream(traceFile);
        try{
        this.trace =traceParser.decode(in);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        super.contains(id);
        return this.id.equals(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public SangerTrace get(String id) throws DataStoreException {
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
    public Iterator<String> getIds() throws DataStoreException {
        super.getIds();
        return IteratorUtils.singletonIterator(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        super.size();
        return 1;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        super.close();
        
    }

   
}
