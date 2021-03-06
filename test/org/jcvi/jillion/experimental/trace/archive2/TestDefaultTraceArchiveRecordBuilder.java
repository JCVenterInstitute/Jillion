/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.experimental.trace.archive2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.experimental.trace.archive2.DefaultTraceArchiveRecord;
import org.jcvi.jillion.experimental.trace.archive2.TraceInfoField;
import org.jcvi.jillion.experimental.trace.archive2.DefaultTraceArchiveRecord.Builder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestDefaultTraceArchiveRecordBuilder {

    private static final TraceInfoField key1 = TraceInfoField.ACCESSION;
    private static final TraceInfoField key2 = TraceInfoField.CENTER_NAME;
    private static final String value1 = "value1";
    private static final String value2 = "value2";
    
    private static final Map<TraceInfoField, String> EMPTY_MAP = Collections.emptyMap();
    private static final Map<TraceInfoField, String> MAP_ONE_ENTRY;
    private static final Map<TraceInfoField, String> MAP_TWO_ENTRIES;
    
    private static final Map<String, String> EXTENDED_DATA;
    private static final Map<String, String> EMPTY_EXTENDED_DATA = Collections.<String,String>emptyMap();
    
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
    private DefaultTraceArchiveRecord.Builder sut;
    @Before
    public void setup(){
        sut = new DefaultTraceArchiveRecord.Builder();
    }
    @Test
    public void notAddingAnythingShouldCreateEmptyRecord(){
        assertEquals(new DefaultTraceArchiveRecord(EMPTY_MAP,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    @Test
    public void oneRecord(){
        assertSame(sut,sut.put(key1, value1));
        assertEquals(new DefaultTraceArchiveRecord(MAP_ONE_ENTRY,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    @Test
    public void twoRecords(){
        assertSame(sut,sut.put(key1, value1));
        assertSame(sut,sut.put(key2, value2));
        assertEquals(new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    @Test
    public void putAll(){
        assertEquals(sut,sut.putAll(MAP_TWO_ENTRIES));
        assertEquals(new DefaultTraceArchiveRecord(MAP_TWO_ENTRIES,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    
    @Test
    public void remove(){
        assertSame(sut,sut.putAll(MAP_TWO_ENTRIES));
        assertSame(sut,sut.remove(key2));
        assertEquals(new DefaultTraceArchiveRecord(MAP_ONE_ENTRY,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    @Test
    public void removeFromEmptyMapShouldDoNothing(){
        assertSame(sut,sut.remove(key1));
        assertEquals(new DefaultTraceArchiveRecord(EMPTY_MAP,EMPTY_EXTENDED_DATA),
                sut.build());
    }
    
    @Test
    public void addExtendedData(){
        for(Entry<String, String> entry : EXTENDED_DATA.entrySet()){
            sut.putExtendedData(entry.getKey(), entry.getValue());
        }
        assertEquals(new DefaultTraceArchiveRecord(EMPTY_MAP,EXTENDED_DATA),
                sut.build());
    }
    @Test
    public void removeExtendedData(){
        for(Entry<String, String> entry : EXTENDED_DATA.entrySet()){
            sut.putExtendedData(entry.getKey(), entry.getValue());
            sut.removeExtendedData(entry.getKey());
        }
        assertEquals(new DefaultTraceArchiveRecord(EMPTY_MAP,EMPTY_EXTENDED_DATA),
                sut.build());
    }
}
