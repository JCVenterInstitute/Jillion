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

import java.util.NoSuchElementException;

import org.jcvi.jillion.experimental.plate.Well.IndexOrder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestPlatePopulator {
    IndexOrder order = IndexOrder.ROW_MAJOR;
    PlateFormat plateFormat = PlateFormat._96;
    PlatePopulator sut;
    
    @Before
    public void setup(){
        sut = new PlatePopulator(order, plateFormat);
    }
    @Test
    public void nextWellShouldGetNextWellFromIndex(){
        Well expected = Well.compute96Well(0, order);
        assertFalse(sut.isFull());
        assertEquals(expected, sut.nextUnusedWell());
    }
    @Test
    public void useWell(){
        Well first = Well.compute96Well(0, order);
        Well second = Well.compute96Well(1, order);
        sut.isUnused(first);
        sut.use(first);        
        assertEquals(second, sut.nextUnusedWell());
    }
    @Test
    public void useUpFullPlateShouldMakeIsFullTrue(){
        fillUpPlate();
        assertTrue(sut.isFull());
    }
    
    private void fillUpPlate(){
        for(int i=0; i<plateFormat.getNumberOfWells(); i++){
            assertFalse(sut.isFull());
            Well well = sut.nextUnusedWell();
            sut.use(well);
        }
    }
    
    @Test
    public void filledUpPlateWillThrowNoSuchExceptionIfCallNext(){
        fillUpPlate();
        try{
            sut.nextUnusedWell();
            fail("should throw NoSuchElementException if plate is full");
        }catch(NoSuchElementException e){
            //pass
        }
    }
    
    @Test
    public void newPlateShouldClearOutWhatHasBeenUsed(){
        Well first = Well.compute96Well(0, order);
        sut.use(first);
        sut.newPlate();
        assertEquals(first, sut.nextUnusedWell());
    }
    @Test(expected = NullPointerException.class)
    public void constructorWithNullPlateFormatShouldThrowNPE(){
        new PlatePopulator(IndexOrder.COLUMN_MAJOR, null);
    }
    @Test(expected = NullPointerException.class)
    public void constructorWithNullIndexOrderShouldThrowNPE(){
        new PlatePopulator(null, PlateFormat._384);
    }
    
    @Test(expected = NullPointerException.class)
    public void useWithNullWellShouldThrowNPE(){
        sut.use(null);
    }
    
    @Test
    public void tryingToReuseWellShouldThrowIllegalArgumentException(){
        Well well = Well.compute96Well(0, order);
        sut.use(well);
        try{
            sut.use(well);
            fail("should throw IllegalArgumentException when re-using well");
        }catch(IllegalArgumentException e){
            //expected
        }
    }
}
