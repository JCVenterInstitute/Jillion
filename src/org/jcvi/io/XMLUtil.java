/*
 * Created on Sep 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

public final class XMLUtil {

    private XMLUtil(){}
    
    public static String beginTag(Object value){
        return String.format("<%s>", value);
    }
    public static String endTag(Object value){
        return String.format("</%s>", value);
    }
}
