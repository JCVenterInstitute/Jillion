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

package org.jcvi.io;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author dkatzel
 *
 *
 */
@RunWith(Parameterized.class)
public class TestIOUtil_unsignedIntToSignedInt {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, (short)0});
        data.add(new Object[]{50, (short)50});
        data.add(new Object[]{100, (short)100});
        data.add(new Object[]{Short.MAX_VALUE+1, Short.MAX_VALUE+1});
        data.add(new Object[]{Integer.MAX_VALUE, Integer.MAX_VALUE});
        data.add(new Object[]{Integer.MAX_VALUE+4L, Integer.MIN_VALUE+3});
        data.add(new Object[]{Integer.MAX_VALUE+100L, Integer.MIN_VALUE+99});
        data.add(new Object[]{Byte.MAX_VALUE, Byte.MAX_VALUE});
        data.add(new Object[]{(long)Integer.MAX_VALUE+Byte.MAX_VALUE, Integer.MIN_VALUE+Byte.MAX_VALUE-1});
       
        return data;
    }
    
    private final long unsigned;
    private final int signed;
    /**
     * @param unsigned
     * @param signed
     */
    public TestIOUtil_unsignedIntToSignedInt(long unsigned, int signed) {
        this.unsigned = unsigned;
        this.signed = signed;
    }
    
    @Test
    public void convertUnsignedToSigned(){
        assertThat(IOUtil.convertUnsignedIntToSignedInt(unsigned),is(equalTo(signed)));
    }
}
