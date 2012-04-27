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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.ace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ctg.DefaultContigFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Test;

public abstract class  AbstractTestAceParserMatchesAce2Contig {
    DefaultContigFileDataStore expectedContigDataStore;
    ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestAceParserMatchesAce2Contig.class);
    private final String pathToAceFile;
    
    private final AceContigDataStore sut;
    
    private static final List<String> IDS = Arrays.asList(
			"22934-PB2",
			"22934-PB1",
			"22934-PA",
			"22934-HA",
			"22934-NP",
			"22934-NA",
			"22934-MP",
			"22934-NS"
			);
    
    AbstractTestAceParserMatchesAce2Contig(String aceFile, String contigFile) throws IOException, DataStoreException{
        this.expectedContigDataStore = new DefaultContigFileDataStore(
        		RESOURCES.getFileAsStream(contigFile));
        pathToAceFile = aceFile;
        sut = createDataStoreFor(RESOURCES.getFile(aceFile));
    }
   
   @After
   public void closeDataStore() throws IOException{
	   sut.close();
   }
    protected File getAceFile() throws IOException{
    	return RESOURCES.getFile(pathToAceFile);
    }

   

    protected abstract AceContigDataStore createDataStoreFor(File aceFile) throws IOException;
    
    @Test
    public void numberOfContigs() throws DataStoreException{
    	assertEquals(expectedContigDataStore.getNumberOfRecords(), sut.getNumberOfRecords());    	
    }
    
    @Test
    public void idIterator() throws DataStoreException{
    	CloseableIterator<String> ids = null;
    	try{
    		ids =sut.idIterator();  	
    		while(ids.hasNext()){
    			assertTrue(expectedContigDataStore.contains(ids.next()));
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(ids);
    	}
    }
    
    @Test
    public void iterator() throws DataStoreException{
    	CloseableIterator<AceContig> iter = null;
    	try{
    		iter =sut.iterator();  	
    		while(iter.hasNext()){
    			AceContig actualContig = iter.next();
    			assertTrue(expectedContigDataStore.contains(actualContig.getId()));
			  Contig<PlacedRead> expectedContig = expectedContigDataStore.get(actualContig.getId());
    	      AceContigTestUtil.assertContigsEqual(expectedContig, actualContig);
    	       
    		}
    	}finally{
    		IOUtil.closeAndIgnoreErrors(iter);
    	}
    }
    
    @Test
    public void get() throws DataStoreException{
    	
    	
    	for(String id : IDS){
    		Contig<PlacedRead> expected = expectedContigDataStore.get(id);
    		AceContig actual = sut.get(id);
    		AceContigTestUtil.assertContigsEqual(expected, actual);
    	}
    }
  
	@Test
	public void sizeVsIterator() throws IOException, DataStoreException{
		
		long size = sut.getNumberOfRecords();
		long expected = 0;
		CloseableIterator<AceContig> iter = sut.iterator();
		try{
			while(iter.hasNext()){
				expected++;
				iter.next();
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		assertEquals(expected, size);
	}
	
	@Test
	public void contains() throws DataStoreException, IOException{
		for(String id : IDS){
			assertTrue(sut.contains(id));
		}
	}
	
	@Test
	public void containsIdNotInDataStoreShouldReturnFalse() throws DataStoreException, FileNotFoundException, IOException{
		assertFalse(sut.contains("not in datastore"));
	}
	
	@Test
	public void getVsIterator() throws DataStoreException, IOException{
		CloseableIterator<AceContig> iter=null;
		try{
			iter = sut.iterator();
			while(iter.hasNext()){
				AceContig fromIter = iter.next();
				AceContig fromGet = sut.get(fromIter.getId());
				AceContigTestUtil.assertContigsEqual(fromIter, fromGet);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
