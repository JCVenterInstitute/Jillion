/*
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SequenceFastaRecordUtil {
   
    
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+)(\\s+(.*))?");
    
    private SequenceFastaRecordUtil(){}
    
    public static String parseCommentFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
            return idMatcher.group(3);
        }
        return null;
    }

    public static String parseIdentifierFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.find()){
            return idMatcher.group(1);
        }
        return null;
    }
    
    public static String removeWhitespace(CharSequence sequence) {
        String sequenceWithoutWhitespace = sequence.toString().replaceAll("\\s+", "");
        return sequenceWithoutWhitespace;
    }
}
