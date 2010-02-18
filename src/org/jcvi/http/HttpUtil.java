/*
 * Created on Sep 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;


public final class HttpUtil {

    private HttpUtil(){}
    
    /** The default character encoding for text data */
    public static final Charset ASCII = Charset.forName("ASCII");

    /** The separator string to place between variables */
    public static final char VAR_SEPARATOR = '&';
    /** The separator string to place between variable names and their values */
    public static final char VALUE_SEPARATOR = '=';
    /**
     * Java Property key which points the trust store to 
     * use for SSL Authentication.
     */
    public static final String SSL_TRUSTSTORE_PROPERTY_KEY = "javax.net.ssl.trustStore";
    
    /**
     * Writes the given string as a URL encoded string.
     * 
     * @param data The data to write.
     * @throws UnsupportedEncodingException 
     * @throws IOException  If there is an error writing to the stream.
     */
    public static String urlEncode(String data) throws UnsupportedEncodingException 
    {
        return URLEncoder.encode(data, HttpUtil.ASCII.name());
    }
}
