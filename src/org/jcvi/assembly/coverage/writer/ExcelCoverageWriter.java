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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;

public class ExcelCoverageWriter<T extends Placed> implements CoverageWriter<T> {

    private OutputStream out;
    public ExcelCoverageWriter(OutputStream out){
        this.out = out;
    }
    @Override
    public void write(CoverageMap<CoverageRegion<T>> coverageMap) throws IOException {
        
        HSSFWorkbook wb = new HSSFWorkbook();
        // create a new sheet
        HSSFSheet s = wb.createSheet();      
        HSSFRow row =s.createRow(0);
        row.createCell(0).setCellValue(new HSSFRichTextString("coordinate"));
        row.createCell(1).setCellValue(new HSSFRichTextString("coverage"));

        for(int i=0; i<coverageMap.getNumberOfRegions(); i++){
            CoverageRegion<T> region = coverageMap.getRegion(i);
            writeRow(region, s.createRow(i+1));
            
        }
        CoverageRegion<T> lastRegion =coverageMap.getRegion(coverageMap.getNumberOfRegions()-1);
        writeRow(lastRegion, s.createRow(coverageMap.getNumberOfRegions()+1));
        wb.write(out);
        
    }
    private void writeRow(CoverageRegion<T> lastRegion, HSSFRow r) {
        r.createCell(0).setCellValue(lastRegion.getStart());
        r.createCell(1).setCellValue(lastRegion.getCoverage());
    }
    @Override
    public void close() throws IOException {
        out.close();
        
    }

}
