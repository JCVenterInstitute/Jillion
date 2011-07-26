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
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.pos;

import org.jcvi.common.core.symbol.pos.Peaks;
import org.jcvi.common.core.symbol.pos.PeaksUtil;
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
