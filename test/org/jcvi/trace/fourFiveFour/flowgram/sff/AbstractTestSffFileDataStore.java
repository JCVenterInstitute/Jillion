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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.File;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public abstract class AbstractTestSffFileDataStore extends TestReadExampleSffFile{  

    private SffDataStore dataStore;
    @Override
    protected void parseSff(File file) throws Exception{
        dataStore = parseDataStore(file);
    }
    
    protected abstract SffDataStore parseDataStore(File f) throws Exception;
    
    @Override
    protected Flowgram getFlowgram(String id) throws TraceDecoderException, DataStoreException {
        return dataStore.get(id);
    }
    @Override
    protected int getNumberOfFlowgrams() throws TraceDecoderException, DataStoreException {
        return dataStore.size();
    }

}
