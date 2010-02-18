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
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;
import static org.junit.Assert.*;
import org.junit.Test;
public class TestDefaultTigrAuthorizer {

    
    String username = "user";
    char[] pass = "password".toCharArray();
    
    String server = "server";
    String project = "project";
    JCVIAuthorizer authorizer = new DefaultJCVIAuthorizer(username, pass);
    
    DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer(authorizer, project, server);
   
    
    @Test
    public void getServer(){
        assertEquals(server, sut.getServer());
    }
    @Test
    public void getProject(){
        assertEquals(project, sut.getProject());
    }
    
    @Test
    public void getUsername(){
        assertEquals(username,sut.getUsername());
    }
    
    @Test
    public void getPassword(){
        assertArrayEquals(pass,sut.getPassword());
    }
}
