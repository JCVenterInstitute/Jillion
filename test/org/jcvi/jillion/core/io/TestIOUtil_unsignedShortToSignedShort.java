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
package org.jcvi.jillion.core.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.jillion.core.io.IOUtil;
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
public class TestIOUtil_unsignedShortToSignedShort {

    @Parameters
    public static Collection<?> data(){
        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[]{0, (short)0});
        data.add(new Object[]{50, (short)50});
        data.add(new Object[]{100, (short)100});
        data.add(new Object[]{Short.MAX_VALUE+1, Short.MIN_VALUE});
        data.add(new Object[]{Short.MAX_VALUE+4, (short)(Short.MIN_VALUE+3)});
        data.add(new Object[]{Short.MAX_VALUE+100, (short)(Short.MIN_VALUE+99)});
        data.add(new Object[]{Byte.MAX_VALUE, (short)Byte.MAX_VALUE});
        data.add(new Object[]{Short.MAX_VALUE+Byte.MIN_VALUE, (short)(Short.MIN_VALUE+Byte.MIN_VALUE-1)});
        
        return data;
    }
    
    private final int unsigned;
    private final short signed;
    /**
     * @param unsigned
     * @param signed
     */
    public TestIOUtil_unsignedShortToSignedShort(int unsigned, short signed) {
        this.unsigned = unsigned;
        this.signed = signed;
    }
    
    @Test
    public void convertUnsignedToSigned(){
        assertEquals(IOUtil.toSignedShort(unsigned), signed);
    }
}
