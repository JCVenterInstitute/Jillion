/*
 * Created on Dec 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.auth;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultTigrAuthorizerBuilder {

    String username = "user";
    char[] pass = "password".toCharArray();
    
    String server = "server";
    String project = "project";
    JCVIAuthorizer authorizer = new DefaultJCVIAuthorizer(username, pass);
    DefaultTigrAuthorizer tigrAuthorizer = new DefaultTigrAuthorizer.Builder(authorizer)
                                        .project(project)
                                        .server(server)
                                        .build();
    @Test
    public void defaultValues(){
        DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer.Builder()
                                .project(project)
                                .build();
        assertEquals(project, sut.getProject());
        assertEquals(DefaultTigrAuthorizer.DEFAULT_TIGR_SERVER, sut.getServer());
        
        assertArrayEquals(DefaultJCVIAuthorizer.DEFAULT_TIGR_USER.getPassword(), sut.getPassword());
        assertEquals(DefaultJCVIAuthorizer.DEFAULT_TIGR_USER.getUsername(), sut.getUsername());        
    }
    @Test
    public void setServer(){
        DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer.Builder()
                                .project(project)
                                .server(server)
                                .build();
        assertEquals(project, sut.getProject());
        assertEquals(server, sut.getServer());
        
        assertArrayEquals(DefaultJCVIAuthorizer.DEFAULT_TIGR_USER.getPassword(), sut.getPassword());
        assertEquals(DefaultJCVIAuthorizer.DEFAULT_TIGR_USER.getUsername(), sut.getUsername());        
    }
    @Test
    public void jcviAuthorizer(){
        DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer.Builder()
                                    .project(project)
                                    .server(server)
                                    .authorizer(authorizer)
                                    .build();
        assertEquals(project, sut.getProject());
        assertEquals(server, sut.getServer());
        assertEquals(authorizer.getUsername(), sut.getUsername());
        assertArrayEquals(authorizer.getPassword(), sut.getPassword());        
    }
    @Test
    public void jcviAuthorizerConstructor(){
        DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer.Builder(authorizer)
                                    .project(project)
                                    .server(server)
                                    .build();
        assertEquals(project, sut.getProject());
        assertEquals(server, sut.getServer());
        assertEquals(authorizer.getUsername(), sut.getUsername());
        assertArrayEquals(authorizer.getPassword(), sut.getPassword());        
    }
    @Test(expected = NullPointerException.class)
    public void settingNullProjectShouldThrowNPE(){
        new DefaultTigrAuthorizer.Builder()
        .project(null);
    }
    @Test(expected = NullPointerException.class)
    public void settingNullServerShouldThrowNPE(){
        new DefaultTigrAuthorizer.Builder()
        .project(project)
        .server(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void settingNullAuthorizerShouldThrowNPE(){
        new DefaultTigrAuthorizer.Builder()
                                    .project(project)
                                    .server(server)
                                    .authorizer(null);
    }
    @Test(expected = NullPointerException.class)
    public void notSettingAprojectShouldThrowNPE(){
        new DefaultTigrAuthorizer.Builder()
        .server(server)
        .authorizer(authorizer)
        .build();
    }
    @Test
    public void tigrAuthorizerConstructor(){
        String differentServer = "different"+server;
        DefaultTigrAuthorizer sut = new DefaultTigrAuthorizer.Builder(tigrAuthorizer)
                                        .server(differentServer)
                                        .build();
            assertEquals(project, sut.getProject());
            assertEquals(differentServer, sut.getServer());
            assertEquals(tigrAuthorizer.getUsername(), sut.getUsername());
            assertArrayEquals(tigrAuthorizer.getPassword(), sut.getPassword());        
    }
}
