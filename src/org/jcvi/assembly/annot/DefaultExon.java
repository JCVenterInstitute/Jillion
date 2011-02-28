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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;

import org.jcvi.CommonUtil;
import org.jcvi.Range;

public class DefaultExon implements Exon {
    private Frame frame;
    private Range range;
    public DefaultExon(Frame frame, Range range){
        if(frame ==null){
            throw new IllegalArgumentException("frame can not be null");
        }
        this.frame = frame;
        this.range= range;
    }
    public DefaultExon(Frame frame, long start, long end){
      this(frame, Range.buildRange(start, end));
    }
    
    @Override
    public long getEndPosition() {
        return range.getEnd();
    }

    @Override
    public Frame getFrame() {
        return frame;
    }

    @Override
    public long getStartPosition() {
        return range.getStart();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + frame.hashCode();
        result = prime * result + range.hashCode();      
        
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
       
        if (!(obj instanceof Exon)){
            return false;
        }
        DefaultExon other = (DefaultExon) obj;
        return CommonUtil.similarTo(getFrame(), other.getFrame())
        && CommonUtil.similarTo(getStartPosition(), other.getStartPosition())
        && CommonUtil.similarTo(getEndPosition(), other.getEndPosition());
        
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("DefaultExon : ");
        result.append("frame = ");
        result.append(getFrame());
        result.append(" range " );
        result.append(range);
        return result.toString();
    }
    
    

}
