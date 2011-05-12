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

package org.jcvi.plate;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.plate.Well.IndexOrder;

public final class NextWellIterator{
    private final IndexOrder indexOrder;
    private final SortedSet<org.jcvi.plate.Well> unusedWells;
    private final PlateFormat format;
   
    /**
     * @param indexOrder
     */
    public NextWellIterator(IndexOrder indexOrder,PlateFormat format) {
        this.indexOrder = indexOrder;
        this.format = format;
        unusedWells = new TreeSet<org.jcvi.plate.Well>(indexOrder.createWellComparator(format));
        newPlate();
    }
    
    public boolean isUnused(org.jcvi.plate.Well well){
        return unusedWells.contains(well);
    }
    public void use(org.jcvi.plate.Well well){
        if(!isUnused(well)){
            throw new IllegalArgumentException("already used "+ well);
        }
        unusedWells.remove(well);
        
    }
    
    public boolean isFull(){
        return unusedWells.isEmpty();
    }
    public Well nextUnusedWell(){
        if(isFull()){
            throw new NoSuchElementException("no more unused wells");
        }
        return unusedWells.first();
        
    }
    public void finishedPlate(){
        unusedWells.clear();
    }
    public void newPlate(){
        finishedPlate();
        for(int i=0; i<format.getNumberOfWells(); i++){
            unusedWells.add(Well.computeWell(format,i, indexOrder));
        }
    }
}
