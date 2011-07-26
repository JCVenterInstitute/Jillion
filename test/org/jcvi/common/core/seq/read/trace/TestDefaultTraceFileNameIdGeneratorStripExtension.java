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
 * Created on Sep 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace;

import org.jcvi.common.core.seq.read.trace.DefaultTraceFileNameIdGenerator;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultTraceFileNameIdGeneratorStripExtension {

    DefaultTraceFileNameIdGenerator sut = new DefaultTraceFileNameIdGenerator(true);
    
    @Test
    public void noExtension(){
        String fileName = "fileName";
        assertEquals(fileName, sut.generateIdFor(fileName));
    }
    @Test
    public void singleExt(){
        String fileName = "fileName.ztr";
        assertEquals("fileName", sut.generateIdFor(fileName));
    }
    @Test
    public void multExtShouldOnlyStripLast(){
        String fileName = "fileName.tar.gz";
        assertEquals("fileName.tar", sut.generateIdFor(fileName));
    }
    @Test(expected = IllegalArgumentException.class)
    public void onlyHasOnePeriodAtStartShouldThrowIllegalArgumentException(){
        String fileName = ".uhoh";
        sut.generateIdFor(fileName);
    }
    @Test
    public void hasPeriodAtBeginningAndElsewhere(){
        String fileName =".this.ok";
        assertEquals(".this", sut.generateIdFor(fileName));
    }
}
