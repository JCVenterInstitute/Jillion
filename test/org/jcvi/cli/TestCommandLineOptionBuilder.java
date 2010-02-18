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
