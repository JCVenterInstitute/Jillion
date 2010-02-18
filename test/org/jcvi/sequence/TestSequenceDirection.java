package org.jcvi.sequence;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
import static org.jcvi.sequence.SequenceDirection.*;
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
