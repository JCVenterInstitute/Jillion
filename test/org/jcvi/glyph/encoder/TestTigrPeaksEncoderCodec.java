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
 * Created on Sep 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.testUtil.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrPeaksEncoderCodec {

    private static final short[] PEAKS_AS_SHORTS = new short[]{8,15,24,36,49,60};
    
    private static byte[] EXPECTED_ENCODED_PEAKS;
    @BeforeClass
    public static void setup() throws UnsupportedEncodingException{
        EXPECTED_ENCODED_PEAKS =TigrPeaksEncoder.encode(PEAKS_AS_SHORTS).getBytes("US-ASCII");
    }
    
    TigrPeaksEncoderGlyphCodec sut = TigrPeaksEncoderGlyphCodec.getInstance();
    
    List<ShortGlyph> peaks = ShortGlyphFactory.getInstance().getGlyphsFor(
                            PEAKS_AS_SHORTS);
    
    @Test
    public void encode(){
        byte[] actual =sut.encode(peaks);
        assertArrayEquals(EXPECTED_ENCODED_PEAKS, actual);
    }
    
    @Test
    public void decode(){
        assertEquals(peaks, sut.decode(EXPECTED_ENCODED_PEAKS));
    }
    
    @Test
    public void length(){
        assertEquals(peaks.size(), sut.decodedLengthOf(EXPECTED_ENCODED_PEAKS));
    }
    
    @Test
    public void decodeIndex(){
        for(int i=0; i< peaks.size(); i++){
            ShortGlyph actual =sut.decode(EXPECTED_ENCODED_PEAKS, i);
            assertEquals(peaks.get(i), actual);
        }
    }
}
