/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.chromat.abi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestAbiChromatogramTraceParserMatchesZTR {
    private ResourceHelper resources = new ResourceHelper(TestAbiChromatogramTraceParserMatchesZTR.class);

   

    @Test
    public void ab1DataMatchesZtrData() throws IOException{
    	String id = "SDBHD01T00PB1A1672F";
    	ZtrChromatogram ztr = new ZtrChromatogramBuilder(id, resources.getFile("files/SDBHD01T00PB1A1672F.ztr"))
    										.build();
    	AbiChromatogram abi = new AbiChromatogramBuilder("SDBHD01T00PB1A1672F", resources.getFile("files/SDBHD01T00PB1A1672F.ab1"))
    										.build();
    	assertEquals(ztr.getNucleotideSequence(), abi.getNucleotideSequence());
        assertEquals(ztr.getPeakSequence(), abi.getPeakSequence());
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
