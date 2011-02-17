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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceContigDataStore;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.io.fileServer.DirectoryFileServer.TemporaryDirectoryFileServer;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCas2Consed3 {

	 private final ResourceFileServer RESOURCES = new ResourceFileServer(TestCas2Consed3.class); 
	 private ContigDataStore<PlacedRead, Contig<PlacedRead>> expectedDataStore;
	   
	 
	 TemporaryDirectoryFileServer tempDir;
	 @Before
	    public void setup() throws IOException{
	        expectedDataStore = new DefaultContigFileDataStore(RESOURCES.getFile("files/expected.contig"));
	        tempDir = TemporaryDirectoryFileServer.createTemporaryDirectoryFileServer();
	   	    
	 }
	    
	    @Test
	    public void parseCas() throws IOException, DataStoreException{
	        File casFile = RESOURCES.getFile("files/flu.cas");
	      Cas2Consed3 cas2consed3 = new Cas2Consed3(casFile, tempDir.getRootDir());
	      cas2consed3.convert();
	      
	      File aceFile = tempDir.getFile("edit_dir/cas2consed3.ace.1");
	      AceContigDataStore dataStore = new DefaultAceFileDataStore(aceFile);
	      assertEquals("# contigs", expectedDataStore.size(), dataStore.size());
	      
	      for(AceContig contig : dataStore){
	    	  Contig<PlacedRead> expectedContig= expectedDataStore.get(contig.getId());
	    	  assertEquals("consensus", expectedContig.getConsensus().decode(),
	    			  contig.getConsensus().decode());
	    	  assertEquals("# reads", expectedContig.getNumberOfReads(), contig.getNumberOfReads());
	    	  for(AcePlacedRead actualRead : contig.getPlacedReads()){
	    		  String readId =actualRead.getId();
	    		  PlacedRead expectedRead = expectedContig.getPlacedReadById(readId);
	    		  assertEquals("read basecalls", expectedRead.getEncodedGlyphs().decode(), actualRead.getEncodedGlyphs().decode());
	    		  assertEquals("read offset", expectedRead.getStart(), actualRead.getStart());
	    	  }
	      }
	    }
}
