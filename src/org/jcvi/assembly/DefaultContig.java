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
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultContig<P extends PlacedRead> extends AbstractContig<P>{

    

    public DefaultContig(String id, NucleotideEncodedGlyphs consensus,
            Set<P> reads) {
        super(id, consensus, reads);
    }
    
    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public static class Builder extends AbstractContigBuilder<PlacedRead, DefaultContig<PlacedRead>>{
        public Builder(String id, String consensus){
           this(id, new DefaultNucleotideEncodedGlyphs(consensus));
        }
        public Builder(String id, NucleotideEncodedGlyphs consensus){
            super(id,consensus);
        }
        public Builder addRead(String id, int offset,String basecalls){
            return addRead(id, offset, basecalls, SequenceDirection.FORWARD);
        }
        public Builder addRead(String id, int offset,String basecalls, SequenceDirection dir){
            int numberOfGaps = computeNumberOfGapsIn(basecalls);
            return addRead(id, offset, Range.buildRangeOfLength(0,basecalls.length()-numberOfGaps),basecalls, dir);
        }
        /**
         * @param basecalls
         * @return
         */
        private int computeNumberOfGapsIn(String basecalls) {
            int count=0;
            for(int i=0; i<basecalls.length(); i++){
                if(basecalls.charAt(i) == '-'){
                    count++;
                }
            }
            return count;
        }
        public Builder addRead(String id, int offset,Range validRange, String basecalls, SequenceDirection dir){
            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir);
            return this;
            
        }
        @Override
        protected PlacedRead createPlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read, long offset, SequenceDirection dir){
            return new DefaultPlacedRead(read,offset,dir);
        }
       
        public DefaultContig<PlacedRead> build(){
            return new DefaultContig<PlacedRead>(getId(), getConsensus(), getPlacedReads());
        }
    }

}
