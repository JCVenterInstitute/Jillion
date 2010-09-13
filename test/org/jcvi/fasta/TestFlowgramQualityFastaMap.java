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
package org.jcvi.fasta;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSffFileDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.QualitySffDataStore;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SffParser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestFlowgramQualityFastaMap {

    String QUAL_EXPECTED = "files/5readExample.qual";
    String QUAL_ACTUAL = "files/5readExample.sff";
    DataStore<EncodedGlyphs<PhredQuality>> expected;
    DataStore<EncodedGlyphs<PhredQuality>> actual;
    ResourceFileServer RESOURCES = new ResourceFileServer(TestFlowgramQualityFastaMap.class);
    
    @Before
    public void setup() throws IOException, TraceDecoderException{
        expected = QualityFastaRecordDataStoreAdapter.adapt(
                new DefaultQualityFastaFileDataStore(
                		RESOURCES.getFile(QUAL_EXPECTED)));
        DefaultSffFileDataStore datastore = new DefaultSffFileDataStore(new RunLengthEncodedGlyphCodec((byte)70));
        SffParser.parseSFF(
        		RESOURCES.getFileAsStream(QUAL_ACTUAL), datastore);
        
        actual = new QualitySffDataStore(datastore);
        
    }
    
    @Test
    public void valid() throws DataStoreException{
        assertEquals(expected.size(), actual.size());
        
        Iterator<String> iter = expected.getIds();
        while(iter.hasNext()){
            String id = iter.next();
            EncodedGlyphs<PhredQuality> expectedRecord = expected.get(id);
            assertRecordsEqual(expectedRecord, actual.get(id));
        }
    }
    private void assertRecordsEqual(
            EncodedGlyphs<PhredQuality> expectedRecord,
            EncodedGlyphs<PhredQuality> actualRecord) {
        assertEquals(expectedRecord.decode(), actualRecord.decode());
        
    }
    
}
