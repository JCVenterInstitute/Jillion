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

package org.jcvi.fastX.fastq;

import org.jcvi.fastX.fastq.IlluminaUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestIlluminaUtil {

    private static final String ID = "SOLEXA1:4:1:12:1489#0/1";
    private static String NEW_ID = "SOLEXA3_0023_FC:4:8:17653:4072#TAGCTT/1";
    @Test
    public void instrumentName(){
        assertEquals("SOLEXA1", IlluminaUtil.getInstrumentName(ID));
        assertEquals("SOLEXA3", IlluminaUtil.getInstrumentName(NEW_ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForInsrumentNameShouldThrowNPE(){
        IlluminaUtil.getInstrumentName(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForInsrumentNameShouldThrowNPE(){
        IlluminaUtil.getInstrumentName("not an illumina id");
    }
    @Test
    public void flowCell(){
        assertEquals(4, IlluminaUtil.getFlowcellLane(ID));
        assertEquals(4, IlluminaUtil.getFlowcellLane(NEW_ID));
    }
    
    @Test(expected= NullPointerException.class)
    public void nullIdForFlowCellShouldThrowNPE(){
        IlluminaUtil.getFlowcellLane(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForFlowCellShouldThrowNPE(){
        IlluminaUtil.getFlowcellLane("not an illumina id");
    }
    @Test
    public void xClusterCoordinate(){
        assertEquals(12, IlluminaUtil.getXClusterCoordinate(ID));
        assertEquals(17653, IlluminaUtil.getXClusterCoordinate(NEW_ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForXClusterCoordinateShouldThrowNPE(){
        IlluminaUtil.getXClusterCoordinate(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForXClusterCoordinateShouldThrowNPE(){
        IlluminaUtil.getXClusterCoordinate("not an illumina id");
    }
    
    @Test
    public void yClusterCoordinate(){
        assertEquals(1489, IlluminaUtil.getYClusterCoordinate(ID));
        assertEquals(4072, IlluminaUtil.getYClusterCoordinate(NEW_ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForYClusterCoordinateShouldThrowNPE(){
        IlluminaUtil.getYClusterCoordinate(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForYClusterCoordinateShouldThrowNPE(){
        IlluminaUtil.getYClusterCoordinate("not an illumina id");
    }
    @Test
    public void multiplexIndex(){
        assertEquals(0, IlluminaUtil.getMultiplexIndex(ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForMultiplexIndexShouldThrowNPE(){
        IlluminaUtil.getMultiplexIndex(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForMultiplexIndexShouldThrowNPE(){
        IlluminaUtil.getMultiplexIndex("not an illumina id");
    }
    @Test
    public void pairNumber(){
        assertEquals(1, IlluminaUtil.getPairNumber(ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForPairNumberShouldThrowNPE(){
        IlluminaUtil.getPairNumber(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForPairNumberShouldThrowNPE(){
        IlluminaUtil.getPairNumber("not an illumina id");
    }
    
    @Test
    public void tileNumber(){
        assertEquals(1, IlluminaUtil.getTileNumber(ID));
    }
    @Test(expected= NullPointerException.class)
    public void nullIdForTileNumberShouldThrowNPE(){
        IlluminaUtil.getTileNumber(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForTileNumberShouldThrowNPE(){
        IlluminaUtil.getTileNumber("not an illumina id");
    }
}
