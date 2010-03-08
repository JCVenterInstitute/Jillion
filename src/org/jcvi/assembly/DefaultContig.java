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
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Set;


import org.jcvi.Range;
import org.jcvi.assembly.contig.AbstractContig;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultContig<P extends PlacedRead> extends AbstractContig<P>{

    

    protected DefaultContig(String id, NucleotideEncodedGlyphs consensus,
            Set<VirtualPlacedRead<P>> virtualReads,boolean circular) {
        super(id, consensus, virtualReads,circular);
    }
    
    public static class Builder extends AbstractContigBuilder<PlacedRead, DefaultContig<PlacedRead>>{
    
        public Builder(String id, NucleotideEncodedGlyphs consensus){
            super(id,consensus);
        }
        public void addRead(String id, int offset,Range validRange, String basecalls, SequenceDirection dir){
            
            if(offset <0){
                SplitReferenceEncodedNucleotideGlyphs referenceEncodedGlyphs = new SplitReferenceEncodedNucleotideGlyphs(getConsensus(), basecalls,offset, validRange);
                final DefaultPlacedRead actualPlacedRead = new DefaultPlacedRead(new DefaultRead(id, referenceEncodedGlyphs), offset,dir );
                
                long leftOffset = getConsensus().getLength() + offset;
                Range leftRange = Range.buildRangeOfLength(leftOffset, -1L*offset);
                Range rightRange = Range.buildRange(0, basecalls.length() - leftRange.size()-validRange.getStart());
                
                Range leftValidRange = Range.buildRangeOfLength(validRange.getStart(), leftRange.size());
                Range rightValidRange = Range.buildRange(leftValidRange.getEnd()+1, validRange.getEnd());
                
                SectionOfPlacedRead<PlacedRead> leftSection = new SectionOfPlacedRead<PlacedRead>(id+"_left",actualPlacedRead, 0,leftRange, leftValidRange );
                SectionOfPlacedRead<PlacedRead> rightSection = new SectionOfPlacedRead<PlacedRead>(id+"_right",actualPlacedRead, (int)rightValidRange.getStart(),rightRange, rightValidRange );
                addRead(leftSection);
                addRead(rightSection);
                
              }
            else{
                super.addRead(id, offset, validRange, basecalls, dir);
            }
            
        }
        @Override
        protected PlacedRead createPlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read, long offset, SequenceDirection dir){
            return new DefaultPlacedRead(read,offset,dir);
        }
       
        public DefaultContig build(){
            return new DefaultContig(getId(), getConsensus(), getVirtualReads(),isCircular());
        }
    }

}
