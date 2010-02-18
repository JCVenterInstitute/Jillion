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
 * Created on Dec 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import org.jcvi.io.Base64;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBasicEncodedJCVIAuthorizer {

    String username = "username";
    char[] password = "password".toCharArray();
    BasicEncodedJCVIAuthorizer sut;
    @Before
    public void setup(){
        sut = new BasicEncodedJCVIAuthorizer(username, password);
    }
    @Test
    public void encode(){        
        assertEquals(username, sut.getUsername());
        assertArrayEquals(password, sut.getPassword());
        String expectedEncodedAuth = "Basic "+Base64.encode((username+":"+new String(password)).getBytes());
        assertEquals(expectedEncodedAuth, sut.getEncodedAuthorization());
    }
    @Test
    public void close(){
        sut.close();
        assertTrue(sut.isClosed());
    }
    @Test(expected = IllegalStateException.class)
    public void getEncodedAuthorizationShouldThrowIllegalStateExceptionifClosed(){
        sut.close();
        sut.getEncodedAuthorization();
    }
}
