/*
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.File;
import java.io.IOException;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;
public class TestDefaultFolderTraceArchiveDataStore {

    private DefaultFolderTraceArchiveDataStore sut;
    private static final String FOLDER_ROOT_DIR = "files/exampleTraceArchive";
    private static final TraceArchiveRecordIdGenerator ID_GENERATOR= new NameTagTraceArchiveRecordIdGenerator();
    private TraceArchiveInfo traceInfo ;
    private String absoluteRootPath;
    
    @Before
    public void setup() throws IOException{
       traceInfo = new DefaultTraceArchiveInfo(
                new TraceInfoXMLTraceArchiveInfoBuilder<TraceArchiveRecord>(
                ID_GENERATOR, 
                TestDefaultFolderTraceArchiveDataStore.class.getResourceAsStream(FOLDER_ROOT_DIR+"/TRACEINFO.xml")));
       
       absoluteRootPath = new File(TestDefaultFolderTraceArchiveDataStore.class.getResource(FOLDER_ROOT_DIR).getFile()).getAbsolutePath();
       sut = new DefaultFolderTraceArchiveDataStore(
               absoluteRootPath ,
                traceInfo);
       
    }
    private static void assertTraceArchiveTraceValuesEqual(TraceArchiveTrace expected, TraceArchiveTrace actual){
        assertEquals(expected.getBasecalls().decode(), actual.getBasecalls().decode());
        assertEquals(expected.getQualities().decode(), actual.getQualities().decode());
        assertEquals(expected.getPeaks().getData().decode(), actual.getPeaks().getData().decode());
    }
    @Test
    public void getTrace() throws DataStoreException{
        String tracename = "XX08A02T44F09PB11F";
        TraceArchiveTrace expectedTrace = new DefaultTraceArchiveTrace(traceInfo.get(tracename),absoluteRootPath);
        assertTraceArchiveTraceValuesEqual(expectedTrace, sut.get(tracename));
    }
    
    @Test
    public void traceDoesNotExistShouldThrowTraceDecoderException(){
        String idThatDoesNotExist = "doesNotExist";
        try{
            sut.get(idThatDoesNotExist);
            fail("should Throw TraceDecoderException");
        }
        catch(DataStoreException e){
            assertEquals(idThatDoesNotExist + " does not exist", e.getMessage());
        }
        
    }
}
