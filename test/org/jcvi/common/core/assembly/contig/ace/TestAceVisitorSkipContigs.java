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

package org.jcvi.common.core.assembly.contig.ace;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DefaultExcludeDataStoreFilter;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceVisitorSkipContigs{

    ResourceFileServer resources = new ResourceFileServer(TestAceVisitorSkipContigs.class);
    @Test
    public void skipSelectedContig() throws IOException, DataStoreException{
        String contigIdToSkip = "22934-PB1";
        File aceFile = resources.getFile("files/fluSample.ace");
        DataStoreFilter filter = new DefaultExcludeDataStoreFilter(Collections.singleton(contigIdToSkip));
        AceContigDataStore datastore = DefaultAceFileDataStore.create(aceFile, filter);
        assertEquals(7, datastore.size());
        DefaultContigFileDataStore contigDataStore = new DefaultContigFileDataStore(resources.getFile("files/fluSample.contig"));
        assertEquals(8,contigDataStore.size());
        
        for(AceContig aceContig : datastore){
            Contig<PlacedRead> contig = contigDataStore.get(aceContig.getId());
            AceContigTestUtil.assertContigsEqual(contig, aceContig);
        }
        
    }
    
}
