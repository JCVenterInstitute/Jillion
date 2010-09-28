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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.auth.JCVIEncodedAuthorizer;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.FileType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.RequestType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.ReturnFormat;
import org.jcvi.util.StringUtilities;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestJcviTraceFileServer_getting {

    private HttpURLConnection mockConnection;
    private JcviTraceFileServer sut;
    private String urlBase = "/base/url";
    private JCVIEncodedAuthorizer authorizer;
    String id= "id";
    
    @Before
    public void setup() throws SecurityException{
        mockConnection = createMock(HttpURLConnection.class);
        authorizer = createMock(JCVIEncodedAuthorizer.class);

        sut = createMockBuilder(JcviTraceFileServer.class)
            .withConstructor(urlBase,authorizer)
            .addMockedMethod("createURLConnectionFor",                        
                        String.class,RequestType.class,FileType.class,ReturnFormat.class)
            .createMock();
      
    }
    
    @Test
    public void contains() throws MalformedURLException, IOException{
        RequestType type = RequestType.SEQ_NAME;
        FileType fileType = FileType.ZTR;
        ReturnFormat returnFormat = ReturnFormat.SINGLE;
        
        expect(sut.createURLConnectionFor(id, type, fileType, returnFormat)).andReturn(mockConnection);
        mockConnection.setRequestMethod("HEAD");
        setUpValidResponse();
        replay(sut, mockConnection);
        assertTrue(sut.contains(id));
        verify(sut, mockConnection);
    }
    
    @Test
    public void doesNotContain() throws MalformedURLException, IOException{
       
        RequestType type = RequestType.SEQ_NAME;
        FileType fileType = FileType.ZTR;
        ReturnFormat returnFormat = ReturnFormat.SINGLE;
        
        expect(sut.createURLConnectionFor(id, type, fileType, returnFormat)).andReturn(mockConnection);
        mockConnection.setRequestMethod("HEAD");
        setupNotValidResponse();
        replay(sut, mockConnection);
        assertFalse(sut.contains(id));
        verify(sut, mockConnection);
    }
    private void setUpValidResponse() throws IOException {
        expect(mockConnection.getResponseCode()).andReturn(HttpURLConnection.HTTP_OK);
    }
    private void setupNotValidResponse() throws IOException {
        expect(mockConnection.getResponseCode()).andReturn(HttpURLConnection.HTTP_NOT_FOUND).anyTimes();
    }
    private void setupNotValidResponse(String message) throws IOException {
        setupNotValidResponse();
        expect(mockConnection.getResponseMessage()).andReturn(message);
    }
    @Test
    public void getFileAsStream() throws MalformedURLException, IOException{
        InputStream expectedInputStream = createMock(InputStream.class);
        expect(sut.createURLConnectionFor(id, RequestType.SEQ_NAME, FileType.ZTR, ReturnFormat.SINGLE)).andReturn(mockConnection);
        mockConnection.connect();
        setUpValidResponse();
        expect(mockConnection.getInputStream()).andReturn(expectedInputStream);
        replay(sut, mockConnection);
        assertEquals(expectedInputStream, sut.getFileAsStream(id));
        verify(sut, mockConnection);
    }
    
    @Test
    public void getMultipleFileAsStream() throws MalformedURLException, IOException{
        List<String> idsAsList = Arrays.asList("id_1","id_2", "id_3");
        InputStream expectedInputStream = createMock(InputStream.class);
        expect(sut.createURLConnectionFor(new StringUtilities.JoinedStringBuilder(
                idsAsList)
        .glue(",")
        .build()
        , RequestType.SEQ_NAME, FileType.ZTR, ReturnFormat.JAR)).andReturn(mockConnection);
        mockConnection.connect();
        setUpValidResponse();
        expect(mockConnection.getInputStream()).andReturn(expectedInputStream);
        replay(sut, mockConnection);
        assertNotNull(sut.getMultipleFilesAsStream(idsAsList, RequestType.SEQ_NAME, FileType.ZTR));
        verify(sut, mockConnection);
    }
    @Test
    public void HTTP_errorCodeOnGetShouldThrowIOException() throws IOException {
        String responseMessage = "expected response message";
        expect(sut.createURLConnectionFor(id, RequestType.SEQ_NAME, FileType.ZTR, ReturnFormat.SINGLE)).andReturn(mockConnection);
        
        mockConnection.connect();
        setupNotValidResponse(responseMessage);
        replay(sut, mockConnection);
        try{
            sut.getFileAsStream(id);
            fail("should throw IOException on invalid response");
        }catch(IOException e){
            String topLevelErrorMessage = "could not fetch traces for "+id;
            assertEquals(topLevelErrorMessage, e.getMessage());
            Throwable actualCause = e.getCause();
            assertEquals("could not get trace(s) Response Code = " +
                    HttpURLConnection.HTTP_NOT_FOUND+" : "+ responseMessage, actualCause.getMessage());
        }
        
    }
}
