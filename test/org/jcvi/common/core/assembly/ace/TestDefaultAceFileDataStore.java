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
 * Created on May 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.DefaultAceFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;

public class TestDefaultAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

    public TestDefaultAceFileDataStore() throws IOException, DataStoreException {
        super();        
    }

    @Override
    protected List<AceContig> getContigList(File aceFile)
            throws IOException {
        AceContigDataStore dataStore= DefaultAceFileDataStore.create(aceFile);
        try{
            List<AceContig> contigs = new ArrayList<AceContig>((int)dataStore.getNumberOfRecords());
        
            for(Iterator<String> iter = dataStore.idIterator(); iter.hasNext();){
                String id = iter.next();
               contigs.add(dataStore.get(id));
            }
            return contigs;
        } catch (DataStoreException e) {
            throw new RuntimeException("error getting contigs",e);
        }
        }

	@Override
	protected AceContigDataStore createDataStoreFor(File aceFile) throws IOException {
		return DefaultAceFileDataStore.create(aceFile);
	}
    

}
