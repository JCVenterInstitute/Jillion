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
