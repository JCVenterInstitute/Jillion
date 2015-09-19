/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.experimental.plate;

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
