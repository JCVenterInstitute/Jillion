/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

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
