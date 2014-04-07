/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.ztr.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.trace.chromat.ztr.data.FollowData;
import org.junit.BeforeClass;
import org.junit.Test;
public class TestFollowData {

    private static byte[] followBytes;
    //should have follow values [21, 1, -5, 4, -9, 17, 23]
    private byte[] uncompressedData = new byte[]{21,30,45,51,70,63,50};
    FollowData sut = FollowData.INSTANCE;
    @BeforeClass
    public static void setupFollowBytes(){
        followBytes = new byte[256];
        //for our test the follow byte is always +10
        for(int i=0; i<256; i++){
            followBytes[i] = (byte)((i+10)%256);
        }
    }
    
    @Test
    public void parse() throws IOException{
        ByteBuffer compressed = ByteBuffer.allocate(1+256+uncompressedData.length);
        compressed.put((byte)72);// use follow format
        compressed.put(followBytes);
        compressed.put(uncompressedData[0]);
        byte prev = uncompressedData[0];
        for(int i=1; i<uncompressedData.length; i++){
            compressed.put((byte)(followBytes[prev] - uncompressedData[i] ));
            prev = uncompressedData[i];
        }

       byte[] actual = sut.parseData(compressed.array());
       assertTrue(Arrays.equals(actual, uncompressedData));
    }
    
    @Test
    public void encodeAndDecode() throws IOException{
    	byte[] encodedData=sut.encodeData(uncompressedData);
    	assertArrayEquals(uncompressedData,sut.parseData(encodedData));
    }
}
