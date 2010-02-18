/*
 * Created on Dec 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.cli;

import org.apache.commons.cli.Option;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCommandLineOptionBuilder {

    private final String shortName = "shortName";
    private final String longName = "longName";
    private final  String displayName = "displayName";
    private final  String description = "description";
    
    @Test
    public void fullConstructor(){
        Option option = new CommandLineOptionBuilder(shortName, displayName,description).build();
        
        assertEquals(shortName, option.getOpt());
        assertEquals(displayName, option.getArgName());
        assertEquals(description, option.getDescription());
        assertFalse(option.isRequired());
        assertTrue(option.hasArg());
        assertNull(option.getLongOpt());
    }
    @Test
    public void DisplayNameIsShortNameIfNotSpecified(){
        Option option = new CommandLineOptionBuilder(shortName, description).build();
        
        assertEquals(shortName, option.getOpt());
        assertEquals(shortName, option.getArgName());
        assertEquals(description, option.getDescription());
        assertFalse(option.isRequired());
        assertTrue(option.hasArg());
        assertNull(option.getLongOpt());
    }
    @Test
    public void hasLongName(){
        Option option = new CommandLineOptionBuilder(shortName, description).longName(longName).build();
        
        assertEquals(shortName, option.getOpt());
        assertEquals(shortName, option.getArgName());
        assertEquals(description, option.getDescription());
        assertFalse(option.isRequired());
        assertTrue(option.hasArg());
        assertEquals(longName,option.getLongOpt());
    }
    @Test
    public void isFlag(){
        Option option = new CommandLineOptionBuilder(shortName, description)
        .isFlag(true)
        .build();
        assertFalse(option.hasArg());
    }
    @Test
    public void isRequired(){
        Option option = new CommandLineOptionBuilder(shortName, description)
        .isRequired(true)
        .build();
        assertTrue(option.isRequired());
    }
}
