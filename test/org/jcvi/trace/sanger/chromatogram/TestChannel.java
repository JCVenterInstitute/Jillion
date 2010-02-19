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
package org.jcvi.trace.sanger.chromatogram;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import java.nio.ShortBuffer;

import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.DefaultConfidence;
import org.jcvi.testUtil.TestUtil;
import org.jcvi.trace.sanger.chromatogram.Channel;
import org.junit.Test;

public class TestChannel {

    private short[] positions = new short[]{13,14,15,18,20,15,11,4,0};
    private short[] differentPositions = new short[]{20,15,11,4,0,13,14,15,18};

    Confidence confidence = createMock(DefaultConfidence.class);
    Channel sut = new Channel(confidence, ShortBuffer.wrap(positions));

    @Test
    public void defaultConstructor(){
        Channel defaultContructor = new Channel();
        assertNull(defaultContructor.getConfidence());
        assertNull(defaultContructor.getPositions());
    }
    @Test
    public void constructor(){
        assertEquals(confidence, sut.getConfidence());
        assertArrayEquals(positions, sut.getPositions().array());
    }

    @Test
    public void arrayConstructor(){
        byte[] conf = new byte[]{20,30};
        Channel arrayContructor = new Channel(conf, positions);
        assertArrayEquals(conf, arrayContructor.getConfidence().getData());
        assertArrayEquals(positions, arrayContructor.getPositions().array());
    }

    @Test
    public void setPositions(){
        Channel channel = new Channel();
        channel.setPositions(ShortBuffer.wrap(positions));
        assertArrayEquals(positions, channel.getPositions().array());
    }

    @Test
    public void setConfidence(){
        Channel channel = new Channel();
        channel.setConfidence(confidence);
        assertEquals(confidence, channel.getConfidence());
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a channel"));
    }
    @Test
    public void equalsSameValues(){
        Channel sameValues = new Channel(confidence, ShortBuffer.wrap(positions));
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void notEqualsDifferentConfidence(){
        Confidence differentConfidence = createMock(DefaultConfidence.class);
        Channel hasDifferentConfidence = new Channel(differentConfidence, ShortBuffer.wrap(positions));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentConfidence);
    }
    @Test
    public void notEqualsNullConfidence(){
        Channel hasNullConfidence = new Channel(null, ShortBuffer.wrap(positions));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullConfidence);
    }
    @Test
    public void notEqualsDifferentPositions(){
        Channel hasDifferentPositions = new Channel(confidence, ShortBuffer.wrap(differentPositions));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentPositions);
    }
    @Test
    public void notEqualsNullPositions(){
        Channel hasNullPositions = new Channel(confidence, null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasNullPositions);
    }
}
