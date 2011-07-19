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
 * Created on Oct 13, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.nio.ByteBuffer;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFCommonHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultSFFCommonHeaderCodec_encode extends AbstractTestDefaultSFFCommonHeaderCodec {


    @Test
    public void validEncode(){
        byte[] expectedBytes = encode(expectedHeader);
        byte[] actualBytes =sut.encodeHeader(expectedHeader);
        assertArrayEquals(expectedBytes, actualBytes);
    }

    private byte[] encode(DefaultSFFCommonHeader expectedHeader) {
        final short keyLength =(short) (expectedHeader.getKeySequence().length());
        int size = 31+expectedHeader.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SFFUtil.caclulatePaddedBytes(size);
        ByteBuffer buf = ByteBuffer.wrap(new byte[size+padding]);
        buf.put(".sff".getBytes());
        buf.put(new byte[]{0,0,0,1});
        buf.put(IOUtil.convertUnsignedLongToByteArray(expectedHeader.getIndexOffset()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(expectedHeader.getIndexLength()));
        buf.put(IOUtil.convertUnsignedIntToByteArray(expectedHeader.getNumberOfReads()));
        buf.putShort((short)(size+padding));
        buf.putShort(keyLength);
        buf.put(IOUtil.convertUnsignedShortToByteArray(expectedHeader.getNumberOfFlowsPerRead()));
        buf.put((byte)1);
        buf.put(expectedHeader.getFlow().getBytes());
        buf.put(expectedHeader.getKeySequence().getBytes());
        return buf.array();
    }
}
