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

import java.util.Iterator;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.ShortSymbol;

public class DefaultPositionSequenceFastaRecord <T extends Sequence<ShortSymbol>> extends AbstractFastaRecord<ShortSymbol,T> implements PositionSequenceFastaRecord<T>{

    private final T positions;
    public DefaultPositionSequenceFastaRecord(String id, T positions){
        this(id, null, positions);
    }
    public DefaultPositionSequenceFastaRecord(String id, String comments, T positions){
        super(id,comments);
        this.positions = positions;
        
    }
    @Override
    protected CharSequence getRecordBody() {
    	 int length = (int)positions.getLength();
        StringBuilder result = new StringBuilder(5*length);

       Iterator<ShortSymbol> iter = positions.iterator();
       int i=1;
       while(iter.hasNext()){
    	   result.append(String.format("%04d", iter.next().getValue()));
    	   if(iter.hasNext()){
    		   if(i%12 == 0){
                   this.appendCarriageReturnAndLineFeed(result);
               }
               else{
                   result.append(' ');
               }    		   
    	   }    	   
    	   i++;
       }       
       return result.toString();
    }

   
    @Override
    public T getSequence() {
        return positions;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DefaultPositionSequenceFastaRecord)){
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
