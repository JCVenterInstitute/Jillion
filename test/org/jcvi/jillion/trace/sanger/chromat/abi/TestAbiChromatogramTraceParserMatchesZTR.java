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

package org.jcvi.jillion.trace.sanger.chromat.abi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.sanger.chromat.abi.AbiChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.abi.AbiChromatogramBuilder;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.sanger.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestAbiChromatogramTraceParserMatchesZTR {
    private ResourceHelper resources = new ResourceHelper(TestAbiChromatogramTraceParserMatchesZTR.class);

   

    @Test
    public void ab1DataMatchesZtrData() throws FileNotFoundException, TraceDecoderException, IOException{
    	String id = "SDBHD01T00PB1A1672F";
    	ZtrChromatogram ztr = new ZtrChromatogramBuilder(id, resources.getFile("files/SDBHD01T00PB1A1672F.ztr"))
    										.build();
    	AbiChromatogram abi = new AbiChromatogramBuilder("SDBHD01T00PB1A1672F", resources.getFile("files/SDBHD01T00PB1A1672F.ab1"))
    										.build();
    	assertEquals(ztr.getNucleotideSequence(), abi.getNucleotideSequence());
        assertEquals(ztr.getPositionSequence(), abi.getPositionSequence());
        assertEquals(ztr.getQualitySequence(), abi.getQualitySequence());
        assertEquals(ztr.getChannelGroup(), abi.getChannelGroup());
        assertEquals(ztr.getNumberOfTracePositions(), abi.getNumberOfTracePositions());
        assertCommentsCorrect(ztr.getComments(), abi.getComments());
  
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
