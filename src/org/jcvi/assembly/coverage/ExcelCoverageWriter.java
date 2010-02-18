/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jcvi.assembly.Placed;

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

        for(int i=0; i<coverageMap.getSize(); i++){
            CoverageRegion<T> region = coverageMap.getRegion(i);
            writeRow(region, s.createRow(i+1));
            
        }
        CoverageRegion<T> lastRegion =coverageMap.getRegion(coverageMap.getSize()-1);
        writeRow(lastRegion, s.createRow(coverageMap.getSize()+1));
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
