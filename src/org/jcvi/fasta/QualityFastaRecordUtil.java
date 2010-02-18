/*
 * Created on Apr 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public final class QualityFastaRecordUtil {
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+).*");
    private static final RunLengthEncodedGlyphCodec RUN_LENGTH_CODEC = RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE;


    public static DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>> buildFastaRecord(
            String identifier, String comment, CharSequence sequence) {
        List<PhredQuality> qualities = parseQualities(sequence);
        return new DefaultQualityFastaRecord<EncodedGlyphs<PhredQuality>>(identifier, comment, 
                new DefaultEncodedGlyphs<PhredQuality>(RUN_LENGTH_CODEC,qualities));
    }

    public static List<PhredQuality> parseQualities(CharSequence sequence) {
        Scanner scanner = new Scanner(sequence.toString());
        List<PhredQuality> result = new ArrayList<PhredQuality>();
        while(scanner.hasNextByte()){
            result.add(PhredQuality.valueOf(scanner.nextByte()));
        }
        return result;
    }

    public static String parseIdentifierFromIdLine(String line) {
        final Matcher idMatcher = ID_LINE_PATTERN.matcher(line);
        if (idMatcher.matches()){
            return idMatcher.group(1);
        }
        return null;
    }
}
