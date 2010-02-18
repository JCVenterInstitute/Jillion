/*
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.jcvi.auth.JCVIEncodedAuthorizer;
import org.jcvi.http.HttpGetRequestBuilder;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.FileType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.RequestType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.ReturnFormat;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestJcviTraceFileServer {
    String urlBase = "traceFileServer_URL_BASE";
    JCVIEncodedAuthorizer authorizer;
    JcviTraceFileServer sut;
    HttpGetRequestBuilder requestBuilder;
    @Before
    public void setup(){
        authorizer = createMock(JCVIEncodedAuthorizer.class);
        requestBuilder = createMock(HttpGetRequestBuilder.class);
        sut = new JcviTraceFileServer(urlBase, authorizer) {

            @Override
            protected HttpGetRequestBuilder createHttpGetRequestBuilder(String urlBase) {
                return requestBuilder;
            }
            
        };
    }
    
    @Test
    public void generateTraceFileServerURL() throws IOException{
        String id = "traceId";
        HttpURLConnection expectedUrlConnection = createMock(HttpURLConnection.class);
        for(RequestType type : RequestType.values()){
           for(FileType fileType : FileType.values()){
               for(ReturnFormat returnFormat : ReturnFormat.values()){
                   reset(requestBuilder);
                   expect(requestBuilder.addVariable(type +"s", id)).andReturn(requestBuilder);
                   expect(requestBuilder.addVariable("TraceFileType", fileType)).andReturn(requestBuilder);
                   expect(requestBuilder.addVariable("ReturnFormat", returnFormat)).andReturn(requestBuilder);
                   expect(requestBuilder.build()).andReturn(expectedUrlConnection);
                   replay(requestBuilder);
                   assertEquals(expectedUrlConnection, 
                           sut.generateTraceFileServerURLConnection(id, type, fileType, returnFormat));
                   verify(requestBuilder);
               }
           }
        }
    }
    
    @Test
    public void doesNotSupportFileObjects(){
        assertFalse(sut.supportsGettingFileObjects());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void getFileShouldThrowUnsupportedOperationException() throws IOException{
        sut.getFile("should throw UnsupportedOperationException");
    }
    
    @Test(expected = NullPointerException.class)
    public void nullURLShouldThrowNullPointerException(){
        new JcviTraceFileServer(null, authorizer) {};
    }
    @Test(expected = NullPointerException.class)
    public void authorizerShouldThrowNullPointerException(){
        new JcviTraceFileServer(urlBase, null) {};
    }
    
}
