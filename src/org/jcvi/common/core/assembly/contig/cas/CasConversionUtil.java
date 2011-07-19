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

package org.jcvi.common.core.assembly.contig.cas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
public final class CasConversionUtil {

    private static final Pattern TRIM_FILE_MAP_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+)");
    /**
     * Parse the given trimFileMap file.
     * <p>
     * The TrimFileMap should be a file which contains tab delimited
     * file names as a key value pair.  the Key is the trimmed file
     * and the value is the corresponding untrimmed file.
     * @param in the {@link File} to parse containing trimFileMap data.
     * @return a {@link Map} containing the trimmed file to untrimmed file
     * mapping.
     * @throws FileNotFoundException if the given file does not exist.
     */
    public static Map<String, String> parseTrimmedToUntrimmedFiles(File trimFileMap) throws FileNotFoundException{
        FileInputStream in = new FileInputStream(trimFileMap);
        try{
            return parseTrimmedToUntrimmedFiles(in);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    /**
     * Parse the given trimFileMap data as an InputStream.
     * <p>
     * The TrimFileMap should be a file which contains tab delimited
     * file names as a key value pair.  the Key is the trimmed file
     * and the value is the corresponding untrimmed file.
     * @param in the {@link InputStream} to parse containing trimFileMap data.
     * @return a {@link Map} containing the trimmed file to untrimmed file
     * mapping.
     */
    public static Map<String, String> parseTrimmedToUntrimmedFiles(InputStream in){
        Map<String, String> map = new HashMap<String, String>();
        Scanner scanner = new Scanner(in);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = TRIM_FILE_MAP_PATTERN.matcher(line);
            if(matcher.find()){
                map.put(matcher.group(1), matcher.group(2));
            }
        }
        scanner.close();
        return Collections.unmodifiableMap(map);
    }
}
