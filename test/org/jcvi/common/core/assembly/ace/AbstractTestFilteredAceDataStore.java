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

package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ContigDataStore;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileContigDataStore;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestFilteredAceDataStore{

    ResourceHelper resources = new ResourceHelper(AbstractTestFilteredAceDataStore.class);
    @Test
    public void skipSelectedContig() throws IOException, DataStoreException{
        String contigIdToSkip = "22934-PB1";
        File aceFile = resources.getFile("files/fluSample.ace");
        DataStoreFilter filter = DataStoreFilters.newExcludeFilter(Collections.singleton(contigIdToSkip));
        AceFileContigDataStore datastore = create(aceFile, filter);
        assertEquals(7, datastore.getNumberOfRecords());
        assertEquals(499 , datastore.getNumberOfTotalReads());
        ContigDataStore<AssembledRead, Contig<AssembledRead>> contigDataStore = DefaultContigFileDataStore.create(resources.getFile("files/fluSample.contig"));
        assertEquals(8,contigDataStore.getNumberOfRecords());
        StreamingIterator<AceContig> iter = datastore.iterator();
        try{
	    	 while(iter.hasNext()){
	        	AceContig aceContig = iter.next();
	            Contig<AssembledRead> contig = contigDataStore.get(aceContig.getId());
	            AceContigTestUtil.assertContigsEqual(contig, aceContig);
	    	 }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }
    
    protected abstract AceFileContigDataStore create(File aceFile, DataStoreFilter filter) throws IOException;
    
}
