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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.plate;

/**
 * {@code PlateFormat} is the the format for the
 * plate wells used for sanger based sequencing.
 * @author dkatzel
 *
 *
 */
public enum PlateFormat {
    /**
     * A 384 well plate.
     */
    _384(384,24),
    /**
     * A 96 well plate.
     */
    _96(96,12);
    private final int numberOfWells;

    private final int numberOfColumns;
    private final int numberOfRows;

    /**
     * @param numberOfColumns
     */
    PlateFormat(int numberOfWells,int numberOfColumns) {
        this.numberOfWells = numberOfWells;
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows =  numberOfWells/numberOfColumns;
    }

    public static PlateFormat getFormatFor(int numberOfWells){
    	
    	if(numberOfWells <1){
    		throw new IllegalArgumentException("number of wells must be positive");
    	}
    	for(PlateFormat f : values()){
    		if(numberOfWells ==f.getNumberOfRows()){
    			return f;
    		}
    	}
    	throw new IllegalArgumentException("no plate format for " + numberOfWells);
    }
    
    public int getNumberOfWells() {
        return numberOfWells;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    } 
    
    public int getNumberOfWellsPerQuadrant(){
        return numberOfWells/4;
    }
    
}
