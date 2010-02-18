/*
 * Created on Jul 8, 2009
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
import org.jcvi.glyph.num.DefaultShortGlyphCodec;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;

public class PositionsFastaRecordUtil {
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+).*");
    private static final ShortGlyphFactory shortGlyphFactory  = ShortGlyphFactory.getInstance();

    public static DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>> buildFastaRecord(
            String identifier, String comment, CharSequence sequence) {
        List<ShortGlyph> qualities = parsePositions(sequence);
        return new DefaultPositionFastaRecord<EncodedGlyphs<ShortGlyph>>(identifier, comment, 
                new DefaultEncodedGlyphs<ShortGlyph>(DefaultShortGlyphCodec.getInstance(),qualities));
    }

    public static List<ShortGlyph> parsePositions(CharSequence sequence) {
        Scanner scanner = new Scanner(sequence.toString());
        List<ShortGlyph> result = new ArrayList<ShortGlyph>();
        while(scanner.hasNextShort()){
            result.add(shortGlyphFactory.getGlyphFor(scanner.nextShort()));
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
