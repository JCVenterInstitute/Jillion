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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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


import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.trace.chromat.Channel;
import org.jcvi.jillion.trace.chromat.ChannelGroup;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
public class TestSMP4Chunk extends EasyMockSupport{

    private static short[] aTraces = new short[]{0,0,2,4,5,3,2,0,0,0,1};
    private static short[] cTraces = new short[]{7,5,2,0,1,0,2,1,1,0,1};
    private static short[] gTraces = new short[]{1,0,0,2,1,0,3,8,4,2,0};
    private static short[] tTraces = new short[]{0,0,2,4,2,3,2,0,5,8,25};
   
    Chunk sut = Chunk.SMP4;
    private static final byte[] encodedBytes;
    static{
    	ByteBuffer buf = ByteBuffer.allocate(aTraces.length *8 + 2);
        buf.putShort((short)0);
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(aTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(cTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(gTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(tTraces[i]);
        }
        encodedBytes = buf.array();
    }
    
    @Test
    public void parse() throws IOException{
        
        ZtrChromatogramBuilder struct = new ZtrChromatogramBuilder("id");

        sut.parseData(encodedBytes, struct);
        assertArrayEquals(struct.aPositions(),aTraces);
        assertArrayEquals(struct.cPositions(),cTraces);
        assertArrayEquals(struct.gPositions(),gTraces);
        assertArrayEquals(struct.tPositions(),tTraces);
    }
    
    @Test
    public void encode() throws IOException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	ChannelGroup channelGroup = createMockChannelGroup();
    	
    	expect(mockChromatogram.getNumberOfTracePositions()).andReturn(aTraces.length);
    	expect(mockChromatogram.getChannelGroup()).andReturn(channelGroup);
    	replayAll();
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedBytes, actual);
    	verifyAll();
    
    }
    
    private ChannelGroup createMockChannelGroup(){
    	ChannelGroup channelGroup = createMock(ChannelGroup.class);
    	
    	final Channel aChannel = createMock(Channel.class);
    	expect(aChannel.getPositionSequence()).andStubReturn(new PositionSequenceBuilder(aTraces).build());
    	
    	final Channel cChannel = createMock(Channel.class);
    	expect(cChannel.getPositionSequence()).andStubReturn(new PositionSequenceBuilder(cTraces).build());
    	
    	final Channel gChannel = createMock(Channel.class);
    	expect(gChannel.getPositionSequence()).andStubReturn(new PositionSequenceBuilder(gTraces).build());
    	
    	final Channel tChannel = createMock(Channel.class);
    	expect(tChannel.getPositionSequence()).andStubReturn(new PositionSequenceBuilder(tTraces).build());
    	
    	expect(channelGroup.getAChannel()).andStubReturn(aChannel);
    	expect(channelGroup.getCChannel()).andStubReturn(cChannel);
    	expect(channelGroup.getGChannel()).andStubReturn(gChannel);
    	expect(channelGroup.getTChannel()).andStubReturn(tChannel);

    	return channelGroup;
    }
}
