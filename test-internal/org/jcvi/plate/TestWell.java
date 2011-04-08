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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestWell {

    private Well expected;
    private Well actual;
    private int expectedIndex;
    private int actualIndex;
    
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        //a few selected wells of each type for fail fast
        data.add(new Object[]{ 
                Well.create("A01"),
                Well.create("A01"),
                0,
                Well.create("A01").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("B01"),
                Well.create("B01"),
                1,
                Well.create("B01").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("A02"),
                Well.create("A02"),
                16,
                Well.create("A02").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("B03"),
                Well.create("B03"),
                33,
                Well.create("B03").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("A01"),
                Well.compute384Well(384, Well.IndexOrder.COLUMN_MAJOR),
                0,
                Well.create("A01").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("B01"),
                Well.compute384Well(384+1, Well.IndexOrder.COLUMN_MAJOR),
                1,
                Well.create("B01").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("A02"),
                Well.compute384Well(384+16, Well.IndexOrder.COLUMN_MAJOR),
                16,
                Well.create("A02").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("B03"),
                Well.compute384Well(384+33, Well.IndexOrder.COLUMN_MAJOR),
                33,
                Well.create("B03").get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        
        
        data.add(new Object[]{ 
                Well.create("A01"),
                Well.compute96Well(0, Well.IndexOrder.COLUMN_MAJOR),
                0,
                Well.create("A01").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("B01"),
                Well.compute96Well(1, Well.IndexOrder.COLUMN_MAJOR),
                1,
                Well.create("B01").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("A02"),
                Well.compute96Well(8, Well.IndexOrder.COLUMN_MAJOR),
                8,
                Well.create("A02").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("B03"),
                Well.compute96Well(17, Well.IndexOrder.COLUMN_MAJOR),
                17,
                Well.create("B03").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("A01"),
                Well.compute96Well(96, Well.IndexOrder.COLUMN_MAJOR),
                0,
                Well.create("A01").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("B01"),
                Well.create("B01"),
                1,
                Well.create("B01").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        data.add(new Object[]{ 
                Well.create("A02"),
                Well.compute96Well(96+8, Well.IndexOrder.COLUMN_MAJOR),
                8,
                Well.create("A02").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        data.add(new Object[]{ 
                Well.create("B03"),
                Well.compute96Well(96+17, Well.IndexOrder.COLUMN_MAJOR),
                17,
                Well.create("B03").get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        
        
        //384 wells
        for(int i=0; i< 384; i++){
            //zero padded
            final String zeroPaddedName = String.format("%s%02d", (char)('A'+(i/24)%16),i%24 +1 );
            final String unPaddedName = String.format("%s%d", (char)('A'+(i/24)%16),i%24 +1 );
            
            final Well actualWell = Well.compute384Well(i, Well.IndexOrder.ROW_MAJOR);
            final Well actualRollOverWell = Well.compute384Well(i+(384*5),Well.IndexOrder.ROW_MAJOR);
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell,
                    i,
                    actualWell.get384WellIndex()});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualWell,
                    i,
                    actualWell.get384WellIndex()});
            //roll over
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualRollOverWell,
                    i,
                    actualWell.get384WellIndex()});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualRollOverWell,
                    i,
                    actualWell.get384WellIndex()});            
        }
        
        //96 well
        for(int i=0; i< 96; i++){
            //zero padded
            final String zeroPaddedName = String.format("%s%02d", (char)('A'+(i/12)%8),i%12 +1 );
            final String unPaddedName = String.format("%s%d", (char)('A'+(i/12)%8),i%12 +1 );
            
            final Well actualWell = Well.compute96Well(i,Well.IndexOrder.ROW_MAJOR);
            final Well actualRollOverWell = Well.compute96Well(i+(96*5),Well.IndexOrder.ROW_MAJOR);
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell,
                    i,
                    actualWell.get96WellIndex()});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualWell,
                    i,
                    actualWell.get96WellIndex()});
            //roll over
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualRollOverWell,
                    i,
                    actualWell.get96WellIndex()});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualRollOverWell,
                    i,
                    actualWell.get96WellIndex()});
                    
        }
        
        //column order
        for(int i=0; i<384; i++){
            final String zeroPaddedName = String.format("%s%02d", 
                    (char)('A'+i%16),i/16 +1);
            final Well actualWell = Well.compute384Well(i, Well.IndexOrder.COLUMN_MAJOR);
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell,
                    i,
                    actualWell.get384WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        }
        for(int i=0; i<96; i++){
            final String zeroPaddedName = String.format("%s%02d", 
                    (char)('A'+i%8),i/8 +1);
            final Well actualWell = Well.compute96Well(i, Well.IndexOrder.COLUMN_MAJOR);
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell,
                    i,
                    actualWell.get96WellIndex(Well.IndexOrder.COLUMN_MAJOR)});
        }
        
        
        //3730 16 cap 96 well
        int index=-1;
        for(int capIndex=0; capIndex<6; capIndex++){
            for(int row=0; row<8; row++){
                for(int col=0; col<2; col++){
                    index++;
                    
                    final String zeroPaddedName = String.format("%s%02d", 
                            (char)('A'+row),2*capIndex + col+1);
                    
                    final Well actualWell = Well.compute96Well(index, Well.IndexOrder.ABI_3130_16_CAPILLARIES);
                    data.add(new Object[]{ 
                            Well.create(zeroPaddedName),
                            actualWell,
                            index,
                            actualWell.get96WellIndex(Well.IndexOrder.ABI_3130_16_CAPILLARIES)});
                }
            }
        }
        
        return data;
    }

    /**
     * @param expected
     * @param actual
     */
    public TestWell(Well expected, Well actual, int expectedIndex, int actualIndex) {
        this.expected = expected;
        this.actual = actual;
        this.expectedIndex = expectedIndex;
        this.actualIndex = actualIndex;
    }
    
    @Test
    public void computeWellMatchesParsedString(){
        
        assertEquals("well",expected, actual);
        assertEquals("index "+ expected,expectedIndex, actualIndex);
    }
    
}
