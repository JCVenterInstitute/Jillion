/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;

public class RunLengthEncoder {

    
    
    public static <T> List<RunLength<T>> encode(List<T> elements){
        List<RunLength<T>> encoding = new ArrayList<RunLength<T>>();
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
