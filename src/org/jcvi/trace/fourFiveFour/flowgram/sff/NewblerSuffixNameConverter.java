/*
 * Created on Jul 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;

public final class NewblerSuffixNameConverter {
    private static final Pattern RANGE_PATTERN = Pattern.compile("\\.(\\d+)-(\\d+)");
    
    private NewblerSuffixNameConverter(){}
    
    public static String getUnSuffixedNameFrom(String suffixedId){
        return suffixedId.replaceAll("\\..+$", "")
                                        .replace("_left","")
                                        .replace("_right","");
    }
    
    public static Range getSuffixedRangeFrom(String suffixedId){
        Matcher matcher =RANGE_PATTERN.matcher(suffixedId);
        if(matcher.find()){
            int first = Integer.parseInt(matcher.group(1));
            int second = Integer.parseInt(matcher.group(2));
            if(first > second){
                //swap
                int temp = second;
                second = first;
                first = temp;
            }
          //shift left to make 0-based
            return Range.buildRange(first, second)
                            .shiftLeft(1);  
        }
        return null;
    }
}
