/*
 * Created on Sep 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jcvi.auth.BasicEncodedJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.jcvi.common.io.zip.DefaultZipDataStore;
import org.jcvi.common.io.zip.InMemoryZipDataStore;
import org.jcvi.common.io.zip.ZipDataStore;
import org.jcvi.common.net.http.HttpUtil;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.FileType;
import org.jcvi.trace.sanger.traceFileServer.JcviTraceFileServer.RequestType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestActualJcviTraceFileServer {

    private static final ResourceFileServer RESOURCES = new ResourceFileServer(AbstractTestActualJcviTraceFileServer.class);

    private static String oldTrustStore;
    private JcviTraceFileServer traceFileServer;
    @BeforeClass    
    public static void setupTrustStore(){
        oldTrustStore = System.getProperty(HttpUtil.SSL_TRUSTSTORE_PROPERTY_KEY);
        System.setProperty(HttpUtil.SSL_TRUSTSTORE_PROPERTY_KEY,
        TraceFileServerUtil.TRACE_FILE_SERVER_TRUSTSTORE_UNIX_PATH);
        
    }
    @AfterClass    
    public static void restoreOldTrustStore(){
        if(oldTrustStore !=null){
            System.setProperty(HttpUtil.SSL_TRUSTSTORE_PROPERTY_KEY,
                    oldTrustStore);
        }
    }
    protected abstract String getURL();
    protected abstract JCVIAuthorizer getAuthorizer();
    @Before
    public void setup(){
        traceFileServer = JcviTraceFileServer.createJcviTraceFileServer(
                getURL(), 
                new BasicEncodedJCVIAuthorizer(getAuthorizer()));
        
    }
    @Test
    public void getSingleTrackerSequence() throws IOException{
        String seqname = "AAAGA50TR";
        InputStream actualInputStream =traceFileServer.getFileAsStream(seqname);
        InputStream expectedInputStream = RESOURCES.getFileAsStream("files/AAAGA50TR.ztr");
        compareInputStreams(expectedInputStream, actualInputStream);
    }
    
    @Test 
    public void getTrackerSequenceNames() throws IOException, DataStoreException{
       List<String> idList = createListOf("AAAGA50TR,AAAGA51TF,AAAGA51TR,AAAGA52TF,AAAGA52TR,AAAGA53TF,AAAGA53TR,AAAGA54TF,AAAGA54TR,AAAGA55TF,AAAGA55TR,AAAGA56TF,AAAGA57TF,AAAGA57TR,AAAGA58TF,AAAGA58TR,AAAGA59TF,AAAGA59TR,AAAGA60TF,AAAGA60TR,AAAGA61TR,AAAGA62TF,AAAGA62TR,AAAGA63TF,AAAGA64TF,AAAGA64TR,AAAGA65TF,AAAGA65TR,AAAGA66TF");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.SEQ_NAME, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/trackerSequenceNames.jar");
       compareJars(expectedJar, actualInputStream);
    }
    
    @Test 
    public void getTrackerReadIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("1097994628639,1097994628645,1097994628651,1097994628657,1097994628663,1097994628669,1097994628675,1097994628681,1097994628687,1097994628693");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.READ_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/trackerReadIDs.jar");
       compareJars(expectedJar, actualInputStream);
    }
    @Test 
    public void getTrackerTraceFileIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("1097994628502,1097994628508,1097994628514,1097994628520,1097994628526,1097994628532,1097994628538,1097994628544,1097994628550,1097994628556,1097994628562,1097994628568,1097994628574,1097994628580,1097994628586,1097994628592,1097994628598,1097994628604");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.TRACE_FILE_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/trackerTraceFileIDs.jar");
       compareJars(expectedJar, actualInputStream);
    }
    @Test 
    public void getTrackerTraceIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("1097994628503,1097994628509,1097994628515,1097994628527,1097994628533,1097994628539,1097994628545,1097994628557,1097994628563,1097994628569,1097994628575,1097994628587,1097994628593,1097994628599,1097994628605,1097994628617,1097994628623,1097994628629");
        InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.TRACE_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/trackerTraceIDs.jar");
       compareJars(expectedJar, actualInputStream);
    }
    @Test 
    public void getTrackerSequencerPlateIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("TFS-TESTING");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.SEQUENCER_PLATE_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/trackerPlateBarcode.jar");
       compareJars(expectedJar, actualInputStream);
    }
    @Test 
    public void getTrackerGelIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("515866,515863");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.GEL_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/gelIDRequest.jar");
       compareJars(expectedJar, actualInputStream);
    }
    
    @Test 
    public void getRunIDs() throws IOException, DataStoreException{
       List<String> idList = createListOf("1064025868491");
       InputStream actualInputStream =traceFileServer.getMultipleFilesAsStream(idList,RequestType.RUN_ID, FileType.ZTR);
       File expectedJar = RESOURCES.getFile("files/jlimsRunIDsZtrs.jar");
       compareJars(expectedJar, actualInputStream);
    }
    
    private List<String> createListOf(String commaSepString) {
       return Arrays.asList(commaSepString.split(","));
      
    }
    private void compareInputStreams(InputStream expected, InputStream actual) throws IOException{
        ByteArrayOutputStream expectedOut = new ByteArrayOutputStream();
        ByteArrayOutputStream actualOut = new ByteArrayOutputStream();
        IOUtil.writeToOutputStream(expected, expectedOut);
        IOUtil.writeToOutputStream(actual, actualOut);
        
        assertTrue("expected " + new String(expectedOut.toByteArray()) + " but got " + new String(actualOut.toByteArray()),
                Arrays.equals(expectedOut.toByteArray(), actualOut.toByteArray()));
    }
    
    private void compareJars(File expectedJar, InputStream inputStreamOfActualJar) throws ZipException, IOException, DataStoreException{
        final ZipFile zipfile = new ZipFile(expectedJar);
        ZipDataStore expectedDataStore = new DefaultZipDataStore(zipfile);
        ZipDataStore actualDataStore = InMemoryZipDataStore.createInMemoryZipDataStoreFrom(inputStreamOfActualJar);
        Iterator<String> expectedIds =expectedDataStore.getIds();
        while(expectedIds.hasNext()){
            String id= expectedIds.next();
            //ignore manifests because the files in the manifest may 
            //be in any order, also includes JVM version etc
            if(!id.startsWith("META-INF")){
                InputStream expectedInputStream = expectedDataStore.get(id);
                InputStream actualInputStream = actualDataStore.get(id);
                
                compareInputStreams(expectedInputStream,actualInputStream);
            }
        }
    }
}
