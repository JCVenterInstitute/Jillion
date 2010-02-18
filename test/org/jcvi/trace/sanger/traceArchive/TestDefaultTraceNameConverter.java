/*
 * Created on Sep 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.jcvi.datastore.DataStoreException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultTraceNameConverter {

    private static final Map<String, Long> EXPECTED_MAPPING= new HashMap<String, Long>();
    @BeforeClass
    public static void setupMap(){
        EXPECTED_MAPPING.put("trace1", 1L);
        EXPECTED_MAPPING.put("trace2", 2L);
        EXPECTED_MAPPING.put("trace3", 3L);
        EXPECTED_MAPPING.put("trace4", 4L);
    }
    
    DefaultTraceNameConverter sut;
    TraceArchiveInfo mockTraceArchiveInfo;
    TraceArchiveRecordIdGenerator traceIdGenerator;
    
    @Before
    public void setupSut() throws DataStoreException{
        mockTraceArchiveInfo = createMock(TraceArchiveInfo.class);
        traceIdGenerator = createMock(TraceArchiveRecordIdGenerator.class);
        List<TraceArchiveRecord> mockTraceRecords = new ArrayList<TraceArchiveRecord>();
        for(Entry<String, Long> entry : EXPECTED_MAPPING.entrySet()){
            TraceArchiveRecord mockRecord = createMock(TraceArchiveRecord.class);            
            expect(traceIdGenerator.generateIdFor(mockRecord)).andReturn(entry.getKey());
            expect(mockRecord.getAttribute(TraceInfoField.TRACE_NAME)).andReturn(entry.getValue().toString());
            mockTraceRecords.add(mockRecord);
            replay(mockRecord);
        }
        expect(mockTraceArchiveInfo.iterator()).andReturn(mockTraceRecords.iterator());
        expect(mockTraceArchiveInfo.size()).andReturn(EXPECTED_MAPPING.size());
        replay(mockTraceArchiveInfo,traceIdGenerator);
        sut = new DefaultTraceNameConverter(mockTraceArchiveInfo, traceIdGenerator);
    }
    
    @Test
    public void convertTraceName(){
        for(Entry<String, Long> entry : EXPECTED_MAPPING.entrySet()){
            assertEquals(entry.getValue(), sut.convertId(entry.getKey()));
        }
    }
    
    @Test(expected = NoSuchElementException.class)
    public void nameNotFoundShouldThrowNoSuchElementException(){
        sut.convertId("not a valid id");
    }
    
}
