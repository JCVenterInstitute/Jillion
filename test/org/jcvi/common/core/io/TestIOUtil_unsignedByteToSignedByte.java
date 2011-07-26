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

package org.jcvi.common.core.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestIOUtil_unsignedByteToSignedByte {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, (byte)0});
        data.add(new Object[]{50, (byte)50});
        data.add(new Object[]{100, (byte)100});
        data.add(new Object[]{255, (byte)-1});
        data.add(new Object[]{252, (byte)-4});
        data.add(new Object[]{156, (byte)-100});
        data.add(new Object[]{Byte.MAX_VALUE, Byte.MAX_VALUE});
        data.add(new Object[]{128, Byte.MIN_VALUE});
        return data;
    }
    
    private final int unsigned;
    private final byte signed;
    /**
     * @param unsigned
     * @param signed
     */
    public TestIOUtil_unsignedByteToSignedByte(int unsigned, byte signed) {
        this.unsigned = unsigned;
        this.signed = signed;
    }
    
    @Test
    public void convertUnsignedToSigned(){
        assertThat(IOUtil.convertUnsignedByteToSignedByte(unsigned),is(equalTo(signed)));
    }
    
}
