/*
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
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
    @Before
    public void setup() throws IOException, TraceDecoderException{
        expected = QualityFastaRecordDataStoreAdapter.adapt(
                new DefaultQualityFastaFileDataStore(
                new File(TestFlowgramQualityFastaMap.class.getResource(QUAL_EXPECTED).getFile())));
        DefaultSffFileDataStore datastore = new DefaultSffFileDataStore(new RunLengthEncodedGlyphCodec((byte)70));
        SffParser.parseSFF(TestFlowgramQualityFastaMap.class.getResourceAsStream(QUAL_ACTUAL), datastore);
        
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
