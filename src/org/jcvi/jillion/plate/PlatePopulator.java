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

package org.jcvi.jillion.plate;

import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jcvi.jillion.plate.Well.IndexOrder;
/**
 * {@code PlatePopulator} is a way to abstract away
 * how a plate is to be populated.  All the logic on what the next
 * well to fill is encapsulated in this class so callers of this class
 * don't need to worry about the business logic.
 * @author dkatzel
 */
public final class PlatePopulator{
    
    private final IndexOrder indexOrder;
    
    private final SortedSet<Well> unusedWells;
    
    private final PlateFormat format;
   
    /**
     * Create a new PlatePopulator.
     * @param indexOrder the order that the wells
     * should get populated.
     * @param format the {@link PlateFormat} of the
     * plate(s) to be populated.
     * @throws NullPointerException if any arguments are null.
     */
    public PlatePopulator(IndexOrder indexOrder,PlateFormat format) {
        if(indexOrder==null){
            throw new NullPointerException("index order can not be null");
        }
        if(format==null){
            throw new NullPointerException("plate format can not be null");
        }
        this.indexOrder = indexOrder;
        this.format = format;
        unusedWells = new TreeSet<Well>(indexOrder.createWellComparator(format));
        newPlate();
    }
    /**
     * Is the given well used on this plate already.
     * @param well the well to check.
     * @return {@code true} if the well has been used; {@code false} otherwise.
     * @throws NullPointerException if well is null.
     */
    public boolean isUnused(Well well){
        if(well==null){
            throw new NullPointerException("well can not be null");
        }
        return unusedWells.contains(well);
    }
    /**
     * Mark the given well as being used.
     * @param well the well to mark as being used.
     * @throws NullPointerException if well is null.
     * @throws IllegalArgumentException if the well has
     * already been used.
     */
    public void use(Well well){
        if(well==null){
            throw new NullPointerException("well can not be null");
        }
        if(!isUnused(well)){
            throw new IllegalArgumentException("already used "+ well);
        }
        unusedWells.remove(well);
        
    }
    
    public int getNumberOfUnusedWells(){
    	return unusedWells.size();
    }
    /**
     * Is this plate finished or does this plate no longer have any unused wells
     * on it. 
     * @return {@code true} if the plate is finished or  has used up all its
     * wells; {@code false} otherwise.
     * @see #use(Well)
     * @see #finishedPlate()
     */
    public boolean isFull(){
        return unusedWells.isEmpty();
    }
    /**
     * Get the next unused well based on the IndexOrder.
     * WARNING: calling this method does not mark
     * the well as used, you must also call
     * {@link #use(Well)}.
     * @return the new unused well, will never be null.
     * @throws NoSuchElementException if the plate is full.
     * @see #use(Well)
     */
    public Well nextUnusedWell(){
        if(isFull()){
            throw new NoSuchElementException("no more unused wells");
        }
        return unusedWells.first();
        
    }
    /**
     * Mark the current plate as finished.  Once a plate is marked
     * as finished, it is considered full.
     */
    public void finishedPlate(){
        unusedWells.clear();
    }
    /**
     * Mark the current plate as finished and begin a using
     * a new empty plate.
     */
    public void newPlate(){
        finishedPlate();
        for(int i=0; i<format.getNumberOfWells(); i++){
            unusedWells.add(Well.computeWell(format,i, indexOrder));
        }
    }
}
