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
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.internal;

import java.util.List;

import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.internal.TigrQualitiesEncodedGyphCodec;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTigrQualitiesEncoderCodec {

    byte[] qualitiesAsBytes = new byte[]{10,20,30,40,50,60,23,55};
    List<PhredQuality> qualities = PhredQuality.valueOf(qualitiesAsBytes);
    String encodedQualities = ":DNXblGg";
    
    TigrQualitiesEncodedGyphCodec sut = TigrQualitiesEncodedGyphCodec.getINSTANCE();
    
    @Test
    public void encode(){
        assertEquals(encodedQualities, new String(sut.encode(qualities)));
    }
    
    @Test
    public void decode(){
        assertEquals(qualities, sut.decode(encodedQualities.getBytes()));
    }
    @Test
    public void length(){
        assertEquals(qualitiesAsBytes.length, sut.decodedLengthOf(encodedQualities.getBytes()));
    }
    @Test
    public void indexedDecode(){
        byte[] encodedBytes = encodedQualities.getBytes();
        for(int i=0; i<encodedQualities.length(); i++){
            assertEquals(qualities.get(i), sut.decode(encodedBytes, i));
        }
    }
}
