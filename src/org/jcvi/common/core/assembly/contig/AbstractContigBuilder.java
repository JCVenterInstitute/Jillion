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

package org.jcvi.common.core.assembly.contig;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.util.Builder;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigBuilder<P extends PlacedRead, C extends Contig<P>> implements Builder<C>{
        private NucleotideSequence consensus;
        private String id;
        private final Set<P> reads;
        public AbstractContigBuilder(String id, NucleotideSequence consensus){
            this.id = id;
            this.consensus = consensus;
            reads = new LinkedHashSet<P>();
        }
        public AbstractContigBuilder<P,C> addRead(String id, int offset,Range validRange, String basecalls, Direction dir){
            
            NucleotideSequence referenceEncoded = NucleotideSequenceFactory.createReferenceEncoded(consensus,basecalls, offset);
            final P actualPlacedRead = createPlacedRead(new DefaultRead(id, referenceEncoded), offset,dir, validRange );
            
            return addRead(actualPlacedRead);
        }
        public  AbstractContigBuilder<P,C>  addRead(P read){
            reads.add(read);
            return this;
        }
        protected abstract P createPlacedRead(Read<ReferenceEncodedNucleotideSequence> read, long offset, Direction dir, Range validRange);
        
        public NucleotideSequence getConsensus() {
            return consensus;
        }
        public String getId() {
            return id;
        }
        public AbstractContigBuilder<P,C> changeConsensus(NucleotideSequence newConsensus){
            this.consensus = newConsensus;
            return this;
        }
        public AbstractContigBuilder<P,C> setId(String id){
            this.id = id;
            return this;
        }
        public Set<P> getPlacedReads() {
            return reads;
        }
        public abstract C build();
   
}
