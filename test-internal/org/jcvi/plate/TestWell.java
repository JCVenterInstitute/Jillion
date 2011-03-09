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
    
    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        //384 wells
        for(int i=0; i< 384; i++){
            //zero padded
            final String zeroPaddedName = String.format("%s%02d", (char)('A'+(i/24)%16),i%24 +1 );
            final String unPaddedName = String.format("%s%d", (char)('A'+(i/24)%16),i%24 +1 );
            
            final Well actualWell = Well.compute384Well(i);
            final Well actualRollOverWell = Well.compute384Well(i+(384*5));
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualWell});
            //roll over
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualRollOverWell});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualRollOverWell});
        }
        
        //96 well
        for(int i=0; i< 96; i++){
            //zero padded
            final String zeroPaddedName = String.format("%s%02d", (char)('A'+(i/12)%8),i%12 +1 );
            final String unPaddedName = String.format("%s%d", (char)('A'+(i/12)%8),i%12 +1 );
            
            final Well actualWell = Well.compute96Well(i);
            final Well actualRollOverWell = Well.compute96Well(i+(96*5));
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualWell});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualWell});
            //roll over
            data.add(new Object[]{ 
                    Well.create(zeroPaddedName),
                    actualRollOverWell});
            data.add(new Object[]{ 
                    Well.create(unPaddedName),
                    actualRollOverWell});
                    
        }
        return data;
    }

    /**
     * @param expected
     * @param actual
     */
    public TestWell(Well expected, Well actual) {
        this.expected = expected;
        this.actual = actual;
    }
    
    @Test
    public void computeWellMatchesParsedString(){
        assertEquals(expected, actual);
    }
}
