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
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;
import java.io.OutputStream;

public class SffVisitorWriter implements SffFileVisitor{

    private final OutputStream out;
   
    
    
    /**
     * @param out
     */
    public SffVisitorWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public CommonHeaderReturnCode visitCommonHeader(SffCommonHeader header) {
        try {         
            SffWriter.writeCommonHeader(header, out);
        } catch (IOException e) {
            throw new IllegalStateException("error writing Sff Common Header ",e);
        }
        return CommonHeaderReturnCode.PARSE_READS;
        
    }

    @Override
    public ReadDataReturnCode visitReadData(SffReadData readData) {
       
        try {
          SffWriter.writeReadData(readData, out);
        } catch (IOException e) {
            throw new IllegalStateException("error writing Sff read Data ",e);
        }
        return ReadDataReturnCode.PARSE_NEXT_READ;
    }

    @Override
    public ReadHeaderReturnCode visitReadHeader(SffReadHeader readHeader) {
       try {
       SffWriter.writeReadHeader(readHeader, out);
    } catch (IOException e) {
        throw new IllegalStateException("error writing Sff read header ",e);
    }
    return ReadHeaderReturnCode.PARSE_READ_DATA;
        
    }

    
    @Override
    public void visitEndOfFile() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFile() {
        // TODO Auto-generated method stub
        
    }

}
