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

package org.jcvi.common.core.assembly;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigBuilder<P extends PlacedRead, C extends Contig<P>> implements ContigBuilder<P,C>{
        private NucleotideSequenceBuilder consensus;
        private String id;
        private final Map<String, PlacedReadBuilder<P>> reads;
        public AbstractContigBuilder(String id, NucleotideSequence consensus){
            this.id = id;
            this.consensus = new NucleotideSequenceBuilder(consensus);
            reads = new LinkedHashMap<String,PlacedReadBuilder<P>>();
        }
        public AbstractContigBuilder<P,C> addRead(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength){
            reads.put(id, createPlacedReadBuilder(id,offset,validRange,basecalls,dir,fullUngappedLength));
            return this;
        }
        
        public  AbstractContigBuilder<P,C>  addRead(P read){
            reads.put(read.getId(),createPlacedReadBuilder(read));
            return this;
        }
        protected abstract PlacedReadBuilder<P> createPlacedReadBuilder(P read);
        protected abstract PlacedReadBuilder<P> createPlacedReadBuilder(String id, int offset,Range validRange, String basecalls, Direction dir, int fullUngappedLength);
      
       
        public AbstractContigBuilder<P,C> setId(String id){
            this.id = id;
            return this;
        }
        
        /**
         * 
        * {@inheritDoc}
         */
        @Override
        public abstract C build();
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<P, C> setContigId(String contigId) {
            id = contigId;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return id;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return reads.size();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<P, C> addAllReads(Iterable<P> reads) {
            for(P read : reads){
                addRead(read);
            }
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<? extends PlacedReadBuilder<P>> getAllPlacedReadBuilders() {
            return reads.values();
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<P> getPlacedReadBuilder(String readId) {
            return reads.get(readId);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public void removeRead(String readId) {
            reads.remove(readId);
            
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return consensus;
        }
        
        
   
}
