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

import java.util.Arrays;

import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultJCVIAuthorizer {
    private final String username = "Username";
    private final char[] password = "pass".toCharArray();
    
    DefaultJCVIAuthorizer sut = new DefaultJCVIAuthorizer(username, password);
    @Test(expected = NullPointerException.class)
    public void nullUserNameShouldThrowNPE(){
        new DefaultJCVIAuthorizer(null, password);
    }
    @Test(expected = NullPointerException.class)
    public void nullPasswordShouldThrowNPE(){
        new DefaultJCVIAuthorizer(username, null);
    }
    @Test
    public void notClosed(){
        assertFalse(sut.isClosed());
    }
    @Test
    public void getPassword(){
        assertArrayEquals(password, sut.getPassword());
    }
    @Test
    public void getUsername(){
        assertEquals(username, sut.getUsername());
    }
    @Test
    public void close(){
        sut.close();
        assertTrue(sut.isClosed());        
    }
    @Test(expected = IllegalStateException.class)
    public void getPasswordShouldThrowIllegalStateExceptionWhenClosed(){
       sut.close();
       sut.getPassword();
    }
    @Test(expected = IllegalStateException.class)
    public void getUsernameShouldThrowIllegalStateExceptionWhenClosed(){
       sut.close();
       sut.getUsername();
    }
    
    @Test
    public void finalizeShouldForceClose() throws Throwable{
        sut.finalize();
        assertTrue(sut.isClosed());
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentType(){
        assertFalse(sut.equals("not an authorizer"));
    }
    
    @Test
    public void sameValuesShouldBeEqual(){
        DefaultJCVIAuthorizer sameValues = new DefaultJCVIAuthorizer(username, password);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentUsernameShouldNotBeEqual(){
        DefaultJCVIAuthorizer differentUser = new DefaultJCVIAuthorizer("different"+username, password);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentUser);
    }
    @Test
    public void differentPasswordShouldNotBeEqual(){
        DefaultJCVIAuthorizer differentPass = new DefaultJCVIAuthorizer(username, Arrays.copyOf(password, password.length-1));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentPass);
    }
    @Test
    public void ShouldNotBeEqualIfClosed(){
        DefaultJCVIAuthorizer closed = new DefaultJCVIAuthorizer(username, password);
        closed.close();
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, closed);
    }
    @Test
    public void testTString(){
        assertEquals("DefaultJCVIAuthorizer for user "+username, sut.toString());
    }
}
