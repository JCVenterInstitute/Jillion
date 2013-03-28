/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

public class PhaseChangeCasAlignmentRegion implements CasAlignmentRegion{
    private final byte phaseChange;
    public PhaseChangeCasAlignmentRegion(byte phaseChange){
        this.phaseChange = phaseChange;
    }
    @Override
    public long getLength() {
        //length is always 0?
        return 0L;
    }

    @Override
    public CasAlignmentRegionType getType() {
        return CasAlignmentRegionType.PHASE_CHANGE;
    }
    
    public byte getPhaseChange() {
        return phaseChange;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + phaseChange;
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        PhaseChangeCasAlignmentRegion other = (PhaseChangeCasAlignmentRegion) obj;
        if (phaseChange != other.phaseChange){
            return false;
        }
        return true;
    }

    
}
