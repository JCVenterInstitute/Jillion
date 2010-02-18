/*
 * Created on Oct 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.dataDelivery;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.jcvi.auth.DefaultJCVIAuthorizer;
import org.jcvi.auth.JCVIAuthorizer;
import org.jcvi.http.HttpGetRequestBuilder;
import org.jcvi.http.HttpUtil;
import org.jcvi.io.fileServer.DirectoryFileServer;
import org.jcvi.io.fileServer.ReadWriteFileServer;

public final class ReSequencingUtils {

    public enum OUTPUT_TYPE{
        CSV("csv"),
        EXCEL ("xls")
        ;
        private String type;
        private OUTPUT_TYPE(String type){
            this.type= type;
        }
        
        public String getType(){
            return type;
        }
    }
    private static final String GET_MANIFEST_URL ="http://reports-prod:8280/JasperServer-Pro/flow.html";
    
    public static InputStream getManifestFor(String sequencingPlateBarcode, JCVIAuthorizer auth,OUTPUT_TYPE outputType) throws IOException{
        HttpGetRequestBuilder builder = new HttpGetRequestBuilder(GET_MANIFEST_URL);
        builder.addVariable("_flowId", "viewReportFlow")
        .addVariable("reportUnit", "/Secured_Details/Resequencing/QC/Reseq_Manifest")
        .addVariable("standAlone", "true")
        .addVariable("ParentFolderUri", "/Secured_Details/Resequencing/QC")
        .addVariable("j_username", auth.getUsername())
        .addVariable("j_password", new String(auth.getPassword()))
        .addVariable("SearchBy", "sequencing_plate_barcode")
        .addVariable("SearchFor", sequencingPlateBarcode)
        .addVariable("output", outputType.getType());
        
        HttpURLConnection connection = builder.build();
        return connection.getInputStream();
    }
    
    public static InputStream getManifestFor(String Platebarcode,OUTPUT_TYPE outputType) throws IOException{
       return getManifestFor(Platebarcode, DefaultJCVIAuthorizer.JOE_USER, outputType);
    }
    
    public static void main(String args[]) throws IOException{
        System.setProperty(HttpUtil.SSL_TRUSTSTORE_PROPERTY_KEY,
        "/usr/local/devel/JTC/prod/dataDelivery/lib/security/cacerts");
        
        String pcrBarcode = "P303403";
        ReadWriteFileServer manifestDir = DirectoryFileServer.createReadWriteDirectoryFileServer("testManifestDir");
        InputStream manifest = ReSequencingUtils.getManifestFor(pcrBarcode, OUTPUT_TYPE.EXCEL);
        manifestDir.putStream("Manifest_"+pcrBarcode+".xls", manifest);
        
    }
}
