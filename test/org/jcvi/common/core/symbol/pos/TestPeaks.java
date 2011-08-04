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
 * Created on Sep 26, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.pos;

import static org.junit.Assert.*;

import java.nio.ShortBuffer;

import org.jcvi.common.core.symbol.DefaultShortGlyphCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;
import org.jcvi.common.core.symbol.ShortGlyphFactory;
import org.jcvi.common.core.symbol.pos.Peaks;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;

public class TestPeaks {
    private static ShortGlyphFactory PEAKS_FACTORY = ShortGlyphFactory.getInstance();
    private static DefaultShortGlyphCodec PEAK_CODEC = DefaultShortGlyphCodec.getInstance();
    
    private short[] peaks = new short[]{110,120,130,140,150,160};
    private short[] differentPeaks = new short[]{30,40,50,60,70,80,90};
    private Peaks sut = new Peaks(peaks);
    private Sequence<ShortSymbol> encodedPeaks = new EncodedSequence<ShortSymbol>(PEAK_CODEC,PEAKS_FACTORY.getGlyphsFor(peaks));
    @Test
    public void constructor(){
        assertEquals(encodedPeaks, sut.getData());
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        Peaks sameValues = new  Peaks(peaks);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a Confidence"));
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentValues(){
        Peaks differentValues = new Peaks(differentPeaks);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void ShortBufferConstructor(){
        Peaks sameValues = new Peaks(ShortBuffer.wrap(peaks));
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void equalsShortBufferAtDifferentPositions(){
        final ShortBuffer buffer = ShortBuffer.wrap(peaks);
        Peaks sameValues = new Peaks(buffer);
        buffer.position(2);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

   
}
