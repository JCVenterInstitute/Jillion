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

package org.jcvi.assembly.cas.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dkatzel
 *
 *
 */
public class VariationsUtil {
    private static Pattern CONTIG_HEADER = Pattern.compile("^(\\S+):\\s*$");
    private static Pattern VARIATION_RECORD = Pattern.compile("^\\s+(\\d+)\\s+");
    public static Map<String, List<Integer>> parseVariationPositionsFrom(File variationsFile) throws FileNotFoundException{
        Scanner scanner = new Scanner(variationsFile);
        Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> currentList= null;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            Matcher matcher = CONTIG_HEADER.matcher(line);
            if(matcher.matches()){
                String id = matcher.group(1);
                currentList= new ArrayList<Integer>();
                map.put(id, currentList);
            }
            else{
                Matcher record = VARIATION_RECORD.matcher(line);
                if(record.find()){
                    currentList.add(Integer.valueOf(record.group(1)));
                }
            }
        }
        return map;
    }
    
    public static void main(String[] args) throws FileNotFoundException{
        File f = new File("/usr/local/scratch/dkatzel/casSNP/vars.mixed.log");
        Map<String, List<Integer>>  map = VariationsUtil.parseVariationPositionsFrom(f);
        System.out.println(map.get("NS"));
    }
}
