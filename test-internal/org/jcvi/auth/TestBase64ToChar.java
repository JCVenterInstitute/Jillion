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
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestBase64ToChar {

    @Parameters
    public static Collection<?> data(){

        List<Object[]> data = new ArrayList<Object[]>();
        for(int i=0; i<26; i++){
            data.add(new Object[]{(byte)i, (char)('A'+i)});
            data.add(new Object[]{(byte)(i+26), (char)('a'+i)});
        }
        for(byte i=0; i<10; i++){
            data.add(new Object[]{(byte)(i+52), (""+i).charAt(0)});
        }
        data.add(new Object[]{(byte)(62), '+'});
        data.add(new Object[]{(byte)(63), '/'});
        return data;
    }
    
    private byte value;
    private char expectedChar;
    /**
     * @param value
     * @param expectedChar
     */
    public TestBase64ToChar(byte value, char expectedChar) {
        this.value = value;
        this.expectedChar = expectedChar;
    }
    
    @Test
    public void toChar(){
        assertEquals(expectedChar, Base64.base64ToChar(value));
    }
    
}
