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
package org.jcvi.sequence;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.common.core.seq.read.SequenceDirection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.jcvi.common.core.seq.read.SequenceDirection.*;
@RunWith(Parameterized.class)
public class TestSequenceDirection {

    SequenceDirection direction;
    String fullString;
    String abbreviationCode;
    SequenceDirection oppositeDirection;
    
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {FORWARD, "forward", "F", REVERSE},
                {REVERSE, "reverse", "R", FORWARD},
                {NONE, "none", "N", NONE},
                {UNKNOWN, "unkown", "U", UNKNOWN},
        });
    }
    
    public TestSequenceDirection(SequenceDirection direction,
                                    String fullString,
                                    String abbreviationCode,
                                    SequenceDirection oppositeDirection){       
        
        this.direction = direction;
        this.fullString = fullString;
        this.abbreviationCode = abbreviationCode;
        this.oppositeDirection = oppositeDirection;
    }
    
    @Test
    public void getCode(){
        assertEquals(abbreviationCode, direction.getCode());
    }
    @Test
    public void oppositeDirection(){
        assertEquals(oppositeDirection, direction.oppositeOrientation());
    }
    
    @Test
    public void parseStringUppercase(){
        assertEquals(direction, SequenceDirection.parseSequenceDirection(fullString.toUpperCase()));
    }
    @Test
    public void parseStringLowercase(){
        assertEquals(direction, SequenceDirection.parseSequenceDirection(fullString.toLowerCase()));
    }
    
  
    @Test
    public void parseStringAbbreviationCodeUppercase(){
        assertEquals(direction, SequenceDirection.parseSequenceDirection(abbreviationCode.toUpperCase()));
    }
    
    @Test
    public void parseStringAbbreviationCodeLowercase(){
        assertEquals(direction, SequenceDirection.parseSequenceDirection(abbreviationCode.toLowerCase()));
    }
    @Test
    public void parseTFStringAbbreviation(){
        assertEquals(SequenceDirection.FORWARD, SequenceDirection.parseSequenceDirection("TF"));
        assertEquals(SequenceDirection.FORWARD, SequenceDirection.parseSequenceDirection("tf"));
        assertEquals(SequenceDirection.FORWARD, SequenceDirection.parseSequenceDirection("tF"));
        assertEquals(SequenceDirection.FORWARD, SequenceDirection.parseSequenceDirection("Tf"));
    }
    @Test
    public void parseTRStringAbbreviation(){
        assertEquals(SequenceDirection.REVERSE, SequenceDirection.parseSequenceDirection("TR"));
        assertEquals(SequenceDirection.REVERSE, SequenceDirection.parseSequenceDirection("tr"));
        assertEquals(SequenceDirection.REVERSE, SequenceDirection.parseSequenceDirection("tR"));
        assertEquals(SequenceDirection.REVERSE, SequenceDirection.parseSequenceDirection("Tr"));
    }
    
    @Test
    public void parseStringPositiveShouldBeForward(){
        assertEquals(SequenceDirection.FORWARD, SequenceDirection.parseSequenceDirection("+"));
    }
    @Test
    public void parseStringNegativeShouldBeReverse(){
        assertEquals(SequenceDirection.REVERSE, SequenceDirection.parseSequenceDirection("-"));
    }
}
