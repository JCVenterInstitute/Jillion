/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

/**
 * Get the appropriate {@link CritquorCoverageMapReader}
 * based on File Extension.
 * @author dkatzel
 *
 *
 */
public class CritiquorCoverageMapReaderFactory {
    private static final CritquorCoverageMapReader EXCEL_READER = new ExcelCritquorCoverageMapReader();
    private static final CritquorCoverageMapReader CSV_READER = new CSVCritquorCoverageMapReader();
    
    public static CritquorCoverageMapReader getReaderFor(String fileName){
        if(fileName.endsWith(".xls")){
            return EXCEL_READER;
        }
        return CSV_READER;
    }
}
