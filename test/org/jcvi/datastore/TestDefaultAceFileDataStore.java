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
package org.jcvi.datastore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceFileDataStore;
import org.jcvi.common.core.assembly.contig.ace.TestAbstractAceParserMatchesAce2ContigMultipleContigs;
import org.jcvi.common.core.datastore.DataStoreException;

import static org.junit.Assert.fail;
public class TestDefaultAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

    public TestDefaultAceFileDataStore() throws IOException {
        super();        
    }

    @Override
    protected List<AceContig> getContigList(File aceFile)
            throws IOException {
        DefaultAceFileDataStore dataStore= new DefaultAceFileDataStore(aceFile);
        List<AceContig> contigs = new ArrayList<AceContig>(dataStore.size());
        for(Iterator<String> iter = dataStore.getIds(); iter.hasNext();){
            String id = iter.next();
            try {
                contigs.add(dataStore.get(id));
            } catch (DataStoreException e) {
                fail("error getting contig " + id);
            }
        }
        return contigs;
    }

}
