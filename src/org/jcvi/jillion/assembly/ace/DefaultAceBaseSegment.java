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
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ace;

import org.jcvi.jillion.core.Range;

class DefaultAceBaseSegment implements AceBaseSegment{

    private final String name;
    private final Range gappedConsensusRange;
    
    /**
     * @param name
     * @param gappedConsensusRange
     * @throws NullPointerException if either parameter is null.
     */
    public DefaultAceBaseSegment(String name, Range gappedConsensusRange) {
        if(name ==null || gappedConsensusRange==null){
            throw new NullPointerException("parameters can not be null");
        }
        this.name = name;
        this.gappedConsensusRange = gappedConsensusRange;
    }

    @Override
    public Range getGappedConsensusRange() {
        return gappedConsensusRange;
    }

    @Override
    public String getReadName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                +  gappedConsensusRange
                        .hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AceBaseSegment)) {
            return false;
        }
        AceBaseSegment other = (AceBaseSegment) obj;
       return getReadName().equals(other.getReadName()) 
       && getGappedConsensusRange().equals(other.getGappedConsensusRange());
    }

    @Override
    public String toString() {
        return "DefaultAceBaseSegment [name=" + name
                + ", gappedConsensusRange=" + gappedConsensusRange + "]";
    }

}
