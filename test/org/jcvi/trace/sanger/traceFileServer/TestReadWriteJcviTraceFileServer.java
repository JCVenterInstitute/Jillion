/*
 * Created on Aug 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.auth.JCVIEncodedAuthorizer;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.ReadWriteJcviTraceFileServer;
import org.jtc.chromatogram_archiver.api.archiver.exception.ChromatogramArchiveException;
import org.jtc.chromatogram_archiver.api.archiver.intf.ChromatogramArchiver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestReadWriteJcviTraceFileServer {

    private ChromatogramArchiver mockChromatogramArchiver;
    private JCVIEncodedAuthorizer authorizer = createMock(JCVIEncodedAuthorizer.class);
    
    private ReadWriteJcviTraceFileServer sut;
    private String project = "project";
    private String URL_BASE = "traceFileServer/url/base";
    
    ChromatogramArchiveException expectedException = new ChromatogramArchiveException("expected");
    @Before
    public void setup(){
        mockChromatogramArchiver = createMock(ChromatogramArchiver.class);
        sut = new ReadWriteJcviTraceFileServer(URL_BASE, authorizer,mockChromatogramArchiver, project);
    }
    @Test
    public void nullProjectThrowsNullPointerException(){
        try{
            new ReadWriteJcviTraceFileServer(URL_BASE, authorizer,mockChromatogramArchiver, null);
            fail("should throw NPE when project is null");
        }catch(NullPointerException e){
            assertEquals("project can not be null", e.getMessage());
        }
    }
    @Test
    public void nullChromatogramArchiverThrowsNullPointerException(){
        try{
            new ReadWriteJcviTraceFileServer(URL_BASE, authorizer,null, project);
            fail("should throw NPE when ChromatogramArchiver is null");
        }catch(NullPointerException e){
            assertEquals("ChromatogramArchiver can not be null", e.getMessage());
        }
    }
    @Test
    public void putFile() throws ChromatogramArchiveException, IOException{
        File file = createMock(File.class);
        String fileId = "fileId";
        expect(mockChromatogramArchiver.archiveChromatogramFile(file, fileId, project)).andReturn(0L);
        replay(mockChromatogramArchiver);
        sut.putFile(fileId, file);
        verify(mockChromatogramArchiver);
    }
    @Test
    public void putFileThrowsChromatogramArchiveExceptionShouldWrapInIOException() throws ChromatogramArchiveException{
        File file = createMock(File.class);
        String fileId = "fileId";
        String filePath = "path/to/file";
        expect(mockChromatogramArchiver.archiveChromatogramFile(file, fileId, project)).andThrow(expectedException);
        expect(file.getAbsolutePath()).andReturn(filePath);
        replay(mockChromatogramArchiver,file);
        try{
            sut.putFile(fileId, file);
            fail("should wrap ChromatogramArchiveException in IOException");
        }catch(IOException e){
            assertEquals("error putting "+ fileId+ " ("+filePath+") into Trace File Server", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockChromatogramArchiver,file);
    }
    
    @Test
    public void putStream() throws ChromatogramArchiveException, IOException{
        InputStream stream = createMock(InputStream.class);
        String fileId = "fileId";
        expect(mockChromatogramArchiver.archiveChromatogramFile(stream, fileId, project)).andReturn(0L);
        replay(mockChromatogramArchiver);
        sut.putStream(fileId, stream);
        verify(mockChromatogramArchiver);
    }
    
    @Test
    public void putStreamThrowsChromatogramArchiveExceptionShouldWrapInIOException() throws ChromatogramArchiveException{
        InputStream stream = createMock(InputStream.class);
        String fileId = "fileId";
        expect(mockChromatogramArchiver.archiveChromatogramFile(stream, fileId, project)).andThrow(expectedException);
        
        replay(mockChromatogramArchiver);
        try{
            sut.putStream(fileId,stream);
            fail("should wrap ChromatogramArchiveException in IOException");
        }catch(IOException e){
            assertEquals("error putting stream of "+fileId +" into Trace File Server", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockChromatogramArchiver);
    }
}
