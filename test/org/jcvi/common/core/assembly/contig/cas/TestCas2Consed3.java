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

package org.jcvi.common.core.assembly.contig.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.assembly.cas.Cas2Consed3;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.cas.UnTrimmedExtensionTrimMap;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DefaultAceFileDataStore;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.DirectoryFileServer.TemporaryDirectoryFileServer;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestCas2Consed3 {

	 private final ResourceFileServer RESOURCES = new ResourceFileServer(TestCas2Consed3.class); 
	 private ContigDataStore<PlacedRead, Contig<PlacedRead>> expectedDataStore;
	   private String prefix = "cas2consed3";
	 
	 TemporaryDirectoryFileServer tempDir;
	 @Before
	    public void setup() throws IOException{
	        expectedDataStore = new DefaultContigFileDataStore(RESOURCES.getFile("files/expected.contig"));
	        tempDir = DirectoryFileServer.createTemporaryDirectoryFileServer();
	   	    
	 }
	    
	    @Test
	    public void parseCas() throws IOException, DataStoreException{
	        File casFile = RESOURCES.getFile("files/flu.cas");
	      Cas2Consed3 cas2consed3 = new Cas2Consed3(casFile, tempDir,prefix,true,false);
	      cas2consed3.convert(TrimDataStoreUtil.EMPTY_DATASTORE,new UnTrimmedExtensionTrimMap(),FastQQualityCodec.ILLUMINA);
	      
	      File aceFile = tempDir.getFile("edit_dir/"+prefix+".ace.1");
	      AceContigDataStore dataStore = new DefaultAceFileDataStore(aceFile);
	      assertEquals("# contigs", expectedDataStore.size(), dataStore.size());
	      
	      for(AceContig contig : dataStore){
	    	  Contig<PlacedRead> expectedContig= getExpectedContig(contig.getId());
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
	    /**
	     * cas2Consed now appends coordinates to the end of the contig
	     * if they don't get full reference length, stip that out 
	     * to get the corresponding expected flap assembly which
	     * doesn't do that.
	     */
	    private Contig<PlacedRead> getExpectedContig(String actualContigId) throws DataStoreException{
	        String IdWithoutCoordinates = actualContigId.replaceAll("_.+", "");
	        return expectedDataStore.get(IdWithoutCoordinates);
	    }
}
