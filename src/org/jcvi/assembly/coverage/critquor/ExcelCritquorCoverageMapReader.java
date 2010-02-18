/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jcvi.Range;

public class ExcelCritquorCoverageMapReader implements CritquorCoverageMapReader {

    @Override
    public CritiquorCovereageMap read(InputStream inputStream)
            throws IOException {
        
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        HSSFSheet workSheet =workbook.getSheetAt(0);
        
        int lastRowNumber = workSheet.getLastRowNum();
        DefaultCritiquorCoverageMap.Builder builder = new DefaultCritiquorCoverageMap.Builder();
        for(int rowIndex=1; rowIndex <=lastRowNumber; rowIndex++){
            
            final HSSFRow row = workSheet.getRow(rowIndex);
            String key =row.getCell(5).getRichStringCellValue().getString();
                
                Range targetRange = Range.buildRange((long)row.getCell(10).getNumericCellValue(),
                        (long)row.getCell(12).getNumericCellValue() -1);
                builder.addTargetRange(key, targetRange);
            
        }
        final DefaultCritiquorCoverageMap map = builder.build();
        return map;
    }
}
