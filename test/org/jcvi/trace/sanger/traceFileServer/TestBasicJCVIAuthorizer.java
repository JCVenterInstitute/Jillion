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
