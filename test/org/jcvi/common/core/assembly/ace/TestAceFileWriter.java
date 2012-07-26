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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileContigDataStore;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileWriter;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.DefaultAceFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaRecordDataStoreAdatper;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAceFileWriter {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestAceFileWriter.class);
    
    @Test
    public void writeAndReParse() throws IOException, DataStoreException{
        File contigFile = RESOURCES.getFile("files/flu_644151.contig");
        File seqFile = RESOURCES.getFile("files/flu_644151.seq");
        File qualFile = RESOURCES.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideDataStore nucleotideDataStore = NucleotideSequenceFastaRecordDataStoreAdatper.adapt(DefaultNucleotideSequenceFastaFileDataStore.create(seqFile)); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = DefaultQualityFastaFileDataStore.create(qualFile);
        QualityDataStore qualityDataStore = QualityFastaRecordDataStoreAdapter.adapt(qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int numberOfContigs = (int)aceDataStore.getNumberOfRecords();
        int numberOfReads = countNumberOfTotalReads(aceDataStore);
        AceFileWriter.writeAceFileHeader(numberOfContigs, numberOfReads, out);
        writeAceContigs(phdDataStore, aceDataStore, out);
        
        AceContigDataStoreBuilder builder = DefaultAceFileDataStore.createBuilder();
        AceFileParser.parse(new ByteArrayInputStream(out.toByteArray()), builder);
        
        AceFileContigDataStore reparsedAceDataStore = builder.build();
        assertEquals("# contigs", aceDataStore.getNumberOfRecords(), reparsedAceDataStore.getNumberOfRecords());
        StreamingIterator<AceContig> contigIter = aceDataStore.iterator();
        try{
	        while(contigIter.hasNext()){
	        	AceContig expectedContig = contigIter.next();
	            AceContig actualContig = reparsedAceDataStore.get(expectedContig.getId());            
	            assertEquals("consensus", expectedContig.getConsensusSequence(), actualContig.getConsensusSequence());
	            assertEquals("# reads", expectedContig.getNumberOfReads(), actualContig.getNumberOfReads());
	            StreamingIterator<AcePlacedRead> readIter =null;
	            try{
	            	readIter = expectedContig.getReadIterator();
	            	while(readIter.hasNext()){
	            		AcePlacedRead expectedRead = readIter.next();
	            		AcePlacedRead actualRead = actualContig.getRead(expectedRead.getId());
	  	                assertEquals("basecalls", expectedRead.getNucleotideSequence(), actualRead.getNucleotideSequence());
	  	                assertEquals("offset", expectedRead.getGappedStartOffset(), actualRead.getGappedStartOffset());
	  	                assertEquals("validRange", expectedRead.getReadInfo().getValidRange(), actualRead.getReadInfo().getValidRange());
	  	                assertEquals("dir", expectedRead.getDirection(), actualRead.getDirection());
	  	            
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
			AceFileContigDataStore aceDataStore, ByteArrayOutputStream out)
			throws IOException, DataStoreException {
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
		try{
			  while(iter.hasNext()){
		        	AceContig contig = iter.next();
		        	AceFileWriter.writeAceContig(contig, phdDataStore, out);
			  }
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}

	private int countNumberOfTotalReads(AceFileContigDataStore aceDataStore) throws DataStoreException {
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
