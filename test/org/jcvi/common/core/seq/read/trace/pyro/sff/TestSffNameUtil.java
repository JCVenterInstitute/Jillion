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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.seq.read.trace.pyro.sff.Sff454NameUtil;
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
        assertTrue(Sff454NameUtil.is454Read( "C3U5GWL01CBXT2"));
        assertFalse(Sff454NameUtil.is454Read("IVAAA01T48C03PB1234F"));
        assertFalse(Sff454NameUtil.is454Read("IVAAA01T48HA2F"));
        //now check mated reads
        assertTrue("clc split mate",Sff454NameUtil.is454Read("F3P0QKL01AMPVE_1-93"));
        assertTrue("newbler split mate",Sff454NameUtil.is454Read("ERESL0I01CLM9Q_left"));
        assertTrue("sffToCA split mate",Sff454NameUtil.is454Read("ERESL0I01EGOIMb"));
    }
    
    @Test
    public void getUniveralAccessionNumberFrom(){
        assertEquals("C3U5GWL01CBXT2", Sff454NameUtil.parseUniversalAccessionNumberFrom("C3U5GWL01CBXT2"));
        assertEquals("clc split mate",
                "F3P0QKL01AMPVE", Sff454NameUtil.parseUniversalAccessionNumberFrom("F3P0QKL01AMPVE_1-93"));
        
        assertEquals("newbler split mate",
                "ERESL0I01CLM9Q", Sff454NameUtil.parseUniversalAccessionNumberFrom("ERESL0I01CLM9Q_left"));
        assertEquals("sffToCA split mate",
                "ERESL0I01EGOIM", Sff454NameUtil.parseUniversalAccessionNumberFrom("ERESL0I01EGOIMb"));
    }
    @Test
    public void getRegionNumber(){
        assertEquals(1, Sff454NameUtil.getRegionNumber("C3U5GWL01CBXT2"));
        assertEquals(2, Sff454NameUtil.getRegionNumber("F3P0QKL02AMPVE_1-93"));
        
        assertEquals(12, Sff454NameUtil.getRegionNumber("ERESL0I12CLM9Q_left"));
 
    }
    @Test
    public void parseDateFromName(){
        DateTime expectedDate = new DateTime(2004, 9, 22, 16, 59, 10, 0);
        assertEquals(expectedDate.toDate(), Sff454NameUtil.getDateOfRun("C3U5GWL01CBXT2"));
    }
    
   
    
    @Test
    public void parseLocation(){
        assertEquals(new Sff454NameUtil.Location(838,3960), Sff454NameUtil.parseLocationOf("C3U5GWL01CBXT2"));
    }
    
    @Test
    public void generateAccessionName(){
        //example from 454 Data Analysis Software Manual page 533
        String rigRunName = "R_2006_10_10_20_18_48_build04_adminrig_100x7075SEQ082806BHTF960397NewBeadDep2Region4EXP106";
        assertEquals("EBO6PME01EE3WX", Sff454NameUtil.generateAccessionNumberFor(rigRunName, 1, new Sff454NameUtil.Location(1695,767)));
    }
    
    
}
