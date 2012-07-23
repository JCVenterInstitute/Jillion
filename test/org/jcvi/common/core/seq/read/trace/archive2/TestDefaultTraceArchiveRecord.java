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
 * Created on Jun 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.seq.read.trace.archive2.DefaultTraceArchiveRecord;
import org.jcvi.common.core.seq.read.trace.archive2.TraceArchiveRecord;
import org.jcvi.common.core.seq.read.trace.archive2.TraceInfoField;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultTraceArchiveRecord {

    private static final Map<String, String> EMPTY_EXTENDED_DATA = Collections.<String,String>emptyMap();
    private static final TraceInfoField key1 = TraceInfoField.ACCESSION;
    private static final TraceInfoField key2 = TraceInfoField.CENTER_NAME;
    private static final String value1 = "value1";
    private static final String value2 = "value2";
    
    private static final Map<TraceInfoField, String> EMPTY_MAP = Collections.emptyMap();
    private static final Map<TraceInfoField, String> MAP_ONE_ENTRY;
    private static final Map<TraceInfoField, String> MAP_TWO_ENTRIES;
    
    private static final Map<String, String> EXTENDED_DATA;
    
    static{
        MAP_ONE_ENTRY = new HashMap<TraceInfoField, String>();
        MAP_ONE_ENTRY.put(key1,value1);
        
        MAP_TWO_ENTRIES = new HashMap<TraceInfoField, String>();
        MAP_TWO_ENTRIES.put(key1,value1);
        MAP_TWO_ENTRIES.put(key2,value2);
        
        EXTENDED_DATA = new HashMap<String, String>();
        EXTENDED_DATA.put("extra_data_1", "extra_value_1");
        EXTENDED_DATA.put("extra_data_2", "extra_value_2");
    }
    private final DefaultTraceArchiveRecord sut = new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,EXTENDED_DATA);
    @Test(expected = IllegalArgumentException.class)
    public void nullMapShouldThrowIllegalArgumentException(){
        new DefaultTraceArchiveRecord(null,EXTENDED_DATA);
    }
    @Test(expected = IllegalArgumentException.class)
    public void nullExtendedDataShouldThrowIllegalArgumentException(){
        new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,null);
    }
    
    @Test
    public void extendedData(){
        assertEquals(EXTENDED_DATA, sut.getExtendedData());
    }
    @Test
    public void emptyMap(){
        TraceArchiveRecord emptyRecord = new DefaultTraceArchiveRecord(EMPTY_MAP,EMPTY_EXTENDED_DATA);
        assertTrue(emptyRecord.entrySet().isEmpty());
        assertNull(emptyRecord.getAttribute(key1));
        assertFalse(emptyRecord.contains(key1));
        assertTrue(emptyRecord.getExtendedData().isEmpty());
    }
    
    @Test
    public void oneElement(){        
        TraceArchiveRecord oneRecord = new DefaultTraceArchiveRecord(MAP_ONE_ENTRY,EMPTY_EXTENDED_DATA);
        assertEntriesMatch(oneRecord, MAP_ONE_ENTRY);
    }
    
    @Test
    public void twoElements(){
        TraceArchiveRecord twoRecords = new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,EMPTY_EXTENDED_DATA);      
        assertEntriesMatch(twoRecords, MAP_TWO_ENTRIES);
    }
    @Test
    public void testToString(){
        assertEquals(MAP_TWO_ENTRIES.toString(), new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,EMPTY_EXTENDED_DATA).toString());
    }

    private void assertEntriesMatch(TraceArchiveRecord record, Map<TraceInfoField,String> map){
        for(Entry<TraceInfoField, String> entry: record.entrySet()){
            assertEquals(entry.getValue(), map.get(entry.getKey()));
        }
    }
    
    @Test
    public void equalsSameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualToDifferentClass(){
        assertFalse(sut.equals("Not a DefaultTraceArchiveRecord"));
    }
    @Test
    public void notEqualtoDifferentNumberOfElements(){
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, 
                    new DefaultTraceArchiveRecord(MAP_ONE_ENTRY,EMPTY_EXTENDED_DATA));
    }
}
