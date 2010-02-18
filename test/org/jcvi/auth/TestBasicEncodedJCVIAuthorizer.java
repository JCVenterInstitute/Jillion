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
