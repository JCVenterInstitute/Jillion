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
 * Created on Feb 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import org.jcvi.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.read.SequenceDirection;

public class AssembledFrom implements Comparable<AssembledFrom>{

    private final String id;
    private final SequenceDirection dir;
    private final int startOffset;
    
    public static AssembledFrom createFrom(PlacedRead read, long ungappedFullLength){
        final Range validRange;
        SequenceDirection dir = read.getSequenceDirection();
        Range readValidRange = read.getValidRange();
        if(dir==SequenceDirection.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(readValidRange, ungappedFullLength);
        }
        else{
            validRange = readValidRange;
        }
        return new AssembledFrom(read.getId(), 
                (int)(read.getStart()-validRange.getStart()+1),dir);
    }
    /**
     * @param id
     * @param startOffset
     * @param complimented
     */
    public AssembledFrom(String id, int startOffset, SequenceDirection dir) {
        if(id ==null){
            throw new IllegalArgumentException("id can not be null");
        }
        this.id = id;
        this.startOffset = startOffset;
        this.dir = dir;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (!(obj instanceof AssembledFrom)){
            return false;
        }
        AssembledFrom other = (AssembledFrom) obj;
        return id.equals(other.getId());
    }
    public String getId() {
        return id;
    }

    public int getStartOffset() {
        return startOffset;
    }
    
    public SequenceDirection getSequenceDirection(){
        return dir;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(" ").append(startOffset).append("is complimented? ").append(dir ==SequenceDirection.REVERSE);
        return builder.toString();
    }
    /**
    * Compares two AssembledFrom instances and compares them based on start offset
    * then by Id.  This should match the order of AssembledFrom records 
    * (and reads) in an .ace file.
    */
    @Override
    public int compareTo(AssembledFrom o) {
        int cmp= Integer.valueOf(getStartOffset()).compareTo(o.getStartOffset());
        if(cmp !=0){
            return cmp;
        }
        return getId().compareTo(o.getId());
    }
    
    
}
