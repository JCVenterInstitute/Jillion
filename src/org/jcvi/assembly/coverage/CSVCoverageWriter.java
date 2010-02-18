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
