/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.trace.fastq.IlluminaUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestIlluminaUtil {

    private static final String ID = "SOLEXA1:4:1:12:1489#0/1";
    private static final String CASAVA_ID = "SOLEXA3_0023_FC:4:8:17653:4072#TAGCTT/1";
    private static final String NEW_ID ="SIM:1:FCX:1:15:6329:1045:GATTACT+GTCTTAAC 1:N:0:ATCCGA";
    
    @Test
    public void instrumentName(){
        assertEquals("SOLEXA1", IlluminaUtil.getInstrumentName(ID));
        assertEquals("SOLEXA3", IlluminaUtil.getInstrumentName(CASAVA_ID));
        assertEquals("SIM", IlluminaUtil.getInstrumentName(NEW_ID));
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
        assertEquals("4", IlluminaUtil.getFlowcellId(ID));
        assertEquals("4", IlluminaUtil.getFlowcellId(CASAVA_ID));
        assertEquals("FCX", IlluminaUtil.getFlowcellId(NEW_ID));
    }
    
    @Test(expected= NullPointerException.class)
    public void nullIdForFlowCellShouldThrowNPE(){
        IlluminaUtil.getFlowcellId(null);
    }
    @Test(expected= IllegalArgumentException.class)
    public void invalidIdForFlowCellShouldThrowNPE(){
        IlluminaUtil.getFlowcellId("not an illumina id");
    }
    @Test
    public void xClusterCoordinate(){
        assertEquals(12, IlluminaUtil.getXClusterCoordinate(ID));
        assertEquals(17653, IlluminaUtil.getXClusterCoordinate(CASAVA_ID));
        assertEquals(6329, IlluminaUtil.getXClusterCoordinate(NEW_ID));
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
        assertEquals(4072, IlluminaUtil.getYClusterCoordinate(CASAVA_ID));
        assertEquals(1045, IlluminaUtil.getYClusterCoordinate(NEW_ID));
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
        assertEquals(1, IlluminaUtil.getPairNumber(CASAVA_ID));
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
        assertEquals(8, IlluminaUtil.getTileNumber(CASAVA_ID));
        assertEquals(15, IlluminaUtil.getTileNumber(NEW_ID));
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
