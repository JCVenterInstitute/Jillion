/*
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.easymock.IAnswer;
import org.finj.FTPClient;
import org.finj.FTPException;
import org.finj.RemoteFile;
import org.jcvi.io.IOUtil;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestFTPFileServer {
    ResourceFileServer resourceFileServer = new ResourceFileServer(TestFTPFileServer.class);
    FTPClient mockClient;
    IOException expectedIOException = new IOException("expected");
    FTPException expectedFTPException = new FTPException("expected",0,"test");
    
    String server = "server";
    String username = "user";
    char[] pass = "password".toCharArray();
    
    @Before
    public void setup(){
        mockClient = createMock(FTPClient.class);
        mockClient.isVerbose(false);
    }
    
    @Test
    public void constructor() throws IOException{
        mockClient.open(server);
        mockClient.login(username, pass);
        replay(mockClient);
        new FTPFileServer(mockClient,server, username, pass);
        verify(mockClient);
        
    }
    @Test
    public void openthrowsIOExceptionShouldTossUp() throws FTPException, UnknownHostException, IOException{
        mockClient.open(server);
        expectLastCall().andThrow(expectedIOException);
        replay(mockClient);
        try {
            new FTPFileServer(mockClient,server, username, pass);
            fail("should throw IOException");
        } catch (IOException e) {
           assertEquals(expectedIOException, e);
        }
        verify(mockClient);
        
    }
    
    @Test
    public void loginthrowsIOExceptionShouldTossUp() throws FTPException, UnknownHostException, IOException{
        mockClient.open(server);
        mockClient.login(username, pass);
        expectLastCall().andThrow(expectedIOException);
        replay(mockClient);
        try {
            new FTPFileServer(mockClient,server, username, pass);
            fail("should throw IOException");
        } catch (IOException e) {
           assertEquals(expectedIOException, e);
        }
        verify(mockClient);
        
    }
    
    @Test
    public void openthrowsFTPExceptionShouldWrapInIOException() throws FTPException, UnknownHostException, IOException{
        mockClient.open(server);
        expectLastCall().andThrow(expectedFTPException);
        replay(mockClient);
        try {
            new FTPFileServer(mockClient,server, username, pass);
            fail("should throw IOException");
        } catch (IOException e) {
           assertEquals("error logging into server "+ server,e.getMessage());
           assertEquals(expectedFTPException, e.getCause());
        }
        verify(mockClient);
        
    }
    
    @Test
    public void loginthrowsFTPExceptionShouldWrapInIOException() throws FTPException, UnknownHostException, IOException{
        mockClient.open(server);
        mockClient.login(username, pass);
        expectLastCall().andThrow(expectedFTPException);
        replay(mockClient);
        try {
            new FTPFileServer(mockClient,server, username, pass);
            fail("should throw IOException");
        } catch (IOException e) {
           assertEquals("error logging into server "+ server,e.getMessage());
           assertEquals(expectedFTPException, e.getCause());
        }
        verify(mockClient);
        
    }
    
    @Test
    public void doesNotSupportObjects() throws IOException{
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        assertFalse(sut.supportsGettingFileObjects());
    }
    @Test
    public void tryingToGetFileObjectShouldthrowUnsupportedOperationException() throws IOException{
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        try {
            sut.getFile("some file");
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            assertEquals("can not create local files", e.getMessage());
        }
    }
    
    @Test
    public void close() throws IOException{
        mockClient.open(server);
        mockClient.login(username, pass);
        mockClient.close();
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        sut.close();
        verify(mockClient);
    }
    @Test
    public void throwingExceptionOnCloseShouldTossExceptionUp() throws IOException{
        mockClient.open(server);
        mockClient.login(username, pass);
        mockClient.close();
        expectLastCall().andThrow(expectedIOException);
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        try{
            sut.close();
            fail("should toss up IOException on IOException");
        }catch(IOException e){
            assertEquals(expectedIOException, e);
        }
        verify(mockClient);
    }
    
    @Test
    public void putFileAsStream() throws FTPException, UnknownHostException, IOException{
        String path = "files/README.txt";
        mockClient.open(server);
        mockClient.login(username, pass);
        mockClient.putFile(isA(InputStream.class), eq(path), eq(true));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        sut.putStream(path, resourceFileServer.getFileAsStream(path));
        verify(mockClient);
    }
    
    @Test
    public void putFile() throws FTPException, UnknownHostException, IOException{
        String path = "files/README.txt";
        mockClient.open(server);
        mockClient.login(username, pass);
        mockClient.putFile(isA(InputStream.class), eq(path), eq(true));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        sut.putFile(path, resourceFileServer.getFile(path));
        verify(mockClient);
    }
    
    @Test
    public void getFileAsStream() throws FileNotFoundException, IOException{
        String path = "files/README.txt";
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        File expectedFile = resourceFileServer.getFile(path);
        IOUtil.writeToOutputStream(new FileInputStream(expectedFile), expected);
        mockClient.open(server);
        mockClient.login(username, pass);
        mockClient.getFile(isA(OutputStream.class), eq(path));
        expectLastCall().andAnswer(new IAnswer<Object>() {
            
            @Override
            public Object answer() throws Throwable {
                OutputStream out =(OutputStream)getCurrentArguments()[0];
                String path = (String)getCurrentArguments()[1];
                IOUtil.writeToOutputStream(resourceFileServer.getFileAsStream(path), out);
                return null;
            }
        });
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        IOUtil.writeToOutputStream(sut.getFileAsStream(path), actual);
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        verify(mockClient);
    }
    
    @Test
    public void contains() throws FTPException, UnknownHostException, IOException{
        String path = "files/README.txt";
        
        mockClient.open(server);
        mockClient.login(username, pass);
        expect(mockClient.getFileDescriptors("files")).andReturn(createRemoteFilesThatContain("README.txt"));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        assertTrue(sut.contains(path));
        verify(mockClient);
    }
    @Test
    public void doesNotContain() throws FTPException, UnknownHostException, IOException{
        String path = "files/README.txt";
        
        mockClient.open(server);
        mockClient.login(username, pass);
        expect(mockClient.getFileDescriptors("files")).andReturn(createRemoteFilesThatDoNotContain("README.txt"));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        assertFalse(sut.contains(path));
        verify(mockClient);
    }
    @Test
    public void containThrowsFTPExceptionShouldWrapInIOException() throws FTPException, UnknownHostException, IOException{
        String path = "files/README.txt";
        
        mockClient.open(server);
        mockClient.login(username, pass);
        expect(mockClient.getFileDescriptors("files")).andThrow(expectedFTPException);
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        try{
            sut.contains(path);
            fail("should wrap FTPException in IOException");
        }catch(IOException e){
            assertEquals("error getting data from FTP", e.getMessage());
            assertEquals(expectedFTPException, e.getCause());
        }
        verify(mockClient);
    }
    @Test
    public void containsDeepNestedFile() throws FTPException, UnknownHostException, IOException{
        String path = "files/someFolder/deep/down/README.txt";
        
        mockClient.open(server);
        mockClient.login(username, pass);
        expect(mockClient.getFileDescriptors("files/someFolder/deep/down")).andReturn(createRemoteFilesThatContain("README.txt"));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        assertTrue(sut.contains(path));
        verify(mockClient);
    }
    @Test
    public void containsFromRoot() throws FTPException, UnknownHostException, IOException{
        String path = "README.txt";
        
        mockClient.open(server);
        mockClient.login(username, pass);
        expect(mockClient.getFileDescriptors("")).andReturn(createRemoteFilesThatContain("README.txt"));
        replay(mockClient);
        FTPFileServer sut = new FTPFileServer(mockClient,server, username, pass);
        assertTrue(sut.contains(path));
        verify(mockClient);
    }
    private RemoteFile[] createRemoteFilesThatDoNotContain(String filename){
        return new RemoteFile[]{
                createRemoveFile("not"+filename),
                createRemoveFile("not"+filename+"again"),
        };
    }
    private RemoteFile[] createRemoteFilesThatContain(String filename){
        return new RemoteFile[]{
                createRemoveFile("not"+filename),
                createRemoveFile(filename)
        };
    }
    
    private RemoteFile createRemoveFile(String fileName){
        RemoteFile f = createMock(RemoteFile.class);
        expect(f.getName()).andReturn(fileName);
        replay(f);
        return f;
    }
}
