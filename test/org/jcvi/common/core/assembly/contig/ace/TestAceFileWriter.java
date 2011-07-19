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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.assembly.contig.ace.AceAssembly;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.contig.ace.AceFileParser;
import org.jcvi.common.core.assembly.contig.ace.AceFileWriter;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceAdapterContigFileDataStore;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceAssembly;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaRecordDataStoreAdatper;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.joda.time.DateTime;
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
        NucleotideDataStore nucleotideDataStore = NucleotideFastaRecordDataStoreAdatper.adapt(new DefaultNucleotideFastaFileDataStore(seqFile)); 
        final DefaultQualityFastaFileDataStore qualityFastaDataStore = new DefaultQualityFastaFileDataStore(qualFile);
        QualityDataStore qualityDataStore = QualityFastaRecordDataStoreAdapter.adapt(qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, new DateTime(phdDate));
       
        AceContigDataStore aceDataStore = new DefaultAceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        AceAssembly aceAssembly = new DefaultAceAssembly<AceContig>(aceDataStore, phdDataStore);
        AceFileWriter.writeAceFile(aceAssembly, out);
        
        DefaultAceFileDataStore reparsedAceDataStore = new DefaultAceFileDataStore();
        AceFileParser.parseAceFile(new ByteArrayInputStream(out.toByteArray()), reparsedAceDataStore);
        
        assertEquals("# contigs", aceDataStore.size(), reparsedAceDataStore.size());
        for(AceContig expectedContig : aceDataStore){
            AceContig actualContig = reparsedAceDataStore.get(expectedContig.getId());            
            assertEquals("consensus", expectedContig.getConsensus(), actualContig.getConsensus());
            assertEquals("# reads", expectedContig.getNumberOfReads(), actualContig.getNumberOfReads());
            for(AcePlacedRead expectedRead : expectedContig.getPlacedReads()){
                AcePlacedRead actualRead = actualContig.getPlacedReadById(expectedRead.getId());
                assertEquals("basecalls", expectedRead.getEncodedGlyphs(), actualRead.getEncodedGlyphs());
                assertEquals("offset", expectedRead.getStart(), actualRead.getStart());
                assertEquals("validRange", expectedRead.getValidRange(), actualRead.getValidRange());
                assertEquals("dir", expectedRead.getSequenceDirection(), actualRead.getSequenceDirection());
            }
        }
    }
}
