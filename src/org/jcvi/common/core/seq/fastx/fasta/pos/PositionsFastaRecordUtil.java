/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jul 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fasta.pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.symbol.DefaultShortGlyphCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortGlyph;
import org.jcvi.common.core.symbol.ShortGlyphFactory;

public class PositionsFastaRecordUtil {
    private static final Pattern ID_LINE_PATTERN = Pattern.compile("^>(\\S+).*");
    private static final ShortGlyphFactory GLYPH_FACTORY  = ShortGlyphFactory.getInstance();

    public static DefaultPositionFastaRecord<Sequence<ShortGlyph>> buildFastaRecord(
            String identifier, String comment, CharSequence sequence) {
        List<ShortGlyph> positions = parsePositions(sequence);
        return new DefaultPositionFastaRecord<Sequence<ShortGlyph>>(identifier, comment, 
                new EncodedSequence<ShortGlyph>(DefaultShortGlyphCodec.getInstance(),positions));
    }

    public static List<ShortGlyph> parsePositions(CharSequence sequence) {
        Scanner scanner = new Scanner(sequence.toString());
        List<ShortGlyph> result = new ArrayList<ShortGlyph>();
        while(scanner.hasNextShort()){
            result.add(GLYPH_FACTORY.getGlyphFor(scanner.nextShort()));
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
