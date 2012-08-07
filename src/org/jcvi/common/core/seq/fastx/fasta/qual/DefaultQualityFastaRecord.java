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
package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.util.Iterator;

import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaRecord;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;

public class DefaultQualityFastaRecord extends AbstractFastaRecord<PhredQuality,QualitySequence> implements QualitySequenceFastaRecord{
    private final QualitySequence qualities;
    public DefaultQualityFastaRecord(String id, QualitySequence qualities){
        this(id, null, qualities);
    }
    public DefaultQualityFastaRecord(String id, String comments, QualitySequence qualities){
        super(id,comments);
        this.qualities = qualities;
        
    }
    @Override
    protected CharSequence getRecordBody() {
        int length = (int)qualities.getLength();
		StringBuilder result = new StringBuilder(3*length);
		Iterator<PhredQuality> iter = qualities.iterator();
        int i=1;
        while(iter.hasNext()){
        	result.append(String.format("%02d", iter.next().getQualityScore()));
        	if(iter.hasNext()){
        		if(i%17 == 0){
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
    public QualitySequence getSequence() {
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
