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

import java.nio.ShortBuffer;

public final class PeaksUtil {

    private PeaksUtil(){}
    /**
     * Generate Fake Peak Data for a given number of Bases.
     * @param numberOfPeaks the number of peaks to fake.
     * @return a {@link Peaks}.
     * @throws IllegalArgumentException if {@code numberOfPeaks < 0 }
     */
    public static Peaks generateFakePeaks(int numberOfPeaks){
        ShortBuffer buf = ShortBuffer.allocate(numberOfPeaks);
        for(int i=0; i<buf.capacity(); i++){
            buf.put((short)(i*10 +5));
        }
        return new Peaks(buf);
    }
}
