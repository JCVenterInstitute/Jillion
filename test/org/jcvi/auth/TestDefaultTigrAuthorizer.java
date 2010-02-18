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
