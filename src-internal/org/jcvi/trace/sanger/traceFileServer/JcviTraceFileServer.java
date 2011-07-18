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
 * Created on Jul 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import org.jcvi.auth.JCVIEncodedAuthorizer;
import org.jcvi.common.core.util.StringUtilities;
import org.jcvi.http.HttpGetRequestBuilder;
import org.jcvi.io.fileServer.ReadWriteFileServer;
import org.jtc.chromatogram_archiver.api.archiver.client.JTCChromatogramArchiver;
import org.jtc.chromatogram_archiver.api.archiver.exception.ChromatogramArchiveException;
import org.jtc.chromatogram_archiver.api.archiver.intf.ChromatogramArchiver;
/**
 * {@code JcviTraceFileServer} is a {@link TraceFileServer}
 * implementation that interacts with the 
 * JCVI Trace File Server.
 * @author dkatzel
 *
 *
 */
public abstract class JcviTraceFileServer implements TraceFileServer{
    
    /**
     * Supported File Types from JCVI Trace File Server.
     * @author dkatzel     *
     *
     */
    public enum FileType{
        /**
         * Data is encoded in ZTR format.
         */
        ZTR,
        /**
         * Data is encoded in SCF format.
         */
        SCF
    }
    /**
     * Supported Id types.
     * @author dkatzel
     *
     *
     */
    public enum RequestType{
        /**
         * Given ids are Trace Ids.
         */
        TRACE_ID("TraceID"),
        /**
         * Given ids are Trace File Ids.
         */
        TRACE_FILE_ID("TraceFileID"),
        /**
         * Given ids are Read Ids.
         */
        READ_ID("ReadID"),
        /**
         * Given ids are TIGR Sequence Names.
         */
        SEQ_NAME("SeqName"),
        /**
         * The Given ids is a JLIMS Sequencer Plate IDs.
         */
        SEQUENCER_PLATE_ID("SequencerPlateID"),
        /**
         * The Given ids is a JLIMS Run IDs.
         */
        RUN_ID("RunID"),
        /**
         * The Given ids is a TIGR Gel IDs.
         */
        GEL_ID ("GelID");
        
        private final String urlTag;
        
        RequestType(String urlTag){
            this.urlTag = urlTag;
        }
        /**
         * get URL tag to specify type in
         * Trace File Server URL request.
         * @return
         */
        public String getUrlTag() {
            return urlTag;
        }
        @Override
        public String toString() {
            return getUrlTag();
        }
        
        public static RequestType parseRequestType(String type){
            for(RequestType r : values()){
                if(r.getUrlTag().equalsIgnoreCase(type)){
                    return r;
                }
            }
            throw new IllegalArgumentException("unknown request type" + type);
        }
    }
    /**
     * Supported File Format containers the returned
     * traces are returned as.
     * @author dkatzel
     *
     *
     */
    public enum ReturnFormat{
        /**
         * Returns a single trace.
         */
        SINGLE("SINGLE"),
        /**
         * Returns all traces as a single
         * Jar file.
         */
        JAR("JAR"),
        /**
         * Returns all traces as a single
         * ZIP file.
         */
        ZIP("ZIP");
        
        private final String urlTag;
        
        ReturnFormat(String urlTag){
            this.urlTag = urlTag;
        }
        /**
         * get URL tag to specify type in
         * Trace File Server URL request.
         * @return
         */
        public String getUrlTag() {
            return urlTag;
        }
        @Override
        public String toString() {
            return getUrlTag();
        }
    }
    /**
     * Default implementation of {@link JTCChromatogramArchiver}
     * which is used to write data to Trace File Server.
     */
    private static JTCChromatogramArchiver DEFAULT_CHROMATOGRAM_ARCHIVER;
    
    static{
            DEFAULT_CHROMATOGRAM_ARCHIVER = createDefaultChromatogramArchiver();        
    }
    /**
     * Sets the Set the System property used by the JTCChromatogramArchiver EJB
     * for the URL(s) used for the production Trace File Server.
     * @see TraceFileServerUtil#JCVI_WRITE_URLS
     */
    public static final synchronized void useDefaultTraceFileServerWriterUrl(){
        useTraceFileServerWriterUrl(TraceFileServerUtil.JCVI_WRITE_URLS);
    }
    /**
     * Set the System property used by the JTCChromatogramArchiver EJB
     * for the URL(s) to send traces to load into the TraceFileServer.
     * @param url the URL to send traces to load into the TraceFileServer.  May be 
     * comma separated list of URLs which will be tried consecutively until
     * one works.
     */
    public static synchronized final void useTraceFileServerWriterUrl(String url){
        System.setProperty(TraceFileServerUtil.TRACE_FILE_SERVER_WRITER_URL_BASE_KEY, 
                url);
        DEFAULT_CHROMATOGRAM_ARCHIVER = createDefaultChromatogramArchiver();
    }
    
    private static final JTCChromatogramArchiver createDefaultChromatogramArchiver() throws IllegalStateException{
        try{
            return new JTCChromatogramArchiver();
        } catch (ChromatogramArchiveException e) {
            throw new IllegalStateException("can not create JTCChromatogramArchiver",e);
        }
    }
    /**
     * Creates a {@link JcviTraceFileServer} that can only
     * fetch data.
     * @param urlBase url base path to communicate with Trace File Server
     * (can not be null).
     * @param authorizer {@link JCVIEncodedAuthorizer} implementation required
     * to fetch data from secure projects (can not be null)..
     * @return a new ReadOnlyJcviTraceFileServer.
     * @throws NullPointerException if any parameters are null.
     */
    public static ReadOnlyJcviTraceFileServer createJcviTraceFileServer(String urlBase,JCVIEncodedAuthorizer authorizer){
        return new ReadOnlyJcviTraceFileServer(urlBase, authorizer);
    }
    /**
     * Creates a {@link ReadWriteJcviTraceFileServer} that fetch and
     * put trace data.
     * @param fetchUrlBase url base path to communicate with Trace File Server 
     * (can not be null).
     * @param authorizer {@link JCVIEncodedAuthorizer} implementation required
     * to fetch data from secure projects (can not be null)..
     * @param project the project any data put into the Trace File Server
     * will be associated with (can not be null)..
     * @return a new ReadWriteJcviTraceFileServer.
     * @throws NullPointerException if any parameters are null.
     */
    public static ReadWriteJcviTraceFileServer createJcviTraceFileServer(String fetchUrlBase,JCVIEncodedAuthorizer authorizer, String project){
        return new ReadWriteJcviTraceFileServer(fetchUrlBase, authorizer,DEFAULT_CHROMATOGRAM_ARCHIVER,project);
    }
    
    private final String urlBase;
    private final JCVIEncodedAuthorizer authorizer;
    
    protected JcviTraceFileServer(String urlBase, final JCVIEncodedAuthorizer authorizer){
        if(urlBase ==null){
            throw new NullPointerException("url can not be null");
        }
        if(authorizer ==null){
            throw new NullPointerException("authorizer can not be null");
        }
        this.urlBase = urlBase;
        this.authorizer = authorizer;
    }
    
    
    @Override
    public boolean supportsGettingFileObjects() {
        return false;
    }

    protected HttpURLConnection createURLConnectionFor(String id, RequestType type, FileType fileType, ReturnFormat returnFormat ) throws MalformedURLException, IOException{
        HttpURLConnection connection = generateTraceFileServerURLConnection(id, type,
                fileType, returnFormat);
        connection.setDoInput(true);
        connection.setUseCaches(true);
        connection.setRequestProperty("Authorization", authorizer.getEncodedAuthorization());
        return connection;
                  
    }

    protected HttpURLConnection generateTraceFileServerURLConnection(String id, RequestType type,
            FileType fileType, ReturnFormat returnFormat) throws IOException {
        HttpGetRequestBuilder builder = createHttpGetRequestBuilder(urlBase);
        return builder.addVariable(type.toString()+"s", id)
                .addVariable("TraceFileType", fileType)
                .addVariable("ReturnFormat", returnFormat)
                .build();
        
    }
    protected HttpGetRequestBuilder createHttpGetRequestBuilder(String urlBase) {
        return new HttpGetRequestBuilder(urlBase);
    }
    @Override
    public boolean contains(String seqName) throws IOException {
        return contains(seqName, RequestType.SEQ_NAME);
    }

    public boolean contains(String id,RequestType requestType) throws IOException {
        HttpURLConnection connection =createURLConnectionFor(id, requestType, FileType.ZTR, ReturnFormat.SINGLE);
        connection.setRequestMethod("HEAD");
        return responseCodeIsOK(connection);
    }
    @Override
    public File getFile(String fileId) throws IOException {
        throw new UnsupportedOperationException("can not get raw file objects");
    }
    /**
     * Gets the Trace with the given TIGR Sequence Name in ZTR format.
     * This is the same as calling {@code getFileAsStream(seqName, RequestType.SEQ_NAME, FileType.ZTR)}.
     * @param seqName the TIGR Sequence Name to get the ZTR trace for.
     * @throws IOException if there is a problem fetching the trace.
     * @see #getFileAsStream(String, RequestType, FileType)
     */
    @Override
    public InputStream getFileAsStream(String seqName) throws IOException {
        return getFileAsStream(seqName, RequestType.SEQ_NAME, FileType.ZTR);
    }

    public InputStream getFileAsStream(String id,
            final RequestType requestType, final FileType fileType)
            throws IOException {
        return getFileAsStream(id,requestType, fileType, ReturnFormat.SINGLE);
    }
    
   
    public InputStream getFileAsStream(String id,
            final RequestType requestType, final FileType fileType, ReturnFormat returnFormat)
            throws IOException {
        HttpURLConnection connection =createURLConnectionFor(id, requestType, fileType, returnFormat);
        connection.connect();
        try{
            verifyOKResponseCode(connection);
        }catch(IOException e){
            throw new IOException("could not fetch traces for "+id,e);
        }
      
        return connection.getInputStream();
    }
    /**
     * Checks that the HTTP Response code is HTTP_OK (200).
     * @param connection the HttpURLConnection to inspect.
     * @throws IOException if the Response code is not HTTP_OK.
     */
    private void verifyOKResponseCode(HttpURLConnection connection)
            throws IOException {
        if(!responseCodeIsOK(connection)){
            throw new IOException(
                    String.format("could not get trace(s) Response Code = %d : %s", 
                            connection.getResponseCode(),connection.getResponseMessage()));
        }
    }
    private boolean responseCodeIsOK(HttpURLConnection connection)
            throws IOException {
        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
    }
    public InputStream getMultipleFilesAsStream(Iterable<String> ids,
            final RequestType requestType, final FileType fileType)
            throws IOException {
        return getMultipleFilesAsStream(ids,requestType, fileType, ReturnFormat.JAR);
    }
    public InputStream getMultipleFilesAsStream(Iterable<?> ids,
            final RequestType requestType, final FileType fileType, ReturnFormat returnFormat)
            throws IOException {
        return getFileAsStream(new StringUtilities.JoinedStringBuilder(ids)
                                .glue(',')
                                .build()
                                ,requestType, fileType, returnFormat);
    }
    @Override
    public void close() throws IOException {
        // no-op
    }

    
    public static class ReadOnlyJcviTraceFileServer extends JcviTraceFileServer{

        public ReadOnlyJcviTraceFileServer(String urlBase,
                JCVIEncodedAuthorizer authorizer) {
            super(urlBase, authorizer);
        }
        
    }
    public static class ReadWriteJcviTraceFileServer extends JcviTraceFileServer implements ReadWriteFileServer{
        private ChromatogramArchiver chromatogramArchiver;
        private final String project;

        /**
         * @param urlBase
         * @param authorizer
         */
        protected ReadWriteJcviTraceFileServer(String urlBase,
                JCVIEncodedAuthorizer authorizer,ChromatogramArchiver chromatogramArchiver, String project) {
            super(urlBase, authorizer);
            if(chromatogramArchiver ==null){
                throw new NullPointerException("ChromatogramArchiver can not be null");
            }
            if(project ==null){
                throw new NullPointerException("project can not be null");
            }
            this.chromatogramArchiver = chromatogramArchiver;
            this.project = project;
        }

        @Override
        public void putFile(String fileId, File fileToPut) throws IOException {
            try {
                chromatogramArchiver.archiveChromatogramFile(fileToPut, fileId, project);
            } catch (ChromatogramArchiveException e) {
                throw new IOException(
                        String.format("error putting %s (%s) into Trace File Server", fileId, 
                                fileToPut.getAbsolutePath()), e);
            }
        }

        @Override
        public void putStream(String id, InputStream inputStream)
                throws IOException {
            try {
                chromatogramArchiver.archiveChromatogramFile(inputStream, id, project);
            } catch (ChromatogramArchiveException e) {
                throw new IOException("error putting stream of "+id +" into Trace File Server", e);
            }
        }

        @Override
        public File createNewDirIfNeeded(String dirPath) throws IOException {
            throw new UnsupportedOperationException("can not create new dirs on TraceFileServer") ;
        }

        @Override
        public File createNewFile(String filePath) throws IOException {            
            throw new UnsupportedOperationException("can not create empty files on TraceFileServer") ;
        }
        @Override
        public File createNewDir(String dirPath) throws IOException {
            throw new UnsupportedOperationException("can not create new dirs on TraceFileServer");
        }

        @Override
        public void createNewSymLink(String pathtoFileToLink,
                String symbolicPath) throws IOException {
            throw new UnsupportedOperationException("sym links not supported");
            
        }

        @Override
        public boolean supportsSymlinks() {
            return false;
        }
        
    }
}
