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

package org.jcvi.common.core.assembly.trim;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.SimpleDataStore;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFCommonHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadData;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor;
import org.jcvi.common.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public class SffTrimDataStoreBuilder implements SffFileVisitor, Builder<TrimDataStore>{

    private final Map<String, Range> trimRanges = new HashMap<String, Range>();
   
   

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

    /**
    * {@inheritDoc}
    */
    @Override
    public TrimDataStore build() {
        return TrimDataStoreAdatper.adapt(new SimpleDataStore<Range>(trimRanges));
    }

}
