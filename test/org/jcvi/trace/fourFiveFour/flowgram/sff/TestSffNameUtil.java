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

package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSffNameUtil {

    @Test
    public void isSffRead(){
        assertTrue(SffNameUtil.is454Read( "C3U5GWL01CBXT2"));
        assertFalse(SffNameUtil.is454Read("IVAAA01T48C03PB1234F"));
        assertFalse(SffNameUtil.is454Read("IVAAA01T48HA2F"));
        //now check mated reads
        assertTrue("clc split mate",SffNameUtil.is454Read("F3P0QKL01AMPVE_1-93"));
    }
    
    @Test
    public void getUniveralAccessionNumberFrom(){
        assertEquals("C3U5GWL01CBXT2", SffNameUtil.parseUniversalAccessionNumberFrom("C3U5GWL01CBXT2"));
        assertEquals("clc split mate",
                "F3P0QKL01AMPVE", SffNameUtil.parseUniversalAccessionNumberFrom("F3P0QKL01AMPVE_1-93"));
    }
    
    @Test
    public void parseDateFromName(){
        DateTime expectedDate = new DateTime(2004, 9, 22, 16, 59, 10, 0);
        assertEquals(expectedDate.toDate(), SffNameUtil.getDateOfRun("C3U5GWL01CBXT2"));
    }
    
   
    
    @Test
    public void parseLocation(){
        assertEquals(new SffNameUtil.Location(838,3960), SffNameUtil.parseLocationOf("C3U5GWL01CBXT2"));
    }
    
    @Test
    public void generateAccessionName(){
        //example from 454 Data Analysis Software Manual page 533
        String rigRunName = "R_2006_10_10_20_18_48_build04_adminrig_100x7075SEQ082806BHTF960397NewBeadDep2Region4EXP106";
        assertEquals("EBO6PME01EE3WX", SffNameUtil.generateAccessionNumberFor(rigRunName, 1, new SffNameUtil.Location(1695,767)));
    }
    
    
}
