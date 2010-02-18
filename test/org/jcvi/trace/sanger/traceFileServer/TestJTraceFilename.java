/*
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.jcvi.trace.sanger.traceFileServer.JTraceFilenameUtil.SourceLIMS;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestJTraceFilename {
    private final String JLIMS_traceFileName = "F319112_C24_JTC_POTATO-B-01-100-110KB_1064145307578_1064145307602_094_1119728942909.ztr";
    
    private final String Tracker_barcoded_traceFileName = "A300O2YV_B22_JTC_JAAA311TF_1090617433951_1090617433962_095_1094392116666.ztr";
    
    private final String Tracker_non_barcoded_traceFileName = "A-1038559-560-561_A05_TIGR_HMX8Z64T1001EWALK65B_1038561_1119610671976_001_1119728634200.ztr";
    
    private final String external_traceFileName = "Unknown_Z99_EXT_BMMBZ17TR_0_0_000_1108821227875.ztr";
    
    @Test
    public void invalidFileNameShouldThrowIllegalArgumentException(){
        try{
            JTraceFilenameUtil.getPlateIDFrom("not a valid filename");
            fail("should throw IllegalArgumentException");
        }catch(IllegalArgumentException e){
            assertEquals("not a valid filename is not a JTrace filename", e.getMessage());
        }
    }
    @Test(expected = NullPointerException.class)
    public void nullFileNameShouldThrowNullPointerException(){
        JTraceFilenameUtil.getPlateIDFrom(null);
          
    }
    @Test
    public void getFileFormat(){
        assertEquals("JLIMS", "ztr", JTraceFilenameUtil.getFileFormatFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "ztr", JTraceFilenameUtil.getFileFormatFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "ztr", JTraceFilenameUtil.getFileFormatFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External","ztr", JTraceFilenameUtil.getFileFormatFrom(external_traceFileName));
    }
    @Test
    public void getTraceFileID(){
        assertEquals("JLIMS", 1119728942909L, JTraceFilenameUtil.getTraceFileIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1094392116666L, JTraceFilenameUtil.getTraceFileIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1119728634200L, JTraceFilenameUtil.getTraceFileIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External",1108821227875L, JTraceFilenameUtil.getTraceFileIDFrom(external_traceFileName));
    }
    
    @Test
    public void getCapillaryID(){
        assertEquals("JLIMS", (short)94, JTraceFilenameUtil.getCapillaryIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", (short)95, JTraceFilenameUtil.getCapillaryIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", (short)1, JTraceFilenameUtil.getCapillaryIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", (short)0, JTraceFilenameUtil.getCapillaryIDFrom(external_traceFileName));
    }
    
    @Test
    public void getTraceID(){
        assertEquals("JLIMS", 1064145307602L, JTraceFilenameUtil.getTraceIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1090617433962L, JTraceFilenameUtil.getTraceIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1119610671976L, JTraceFilenameUtil.getTraceIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", 0L, JTraceFilenameUtil.getTraceIDFrom(external_traceFileName));
    }
    @Test
    public void getRunID(){
        assertEquals("JLIMS", 1064145307578L, JTraceFilenameUtil.getRunIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", 1090617433951L, JTraceFilenameUtil.getRunIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", 1038561L, JTraceFilenameUtil.getRunIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", 0L, JTraceFilenameUtil.getRunIDFrom(external_traceFileName));
    }
    
    @Test
    public void getPlateId(){
        assertEquals("JLIMS", "F319112", JTraceFilenameUtil.getPlateIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "A300O2YV", JTraceFilenameUtil.getPlateIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "A-1038559-560-561", JTraceFilenameUtil.getPlateIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "Unknown", JTraceFilenameUtil.getPlateIDFrom(external_traceFileName));
    }
    @Test
    public void getPlateWell(){
        assertEquals("JLIMS", "C24", JTraceFilenameUtil.getPlateWellFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "B22", JTraceFilenameUtil.getPlateWellFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "A05", JTraceFilenameUtil.getPlateWellFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "Z99", JTraceFilenameUtil.getPlateWellFrom(external_traceFileName));
    }
    
    @Test
    public void getSourceLIMS(){
        assertEquals("JLIMS", SourceLIMS.JTC, JTraceFilenameUtil.getSourceLIMSFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", SourceLIMS.JTC, JTraceFilenameUtil.getSourceLIMSFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", SourceLIMS.TIGR, JTraceFilenameUtil.getSourceLIMSFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", SourceLIMS.EXT, JTraceFilenameUtil.getSourceLIMSFrom(external_traceFileName));
    }
    
    @Test
    public void getLIMSParentID(){
        assertEquals("JLIMS", "POTATO-B-01-100-110KB", JTraceFilenameUtil.getLIMSParentIDFrom(JLIMS_traceFileName));
        assertEquals("Tracker_barcode", "JAAA311TF", JTraceFilenameUtil.getLIMSParentIDFrom(Tracker_barcoded_traceFileName));
        assertEquals("Tracker_nonbarcode", "HMX8Z64T1001EWALK65B", JTraceFilenameUtil.getLIMSParentIDFrom(Tracker_non_barcoded_traceFileName));
        assertEquals("External", "BMMBZ17TR", JTraceFilenameUtil.getLIMSParentIDFrom(external_traceFileName));
    }
}
