/*
 * Created on Jan 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.InputStream;

import java.net.URL;

public final class NCBIUtil {

    private NCBIUtil(){}
    
    
    public static InputStream getResponseFromNcbi(String url)
                                            throws Exception {
        int tries=0;
        while(true){
            try{
              //sleep required between calls to ncbi 
                Thread.sleep(3000);
                return new URL(url).openStream();
            }catch(Exception e){
                //error from ncbi try again
                System.out.println("tries..."+ tries);
                tries++;
                if(tries >4){
                    throw e;
                }
            }
        }
    }
}
