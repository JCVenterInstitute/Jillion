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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.io.File;

import org.jcvi.common.core.seq.read.trace.frg.AbstractFragmentDataStore;
import org.jcvi.common.core.seq.read.trace.frg.Frg2Parser;
import org.jcvi.common.core.seq.read.trace.frg.IndexedFragmentDataStore;

public class TestIndexedFragmentDataStore extends AbstractTestFragmentDataStore{

    @Override
    protected AbstractFragmentDataStore createFragmentDataStore(File file)
            throws Exception {
        return new IndexedFragmentDataStore(file, new Frg2Parser());
    }

}
