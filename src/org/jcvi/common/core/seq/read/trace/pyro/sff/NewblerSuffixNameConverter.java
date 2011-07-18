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
 * Created on Jul 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

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
