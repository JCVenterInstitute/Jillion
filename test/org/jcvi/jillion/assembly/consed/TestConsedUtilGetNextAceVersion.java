/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed;

import java.io.File;

import org.jcvi.jillion.assembly.consed.ConsedUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtilGetNextAceVersion {

    
    @Test
    public void version1ShouldMakeVersion2(){
        File version1 = new File("consed.ace.1");
        assertEquals("consed.ace.2", ConsedUtil.generateNextAceVersionNameFor(version1));
    }
    @Test
    public void version2ShouldMakeVersion3(){
        File version2 = new File("consed.ace.2");
        assertEquals("consed.ace.3", ConsedUtil.generateNextAceVersionNameFor(version2));
    }
    
    @Test
    public void twoDigitVersion(){
        File version28 = new File("consed.ace.28");
        assertEquals("consed.ace.29", ConsedUtil.generateNextAceVersionNameFor(version28));
    }
    @Test(expected= IllegalArgumentException.class)
    public void noVersionShouldThrowIllegalArgumentException(){
        File noVersion = new File("consed.ace");
        ConsedUtil.generateNextAceVersionNameFor(noVersion);
    }
    
    @Test
    public void doubleSuffix(){
        File doubleSuffix = new File("consed.ace.1.ace.5");
        assertEquals("consed.ace.1.ace.6", ConsedUtil.generateNextAceVersionNameFor(doubleSuffix));
    }
    @Test
    public void noPrefix(){
        File noPrefix = new File("ace.5");
        assertEquals("ace.6", ConsedUtil.generateNextAceVersionNameFor(noPrefix));
    }
}
