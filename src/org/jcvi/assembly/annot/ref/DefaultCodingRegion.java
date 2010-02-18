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
 * Created on Dec 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.CommonUtil;
import org.jcvi.Range;
import org.jcvi.assembly.annot.Exon;

public class DefaultCodingRegion implements CodingRegion {
    private Range range; 
    private CodingRegionState startState, endState; 
    private List<Exon> exons;
    
    public DefaultCodingRegion(Range range, CodingRegionState startState, CodingRegionState endState, List<Exon> exons){
        canNotBeNull(range, startState, endState, exons);
        this.range = range;
        this.startState = startState;
        this.endState = endState;
        this.exons = Collections.unmodifiableList(new ArrayList<Exon>(exons));
    }
    private void canNotBeNull(Range range, CodingRegionState startState,
            CodingRegionState endState, List<Exon> exons) {
        if(range ==null){
            throw new IllegalArgumentException("range can not be null");
        }
        if(startState ==null){
            throw new IllegalArgumentException("start state can not be null");
        }
        if(endState ==null){
            throw new IllegalArgumentException("end state can not be null");
        }
        if(exons ==null){
            throw new IllegalArgumentException("exons can not be null");
        }
    }
    @Override
    public CodingRegionState getEndCodingRegionState() {
        return endState;
    }

    @Override
    public List<Exon> getExons() {
        return exons;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public CodingRegionState getStartCodingRegionState() {
        return startState;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + range.hashCode();
        result = prime * result + startState.hashCode();
        result = prime * result + endState.hashCode();
        result = prime * result + exons.hashCode();       
        
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultCodingRegion)){
            return false;
        }
        DefaultCodingRegion other = (DefaultCodingRegion) obj;
       return CommonUtil.similarTo(getRange(), other.getRange())
       && CommonUtil.similarTo(getStartCodingRegionState(), other.getStartCodingRegionState())
       && CommonUtil.similarTo(getEndCodingRegionState(), other.getEndCodingRegionState())
       && CommonUtil.similarTo(getExons(), other.getExons());
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.getClass().getName());
        result.append(" : ");
        result.append(range);
        result.append(" start complete ?  ");
        result.append(getStartCodingRegionState());
        result.append(" end complete ?  ");
        result.append(getEndCodingRegionState());
        result.append(" exons : ");
        result.append(Arrays.toString(exons.toArray()));
        return result.toString();
    }
    
    
    

}
