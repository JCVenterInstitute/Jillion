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
import java.util.Iterator;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.cas.CasAssembly;
import org.jcvi.assembly.cas.CasContig;
import org.jcvi.assembly.cas.DefaultCasAssembly;
import org.jcvi.assembly.cas.EmptyCasTrimMap;
import org.jcvi.assembly.cas.read.FastaCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2FastQCasDataStoreFactory;
import org.jcvi.assembly.cas.read.H2SffCasDataStoreFactory;
import org.jcvi.assembly.cas.read.MultiCasDataStoreFactory;
import org.jcvi.assembly.util.TrimDataStoreUtil;
import org.jcvi.datastore.ContigDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DefaultContigFileDataStore;
import org.jcvi.fasta.fastq.illumina.IlluminaFastQQualityCodec;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*
;/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultCasAssembly {

    private final ResourceFileServer RESOURCES = new ResourceFileServer(TestDefaultCasAssembly.class); 
    
    private ContigDataStore<PlacedRead, Contig<PlacedRead>> expectedDataStore;
    
    @Before
    public void setup() throws IOException{
        expectedDataStore = new DefaultContigFileDataStore(RESOURCES.getFile("files/expected.contig"));
    }
    
    @Test
    public void parseCas() throws IOException, DataStoreException{
        File casFile = RESOURCES.getFile("files/flu.cas");
        final File rootDir = casFile.getParentFile();
        final IlluminaFastQQualityCodec illuminaQualityCodec = new IlluminaFastQQualityCodec(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE);
        MultiCasDataStoreFactory casDataStoreFactory = new MultiCasDataStoreFactory(
                new H2SffCasDataStoreFactory(rootDir),               
                new H2FastQCasDataStoreFactory(rootDir,illuminaQualityCodec),
                new FastaCasDataStoreFactory(rootDir,EmptyCasTrimMap.getInstance(),100)        
        );
        
        
       
        CasAssembly casAssembly = new DefaultCasAssembly.Builder(casFile, casDataStoreFactory, 
                TrimDataStoreUtil.EMPTY_DATASTORE, EmptyCasTrimMap.getInstance(),rootDir)
                            .build();
        DataStore<CasContig> actualDatastore = casAssembly.getContigDataStore();
      
        //mixed flu sample so has 9 contigs instead of 8
        assertEquals(9, actualDatastore.size());
        Iterator<String> ids = actualDatastore.getIds();
        while(ids.hasNext()){
            String id = ids.next();
            assertContigsEqual(expectedDataStore.get(id), actualDatastore.get(id));
        }
    }

    /**
     * @param contig
     * @param casContig
     */
    private void assertContigsEqual(Contig<PlacedRead> expected,
            CasContig actual) {
        assertEquals("id", expected.getId(), actual.getId());
        assertEquals("consensus", NucleotideGlyph.convertToString(
                expected.getConsensus().decode()), 
                NucleotideGlyph.convertToString(actual.getConsensus().decode()));
        assertEquals("# reads", expected.getNumberOfReads(), actual.getNumberOfReads());
        for(PlacedRead expectedRead : expected.getPlacedReads()){
            String id = expectedRead.getId();
            assertReadsEqual(expectedRead, actual.getPlacedReadById(id).getRealPlacedRead());
        }
        
    }

    /**
     * @param expectedRead
     * @param realPlacedRead
     */
    private void assertReadsEqual(PlacedRead expected,
            PlacedRead actual) {
        assertEquals("id", expected.getId(), actual.getId());
        assertEquals("offset", expected.getStart(), actual.getStart());
        assertEquals("dir", expected.getSequenceDirection(), actual.getSequenceDirection());
        assertEquals("validRange", expected.getValidRange(), actual.getValidRange());
        assertEquals("basecalls", expected.getEncodedGlyphs().decode(), actual.getEncodedGlyphs().decode());
    }
}
