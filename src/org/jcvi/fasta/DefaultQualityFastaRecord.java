/*
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.util.List;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultQualityFastaRecord<T extends EncodedGlyphs<PhredQuality>> extends AbstractFastaRecord<T> implements QualityFastaRecord<T>{
    private final T qualities;
    public DefaultQualityFastaRecord(String id, T qualities){
        this(id, null, qualities);
    }
    public DefaultQualityFastaRecord(String id, String comments, T qualities){
        super(id,comments);
        this.qualities = qualities;
        
    }
    @Override
    protected CharSequence getRecordBody() {
        StringBuilder result = new StringBuilder();
        
       final List<PhredQuality> decodedQualities = qualities.decode();
       for(int i=1; i<decodedQualities.size(); i++){
           result.append(String.format("%02d", decodedQualities.get(i-1).getNumber()));
           if(i%17 == 0){
               this.appendCarriageReturnAndLineFeed(result);
           }
           else{
               result.append(" ");
           }        
       }
       //last value doesn't get a space
       result.append(String.format("%02d", decodedQualities.get(decodedQualities.size()-1).getNumber()));
       return result.toString();
    }

    @Override
    public T getValues() {
        return qualities;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DefaultQualityFastaRecord)){
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    
}
