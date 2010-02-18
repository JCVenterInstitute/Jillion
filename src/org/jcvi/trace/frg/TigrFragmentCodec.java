/*
 * Created on Mar 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TigrFragmentCodec extends Frg2Parser {

    private static final Pattern TIGR_ID_PATTERN = Pattern.compile("src:\\s+(\\w+)");
    
    @Override
    protected String parseIdFrom(String frg) {
        Matcher matcher = TIGR_ID_PATTERN.matcher(frg);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }

}
