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
public class TestFakeTigrSeqnameMatedComputeLibraryLetter {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=0; i<10; i++){
            data.add(new Object[]{i, (""+i).charAt(0)});
        }
        for(int i=0; i<26; i++){
            data.add(new Object[]{i+10, (char)(i+'A')});
        }
        data.add(new Object[]{10, 'A'});
        data.add(new Object[]{20, 'K'});
        return data;
    }
    
    private int position;
    private char expectedLetter;
    
    public TestFakeTigrSeqnameMatedComputeLibraryLetter(int position, char expectedLetter){
        this.position = position;
        this.expectedLetter = expectedLetter;
    }
    @Test
    public void computeLibraryLetter(){
        assertEquals(expectedLetter,
                FakeTigrSeqnameMatedTraceIdGenerator.computeLibraryLetterFrom(position));
    }
    @Test
    public void computeLibraryPosition(){
        assertEquals(position,
                FakeTigrSeqnameMatedTraceIdGenerator.computeLibraryPositionFrom(expectedLetter));
    }
}
