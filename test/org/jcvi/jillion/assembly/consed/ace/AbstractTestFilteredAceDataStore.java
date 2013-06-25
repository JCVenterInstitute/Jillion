/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
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
        AceFileDataStore datastore = create(aceFile, filter);
        assertEquals(7, datastore.getNumberOfRecords());
        assertEquals(499 , datastore.getNumberOfTotalReads());
        TigrContigDataStore contigDataStore = new TigrContigFileDataStoreBuilder(resources.getFile("files/fluSample.contig"),
        									AceContigTestUtil.createFullLengthSeqDataStoreFrom(aceFile))
																				.build();
        assertEquals(8,contigDataStore.getNumberOfRecords());
        StreamingIterator<AceContig> iter = datastore.iterator();
        try{
	    	 while(iter.hasNext()){
	        	AceContig aceContig = iter.next();
	            TigrContig contig = contigDataStore.get(aceContig.getId());
	            AceContigTestUtil.assertContigsEqual(contig, aceContig);
	    	 }
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
        
    }
    
    protected abstract AceFileDataStore create(File aceFile, DataStoreFilter filter) throws IOException;
    
}
