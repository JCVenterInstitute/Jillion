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

package org.jcvi.assembly;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jcvi.Builder;
import org.jcvi.Range;
import org.jcvi.glyph.nuc.DefaultReferencedEncodedNucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractContigBuilder<P extends PlacedRead, C extends Contig<P>> implements Builder<C>{
        private NucleotideEncodedGlyphs consensus;
        private String id;
        private final Set<P> reads;
        private boolean circular;
        public AbstractContigBuilder(String id, NucleotideEncodedGlyphs consensus){
            this.id = id;
            this.consensus = consensus;
            reads = new LinkedHashSet<P>();
        }
        public AbstractContigBuilder<P,C> addRead(String id, int offset,Range validRange, String basecalls, SequenceDirection dir){
            
            NucleotideEncodedGlyphs referenceEncoded = new DefaultReferencedEncodedNucleotideGlyph(consensus,basecalls, offset,validRange);
            final P actualPlacedRead = createPlacedRead(new DefaultRead(id, referenceEncoded), offset,dir );
            
            return addRead(actualPlacedRead);
        }
        protected  AbstractContigBuilder<P,C>  addRead(P read){
            reads.add(read);
            return this;
        }
        protected abstract P createPlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read, long offset, SequenceDirection dir);
        
        public NucleotideEncodedGlyphs getConsensus() {
            return consensus;
        }
        public String getId() {
            return id;
        }
        public AbstractContigBuilder<P,C> changeConsensus(NucleotideEncodedGlyphs newConsensus){
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
        public boolean isCircular() {
            return circular;
        }
        public abstract C build();
   
}
