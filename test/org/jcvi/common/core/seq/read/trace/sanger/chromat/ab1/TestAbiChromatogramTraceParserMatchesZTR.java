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

package org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.seq.read.trace.TraceDecoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.BasicChromatogramFile;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.Ab1FileParser;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.ZTRChromatogramFileParser;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAbiChromatogramTraceParserMatchesZTR {
    private static ResourceFileServer RESOURCES = new ResourceFileServer(TestAbiChromatogramTraceParserMatchesZTR.class);
    BasicChromatogramFile expectedZTR;
    String id = "id";
    @Before
    public void setup() throws FileNotFoundException, TraceDecoderException, IOException{
        expectedZTR = new BasicChromatogramFile(id);
        ZTRChromatogramFileParser.parse(RESOURCES.getFile("files/SDBHD01T00PB1A1672F.ztr"), expectedZTR);
    }
    
    @Test
    public void abiVisitorMatchesZTR() throws FileNotFoundException, TraceDecoderException, IOException{
        BasicChromatogramFile actualAbi = new BasicChromatogramFile(id);
        Ab1FileParser.parse(RESOURCES.getFile("files/SDBHD01T00PB1A1672F.ab1"), actualAbi);

        assertEquals(expectedZTR.getNucleotideSequence(), actualAbi.getNucleotideSequence());
        assertEquals(expectedZTR.getPeaks(), actualAbi.getPeaks());
        assertEquals(expectedZTR.getQualitySequence(), actualAbi.getQualitySequence());
        assertEquals(expectedZTR.getChannelGroup(), actualAbi.getChannelGroup());
        assertEquals(expectedZTR.getNumberOfTracePositions(), actualAbi.getNumberOfTracePositions());
        assertCommentsCorrect(expectedZTR.getComments(), actualAbi.getComments());
    
        
    }

	private void assertCommentsCorrect(Map<String, String> expected,
			Map<String, String> actual) {
		assertEquals("num comments",expected.size(), actual.size());
		for(Entry<String, String> expectedEntry : expected.entrySet()){
			String key = expectedEntry.getKey();
			String value = expectedEntry.getValue();
			assertTrue("missing "+key, actual.containsKey(key));
			assertEquals("wrong value for "+key, value, actual.get(key));
		}
		
	}
}
