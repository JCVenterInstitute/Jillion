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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.jillion.assembly.consed.phd.ArtificalPhdDataStore;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceFileUtil_writingAceContigs {

    private static final ResourceHelper RESOURCES = new ResourceHelper(TestAceFileUtil_writingAceContigs.class);
    
    @Test
    public void writeAndReParse() throws IOException, DataStoreException{
        File contigFile = RESOURCES.getFile("files/flu_644151.contig");
        File seqFile = RESOURCES.getFile("files/flu_644151.seq");
        File qualFile = RESOURCES.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideSequenceDataStore nucleotideDataStore = FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, new NucleotideFastaFileDataStoreBuilder(seqFile).build()); 
        final QualityFastaDataStore qualityFastaDataStore = new QualityFastaFileDataStoreBuilder(qualFile).build();
        QualitySequenceDataStore qualityDataStore = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileDataStore aceDataStore = AceAdapterContigFileDataStore.create(qualityFastaDataStore,phdDate,contigFile);
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int numberOfContigs = (int)aceDataStore.getNumberOfRecords();
        int numberOfReads = countNumberOfTotalReads(aceDataStore);
        AceFileUtil.writeAceFileHeader(numberOfContigs, numberOfReads, out);
        writeAceContigs(phdDataStore, aceDataStore, out);
        
        AceFileDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(new ByteArrayInputStream(out.toByteArray()));
        assertEquals("# contigs", aceDataStore.getNumberOfRecords(), reparsedAceDataStore.getNumberOfRecords());
        StreamingIterator<AceContig> contigIter = aceDataStore.iterator();
        try{
	        while(contigIter.hasNext()){
	        	AceContig expectedContig = contigIter.next();
	            AceContig actualContig = reparsedAceDataStore.get(expectedContig.getId());            
	            assertEquals("consensus", expectedContig.getConsensusSequence(), actualContig.getConsensusSequence());
	            assertEquals("# reads", expectedContig.getNumberOfReads(), actualContig.getNumberOfReads());
	            StreamingIterator<AceAssembledRead> readIter =null;
	            try{
	            	readIter = expectedContig.getReadIterator();
	            	while(readIter.hasNext()){
	            		AceAssembledRead expectedRead = readIter.next();
	            		String id = expectedRead.getId().toString();
	            		AceAssembledRead actualRead = actualContig.getRead(expectedRead.getId());
	  	                assertEquals(id + " basecalls", expectedRead.getNucleotideSequence(), actualRead.getNucleotideSequence());
	  	                assertEquals(id + " offset", expectedRead.getGappedStartOffset(), actualRead.getGappedStartOffset());
	  	                assertEquals(id + " validRange", expectedRead.getReadInfo().getValidRange(), actualRead.getReadInfo().getValidRange());
	  	                assertEquals(id + " dir", expectedRead.getDirection(), actualRead.getDirection());
	  	            
	            	}
	            }finally{
	            	IOUtil.closeAndIgnoreErrors(readIter);
	            }
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(contigIter);
        }
    }

	private void writeAceContigs(PhdDataStore phdDataStore,
			AceFileDataStore aceDataStore, ByteArrayOutputStream out)
			throws IOException, DataStoreException {
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
		try{
			  while(iter.hasNext()){
		        	AceContig contig = iter.next();
		        	AceFileUtil.writeAceContig(contig, phdDataStore, out);
			  }
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	private int countNumberOfTotalReads(AceFileDataStore aceDataStore) throws DataStoreException {
		int numberOfReads =0;
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
		try{
	        while(iter.hasNext()){
	        	AceContig contig = iter.next();
	            numberOfReads +=contig.getNumberOfReads();
	        }
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		return numberOfReads;
	}
}
