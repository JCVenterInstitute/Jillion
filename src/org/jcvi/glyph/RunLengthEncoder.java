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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.Range;

public class RunLengthEncoder {

    
    
    public static <T> List<RunLength<T>> encode(Collection<T> collectionOfElements){
        List<RunLength<T>> encoding = new ArrayList<RunLength<T>>();
        List<T> elements = new ArrayList<T>(collectionOfElements);
        if(elements.isEmpty()){
            return encoding;
        }
        int counter = -1;
        for(int i =0; i< elements.size()-1; i++){
            if(!elements.get(i).equals(elements.get(i+1))){
                encoding.add(new RunLength<T>(elements.get(i), i-counter));
                counter =i;
            }
        }
        encoding.add(new RunLength<T>(elements.get(elements.size()-1),elements.size()-1-counter));
        return encoding;
    }
    public static <T> T decode(List<RunLength<T>> encoded, int decodedIndex){
        long previousIndex=-1;
        final Range target = Range.buildRangeOfLength(decodedIndex, 1);
        for(RunLength<T> runLength : encoded){
            long currentStartIndex = previousIndex+1;
            Range range = Range.buildRangeOfLength(currentStartIndex, runLength.getLength());
            
            if(range.intersects(target)){
                return runLength.getValue();
            }
            previousIndex = range.getEnd();
        }
        throw new ArrayIndexOutOfBoundsException(decodedIndex + " last index is "+ previousIndex);
    }
    public static <T> List<T> decode(List<RunLength<T>> encoding){
        List<T> decoded = new ArrayList<T>();
        for(RunLength<T> runLength : encoding){
            final T value = runLength.getValue();
            for(int i=0; i< runLength.getLength(); i++){                
                decoded.add(value);
            }
        }
        return decoded;
    }
}
