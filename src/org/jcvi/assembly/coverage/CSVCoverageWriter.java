/*
 * Created on May 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.assembly.Placed;

public class CSVCoverageWriter implements CoverageWriter<Placed> {
    private final OutputStream out;
    
    public CSVCoverageWriter(OutputStream out) throws IOException{
        this.out = out;
        out.write(String.format("offset,coverage%n").getBytes());
    }
    @Override
    public void write(CoverageMap<CoverageRegion<Placed>> mapToWrite)
            throws IOException {
        for(CoverageRegion<Placed> region : mapToWrite){
            for(long i = region.getStart(); i<=region.getEnd(); i++){
                out.write(String.format("%d,%d%n", i,region.getCoverage()).getBytes());
            }
        }
        
    }

    @Override
    public void close() throws IOException {
        out.close();
        
    }

}
