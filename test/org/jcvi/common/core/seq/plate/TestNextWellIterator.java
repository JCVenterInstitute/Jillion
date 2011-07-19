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

package org.jcvi.common.core.seq.plate;

import java.util.NoSuchElementException;

import org.jcvi.common.core.seq.plate.NextWellIterator;
import org.jcvi.common.core.seq.plate.PlateFormat;
import org.jcvi.common.core.seq.plate.Well;
import org.jcvi.common.core.seq.plate.Well.IndexOrder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author dkatzel
 *
 *
 */
public class TestNextWellIterator {
    IndexOrder order = IndexOrder.ROW_MAJOR;
    PlateFormat plateFormat = PlateFormat._96;
    NextWellIterator sut;
    
    @Before
    public void setup(){
        sut = new NextWellIterator(order, plateFormat);
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
        new NextWellIterator(IndexOrder.COLUMN_MAJOR, null);
    }
    @Test(expected = NullPointerException.class)
    public void constructorWithNullIndexOrderShouldThrowNPE(){
        new NextWellIterator(null, PlateFormat._384);
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
