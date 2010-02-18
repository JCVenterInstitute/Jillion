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
package org.jcvi.fasta;

import java.util.List;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;

public class DefaultPositionFastaRecord <T extends EncodedGlyphs<ShortGlyph>> extends AbstractFastaRecord<T> implements PositionFastaRecord<T>{

    private final T positions;
    public DefaultPositionFastaRecord(String id, T positions){
        this(id, null, positions);
    }
    public DefaultPositionFastaRecord(String id, String comments, T positions){
        super(id,comments);
        this.positions = positions;
        
    }
    @Override
    protected CharSequence getRecordBody() {
        StringBuilder result = new StringBuilder();
        
       final List<ShortGlyph> decodedPositions = positions.decode();
       for(int i=1; i<decodedPositions.size(); i++){
           result.append(String.format("%04d", decodedPositions.get(i-1).getNumber()));
           if(i%12 == 0){
               this.appendCarriageReturnAndLineFeed(result);
           }
           else{
               result.append(" ");
           }        
       }
       //last value doesn't get a space
       result.append(String.format("%04d", decodedPositions.get(decodedPositions.size()-1).getNumber()));
       return result.toString();
    }

   
    @Override
    public T getValues() {
        return positions;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DefaultPositionFastaRecord)){
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
