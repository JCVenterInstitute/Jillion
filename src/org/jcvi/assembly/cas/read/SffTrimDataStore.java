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

package org.jcvi.assembly.cas.read;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadData;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffFileVisitor;
import org.jcvi.util.CloseableIterator;
import org.jcvi.util.CloseableIteratorAdapter;

/**
 * @author dkatzel
 *
 *
 */
public class SffTrimDataStore implements TrimDataStore, SffFileVisitor{

    private final Map<String, Range> trimRanges = new HashMap<String, Range>();
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return trimRanges.containsKey(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range get(String id) throws DataStoreException {
        return trimRanges.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return CloseableIteratorAdapter.adapt(trimRanges.keySet().iterator());
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int size() throws DataStoreException {
        return trimRanges.size();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public CloseableIterator<Range> iterator() {
        return CloseableIteratorAdapter.adapt(trimRanges.values().iterator());
    }

   

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitCommonHeader(SFFCommonHeader commonHeader) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitReadData(SFFReadData readData) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean visitReadHeader(SFFReadHeader readHeader) {
        trimRanges.put(readHeader.getName(), SFFUtil.getTrimRangeFor(readHeader));
        return false;
    }

}
