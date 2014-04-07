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
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestBPOSChunk {

    private static final short[] decodedPeaks = new short[]{10,20,30,41,53,60,68};
    Chunk sut =Chunk.POSITIONS;

    private static final byte[] encodedPositions;
    static{
    	ByteBuffer buf = ByteBuffer.allocate(decodedPeaks.length*4 + 4);
        buf.putInt(0); //padding
        for(int i=0; i< decodedPeaks.length; i++){
            buf.putInt(decodedPeaks[i]);
        }
        encodedPositions = buf.array();
    }
    @Test
    public void valid() throws IOException{        
        ZtrChromatogramBuilder mockStruct = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedPositions, mockStruct);
        assertEquals(new PositionSequenceBuilder(decodedPeaks).build(), mockStruct.peaks());
    }
    
    @Test
    public void encode() throws IOException{
    	ZtrChromatogram chromatogram = createMock(ZtrChromatogram.class);
    	expect(chromatogram.getPeakSequence()).andReturn(new PositionSequenceBuilder(decodedPeaks).build());
    	replay(chromatogram);
    	byte[] actual =sut.encodeChunk(chromatogram);
    	assertArrayEquals(encodedPositions, actual);
    	verify(chromatogram);
    }
}
