/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.sanger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.trace.sanger.DefaultPositionCodec;
import org.jcvi.jillion.trace.sanger.Position;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDefaultPositionCodec {
   
    private static final short[] shorts = new short[]{12345,10,0,Short.MAX_VALUE, 60,99,256,5000};

    private static final List<Position> decodedGlyphs;
    
    
    
    private static byte[] encodedShortsAsByteArray;
    
    static{
    	decodedGlyphs = new ArrayList<Position>(shorts.length);
    	for(int i=0; i<shorts.length; i++){
    		decodedGlyphs.add(Position.valueOf(shorts[i]));
    	}
    }
    
    @BeforeClass
    public static void createByteArray(){
        ByteBuffer buf = ByteBuffer.allocate(shorts.length *2);
        for(int i=0; i<shorts.length; i++){
            buf.putShort(shorts[i]);
        }
        encodedShortsAsByteArray = buf.array();
    }
    
    DefaultPositionCodec sut = DefaultPositionCodec.INSTANCE;
    
    @Test
    public void encode(){
        byte[] actualEncodedBytes =sut.encode(decodedGlyphs);
        assertArrayEquals(encodedShortsAsByteArray, actualEncodedBytes);
    }
    
    @Test
    public void length(){
        assertEquals(shorts.length, sut.decodedLengthOf(encodedShortsAsByteArray));
    }
    
    @Test
    public void indexedDecode(){
        for(int i=0; i<shorts.length; i++){
            assertEquals( decodedGlyphs.get(i), sut.decode(encodedShortsAsByteArray, i));
        }
    }
    
    @Test
    public void encodeValuesUptoUnsignedShortMax(){
    	List<Position> positions = Arrays.asList(
    						Position.valueOf(1234),
    						Position.valueOf(Short.MAX_VALUE),
    						Position.valueOf(Short.MAX_VALUE+1),
    						Position.valueOf(2*Short.MAX_VALUE - 1));
    	
    	byte[] actual = sut.encode(positions);
    	for(int i=0; i<positions.size(); i++){
            assertEquals( positions.get(i), sut.decode(actual, i));
        }
    }
}
