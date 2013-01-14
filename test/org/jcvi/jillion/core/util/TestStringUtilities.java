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
package org.jcvi.jillion.core.util;

import static org.junit.Assert.*;

import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.junit.Test;

public class TestStringUtilities {

  
    
    @Test
    public void joinBuilderStringsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new JoinedStringBuilder("Larry","Moe","Curly").build());
    }
    
    @Test
    public void joinBuilderNoElements(){
        assertEquals("",
                new JoinedStringBuilder().build());
    }
    
    @Test
    public void joinBuilderStringsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new JoinedStringBuilder("Larry","Moe","Curly")
                        .glue(",")
                        .build());
    }
    
    @Test
    public void joinBuilderObjectsWithNoGlue(){
        assertEquals("LarryMoeCurly",
                new JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"}).build());
    }
    
    @Test
    public void joinBuilderObjectsWithGlue(){
        assertEquals("Larry,Moe,Curly",
                new JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithPrefix(){
        assertEquals("Stooges=Larry,Moe,Curly",
                new JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .prefix("Stooges=")
                        .build());
    }
    @Test
    public void joinBuilderObjectsWithSuffix(){
        assertEquals("Larry,Moe,Curly were the best stooges",
                new JoinedStringBuilder(new Object[]{"Larry","Moe","Curly"})
                        .glue(",")
                        .suffix(" were the best stooges")
                        .build());
    }
    

}
