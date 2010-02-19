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
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.jcvi.auth.BasicEncodedJCVIAuthorizer;
import org.jcvi.auth.DefaultJCVIAuthorizer;
import org.jcvi.io.Base64;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestBasicJCVIAuthorizer {

    private String username = "username";
    private char[] password = "a password".toCharArray();
    
    BasicEncodedJCVIAuthorizer sut = new BasicEncodedJCVIAuthorizer(username,password);
    
    @Test
    public void getEncodedAuthorization(){
        assertEquals("Basic " +Base64.encode((username + ":"+new String(password)).getBytes()), 
                sut.getEncodedAuthorization());
    }
    
    @Test
    public void defaultUser(){
        String expected = "Basic " +Base64.encode("datasupt:forscripts".getBytes());
        assertEquals(expected, 
                new BasicEncodedJCVIAuthorizer(DefaultJCVIAuthorizer.DEFAULT_USER)
                        .getEncodedAuthorization());
    }
}
