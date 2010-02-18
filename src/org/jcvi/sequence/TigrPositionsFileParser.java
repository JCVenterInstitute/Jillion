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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.glyph.num.EncodedShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;

public class TigrPositionsFileParser {
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    private static final Pattern POS_PATTERN = Pattern.compile("^(\\w+)\\s+(\\d)\\s+(\\w+)");
    
    public static PeakMap getPeakMap(InputStream tigrPosFile){
        Map<String, EncodedShortGlyph> map = new HashMap<String, EncodedShortGlyph>();
        Scanner scanner = new Scanner(tigrPosFile);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = POS_PATTERN.matcher(line);
            if(matcher.find()){
                String id = matcher.group(1);
                //ignore tigr peak version
                //Integer version = Integer.parseInt(matcher.group(2));
                String positionsAsHex = matcher.group(3);
                EncodedShortGlyph encodedPositions = convertToGlyphs(positionsAsHex);
                map.put(id, encodedPositions);
            }
        }
        return new DefaultPeakMap(map);
        
    }
    private static EncodedShortGlyph convertToGlyphs(String positionsAsHex) {
        List<Short> shorts = new ArrayList<Short>(positionsAsHex.length()/4);
        for(int i=0; i< positionsAsHex.length(); i+=4){
            String hex = getNextValueAsHex(positionsAsHex, i);
            shorts.add(Short.parseShort(hex, 16));            
        }
        return new EncodedShortGlyph(FACTORY.getGlyphsFor(shorts));
    }
    private static String getNextValueAsHex(String positionsAsHex, int i) {
        return positionsAsHex.substring(i, i+4);
    }
}
