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
 * Created on Jan 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fastX.fasta.qual;

import java.util.List;

import org.jcvi.fastX.fasta.AbstractFastaRecord;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class DefaultQualityFastaRecord extends AbstractFastaRecord<EncodedGlyphs<PhredQuality>> implements QualityFastaRecord{
    private final EncodedGlyphs<PhredQuality> qualities;
    public DefaultQualityFastaRecord(String id, EncodedGlyphs<PhredQuality> qualities){
        this(id, null, qualities);
    }
    public DefaultQualityFastaRecord(String id, String comments, EncodedGlyphs<PhredQuality> qualities){
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
    public EncodedGlyphs<PhredQuality> getValue() {
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
