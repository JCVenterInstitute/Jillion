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
package org.jcvi.jillion.assembly.clc.cas.consed;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.clc.cas.CasFileParser;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStore;
import org.jcvi.jillion.assembly.clc.cas.CasGappedReferenceDataStoreBuilderVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileContigDataStore;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigFileDataStoreBuilder;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestCas2Consed {
	private final ResourceHelper RESOURCES = new ResourceHelper(TestCas2Consed.class); 
	 private TigrContigDataStore expectedDataStore;

	 
	   @Rule
	    public TemporaryFolder folder = new TemporaryFolder();
	    
	 
	@Before
    public void setup() throws IOException{
	 NucleotideFastaDataStore fastas = new NucleotideFastaFileDataStoreBuilder(RESOURCES.getFile("../files/15050.fasta"))
		.build();
        expectedDataStore = new TigrContigFileDataStoreBuilder(RESOURCES.getFile("../files/expected.contig"), fastas)
        						.build();
   	    
	 }
	    
	    @Test
	    public void parseCas() throws IOException, DataStoreException{
	        File casFile = RESOURCES.getFile("../files/flu.cas");
	        CasGappedReferenceDataStoreBuilderVisitor gappedRefVisitor = new CasGappedReferenceDataStoreBuilderVisitor(casFile.getParentFile());
	        CasFileParser casFileParser = new CasFileParser(casFile);
			casFileParser.accept(gappedRefVisitor);
	        CasGappedReferenceDataStore gappedReferenceDataStore = gappedRefVisitor.build();
	        
	        File consedDir = folder.newFolder("consed");
	        String prefix = "cas2consed";
	      Cas2Consed cas2consed = new Cas2Consed(casFile,gappedReferenceDataStore, consedDir, prefix);
	      casFileParser.accept(cas2consed);

	      File editDir = new File(consedDir, "edit_dir");
	      File aceFile = new File(editDir, prefix+".ace.1");
	      AceFileContigDataStore dataStore =new AceFileDataStoreBuilder(aceFile)
												.hint(DataStoreProviderHint.OPTIMIZE_FAST_RANDOM_ACCESS)
												.build();
	        assertEquals("# contigs", expectedDataStore.getNumberOfRecords(), dataStore.getNumberOfRecords());
	        StreamingIterator<AceContig> iter = dataStore.iterator();
	        try{
		        while(iter.hasNext()){
		        	AceContig contig = iter.next();
		    	  TigrContig expectedContig= getExpectedContig(contig.getId());
		    	  assertEquals("consensus", expectedContig.getConsensusSequence(),
		    			  contig.getConsensusSequence());
		    	  assertEquals("# reads", expectedContig.getNumberOfReads(), contig.getNumberOfReads());
		    	  
		    	  assertReadsCorrectlyPlaced(contig, expectedContig);
		      }
	        }finally{
	        	IOUtil.closeAndIgnoreErrors(iter);
	        }
	    }

		private void assertReadsCorrectlyPlaced(AceContig contig,
				TigrContig expectedContig) {
			StreamingIterator<AceAssembledRead> iter = null;
			try{
				iter = contig.getReadIterator();
				while(iter.hasNext()){
					AceAssembledRead actualRead = iter.next();
				String readId = actualRead.getId();
				AssembledRead expectedRead = expectedContig.getRead(readId);
				assertEquals("read basecalls", expectedRead
						.getNucleotideSequence(), actualRead
						.getNucleotideSequence());
				assertEquals("read offset", expectedRead.getGappedStartOffset(),
						actualRead.getGappedStartOffset());

				}
			}finally{
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
	    /**
	     * cas2Consed now appends coordinates to the end of the contig
	     * if they don't get full reference length, stip that out 
	     * to get the corresponding expected flap assembly which
	     * doesn't do that.
	     */
	    private TigrContig getExpectedContig(String actualContigId) throws DataStoreException{
	        String IdWithoutCoordinates = actualContigId.replaceAll("_.+", "");
	        return expectedDataStore.get(IdWithoutCoordinates);
	    }
}
