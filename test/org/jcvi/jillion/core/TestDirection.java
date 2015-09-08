/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.jillion.core.Direction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.jcvi.jillion.core.Direction.*;
@RunWith(Parameterized.class)
public class TestDirection {

    Direction direction;
    String fullString;
    String abbreviationCode;
    Direction oppositeDirection;
    
    @Parameters
    public static Collection<?> data(){

        return Arrays.asList(new Object[][]{
                {FORWARD, "forward", "F", REVERSE},
                {REVERSE, "reverse", "R", FORWARD}
        });
    }
    
    public TestDirection(Direction direction,
                                    String fullString,
                                    String abbreviationCode,
                                    Direction oppositeDirection){       
        
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
        assertEquals(direction, Direction.parseSequenceDirection(fullString.toUpperCase()));
    }
    @Test
    public void parseStringLowercase(){
        assertEquals(direction, Direction.parseSequenceDirection(fullString.toLowerCase()));
    }
    
  
    @Test
    public void parseStringAbbreviationCodeUppercase(){
        assertEquals(direction, Direction.parseSequenceDirection(abbreviationCode.toUpperCase()));
    }
    
    @Test
    public void parseStringAbbreviationCodeLowercase(){
        assertEquals(direction, Direction.parseSequenceDirection(abbreviationCode.toLowerCase()));
    }
    @Test
    public void parseTFStringAbbreviation(){
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("TF"));
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("tf"));
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("tF"));
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("Tf"));
    }
    @Test
    public void parseTRStringAbbreviation(){
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("TR"));
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("tr"));
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("tR"));
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("Tr"));
    }
    
    @Test
    public void parseStringPositiveShouldBeForward(){
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("+"));
    }
    @Test
    public void parseStringNegativeShouldBeReverse(){
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("-"));
    }
    @Test
    public void parseStringOneShouldBeReverse(){
        assertEquals(Direction.REVERSE, Direction.parseSequenceDirection("1"));
    }
    @Test
    public void parseStringZeroShouldBeForward(){
        assertEquals(Direction.FORWARD, Direction.parseSequenceDirection("0"));
    }
}
