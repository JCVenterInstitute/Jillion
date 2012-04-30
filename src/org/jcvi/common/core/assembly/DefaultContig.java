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
package org.jcvi.common.core.assembly;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

public class DefaultContig<P extends PlacedRead> extends AbstractContig<P>{

    

    public DefaultContig(String id, NucleotideSequence consensus,
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

	public static class Builder extends AbstractContigBuilder<PlacedRead, Contig<PlacedRead>>{
        public Builder(String id, String consensus){
           this(id, new NucleotideSequenceBuilder(consensus).build());
        }
        public Builder(String id, NucleotideSequence consensus){
            super(id,consensus);
        }
        public Builder addRead(String id, int offset,String basecalls){
            return addRead(id, offset, basecalls, Direction.FORWARD);
        }
        public Builder addRead(String id, int offset,String basecalls, Direction dir){
            int numberOfGaps = computeNumberOfGapsIn(basecalls);
            int ungappedLength = basecalls.length()-numberOfGaps;
            return addRead(id, offset, Range.createOfLength(0,ungappedLength),basecalls, dir,ungappedLength);
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
        @Override
        public Builder addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){            
            if(offset <0){
                throw new IllegalArgumentException("circular reads not supported");
                
              }
            super.addRead(id, offset, validRange, basecalls, dir,fullUngappedLength);
            return this;            
        }
        
       
        public DefaultContig<PlacedRead> build(){
            Set<PlacedRead> reads = new LinkedHashSet<PlacedRead>();
            for(PlacedReadBuilder<PlacedRead> builder : getAllPlacedReadBuilders()){
                reads.add(builder.build());
            }
            return new DefaultContig<PlacedRead>(getContigId(), getConsensusBuilder().build(), reads);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<PlacedRead> createPlacedReadBuilder(
                PlacedRead read) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    read.getId(), 
                    read.getNucleotideSequence().toString(), 
                    (int)read.getGappedContigStart(), 
                    read.getDirection(), 
                    read.getValidRange(),
                    //TODO need to actually compute ungapped full length here
                    //should we pull from frg or db?
                    (int)read.getValidRange().getEnd());
        }
        /**
        * {@inheritDoc}
        */
        @Override
        protected PlacedReadBuilder<PlacedRead> createPlacedReadBuilder(
                String id, int offset, Range validRange, String basecalls,
                Direction dir, int fullUngappedLength) {
            return DefaultPlacedRead.createBuilder(
                    getConsensusBuilder().build(), 
                    id, 
                    basecalls, 
                    offset, 
                    dir, 
                    validRange,
                    fullUngappedLength);
        }
    }

}
