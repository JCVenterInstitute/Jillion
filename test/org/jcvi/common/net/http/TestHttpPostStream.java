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

package org.jcvi.common.net.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestHttpPostStream extends EasyMockSupport{

    private ByteArrayOutputStream out;
    HttpPostStream sut;
    @Before
    public void setup() throws IOException{
        out= new ByteArrayOutputStream();
        URLConnection urlConnection = createMock(URLConnection.class);
        urlConnection.setDoOutput(true);
        expect(urlConnection.getOutputStream()).andReturn(out);
        replayAll();
        sut = new HttpPostStream(urlConnection);
    }
    
    @Test
    public void oneFlag() throws IOException{
        sut.addFlag("test");
        sut.close();        
        assertEquals("test",new String(out.toByteArray()));
    }
    
    @Test
    public void multipleFlags() throws IOException{
        sut.addFlag("test");
        sut.addFlag("test2");
        sut.close();        
        assertEquals("test&test2",new String(out.toByteArray()));
    }
    @Test
    public void oneKeyValuePair() throws IOException{
        sut.addVariable("key","value");
        sut.close();        
        assertEquals("key=value",new String(out.toByteArray()));
    }
    @Test
    public void multipleKeyValuePairs() throws IOException{
        sut.addVariable("key1","value1");
        sut.addVariable("key2","value2");
        sut.close();        
        assertEquals("key1=value1&key2=value2",new String(out.toByteArray()));
    }
    
    @Test
    public void mixAndMatch() throws IOException{
        sut.addVariable("key1","value1");
        sut.addFlag("flag");
        sut.addVariable("key2","value2");
        sut.close();        
        assertEquals("key1=value1&flag&key2=value2",new String(out.toByteArray()));
  
    }
    
}
