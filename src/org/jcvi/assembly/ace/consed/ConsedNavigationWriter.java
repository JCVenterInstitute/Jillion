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

package org.jcvi.assembly.ace.consed;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;

/**
 * {@code ConsedNavigationWriter} write Consed
 * Custom Navigation Files to allow consed
 * users to quickly jump to feature locations
 * while editing.
 * @author dkatzel
 *
 *
 */
public class ConsedNavigationWriter implements Closeable{

    private final OutputStream out;
    public ConsedNavigationWriter(String title, OutputStream out) throws IOException{
        if(title ==null){
            throw new NullPointerException("title can not be null");
        }
        out.write(String.format("TITLE: %s\n",title).getBytes());
        this.out= out;
        
    }
    @Override
    public void close() throws IOException {
        out.close();
    }
    
    public void writeNavigationElement(ReadNavigationElement element) throws IOException{
        
        StringBuilder builder = new StringBuilder("BEGIN_REGION\n");
        builder.append(String.format("TYPE: %s\n",element.getType()));
        builder.append(String.format("READ: %s\n",element.getElementId()));
        Range range = element.getUngappedPositionRange().convertRange(CoordinateSystem.RESIDUE_BASED);
        builder.append(String.format("UNPADDED_READ_POS: %d %d\n",range.getLocalStart(), range.getLocalEnd()));
        if(element.getComment() !=null){
            builder.append(String.format("COMMENT: %s\n",element.getComment()));
        }
        builder.append("END_REGION\n");
        out.write(builder.toString().getBytes());
    }
    
    public void writeNavigationElement(ConsensusNavigationElement element) throws IOException{
        
        StringBuilder builder = new StringBuilder("BEGIN_REGION\n");
        builder.append(String.format("TYPE: %s\n",element.getType()));
        builder.append(String.format("CONTIG: %s\n",element.getElementId()));
        Range range = element.getUngappedPositionRange().convertRange(CoordinateSystem.RESIDUE_BASED);
        builder.append(String.format("UNPADDED_CONS_POS: %d %d\n",range.getLocalStart(), range.getLocalEnd()));
        if(element.getComment() !=null){
            builder.append(String.format("COMMENT: %s\n",element.getComment()));
        }
        builder.append("END_REGION\n");
        
        out.write(builder.toString().getBytes());
    }
}
