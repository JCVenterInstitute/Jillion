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
package org.jcvi.sequence;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.Confidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.DefaultConfidence;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
public class TestDefaultConfidence {

    private byte[] confidence = new byte[]{20,30,40,50};
    private byte[] differentConfidence = new byte[]{30,40,20,50};
    private Confidence sut = new DefaultConfidence(confidence);

    @Test
    public void constructor(){
        assertArrayEquals(confidence, sut.getData());
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }

    @Test
    public void equalsSameValues(){
        Confidence sameValues = new DefaultConfidence(confidence);
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
        Confidence differentValues = new DefaultConfidence(differentConfidence);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void ByteBufferConstructor(){
        Confidence sameValues = new DefaultConfidence(ByteBuffer.wrap(confidence));
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }

    @Test
    public void equalsByteBufferAtDifferentPositions(){
        final ByteBuffer buffer = ByteBuffer.wrap(confidence);
        Confidence sameValues = new DefaultConfidence(buffer);
        buffer.position(2);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }


}
