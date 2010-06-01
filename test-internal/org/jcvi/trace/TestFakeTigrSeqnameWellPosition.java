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
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestFakeTigrSeqnameWellPosition {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        int counter = 0;
        for(int i=0; i<26; i++){
            char row =(char)(i+'A');
            for(int column= 0; column<100; column++){
                data.add(new Object[]{counter++, String.format("%s%02d",row, column)});
            }
        }
        return data;
    }
    
    private int position;
    private String expectedWell;
    
    public TestFakeTigrSeqnameWellPosition(int position, String well){
        this.position = position;
        this.expectedWell = well;
    }
    
    @Test
    public void computeWellPosition(){
        assertEquals(expectedWell, FakeTigrSeqnameMatedTraceIdGenerator.computeWellPositionFrom(position));
    }
}
