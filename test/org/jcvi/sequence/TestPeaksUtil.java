/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestPeaksUtil {

    @Test
    public void generateEmptyPeaks(){
        Peaks emptyPeaks = PeaksUtil.generateFakePeaks(0);
        assertEquals(emptyPeaks.getData().getLength(),0L);
    }
    @Test(expected = IllegalArgumentException.class)
    public void geneateFakePeaksNegativeShouldThrowIllegalArgumentException(){
        PeaksUtil.generateFakePeaks(-1);
    }
    @Test
    public void generateFakePeaks(){
        short[] expected = new short[]{5,15,25,35,45};
        Peaks actualpeaks = PeaksUtil.generateFakePeaks(expected.length);
        
        assertEquals(new Peaks(expected), actualpeaks);
    }
}
