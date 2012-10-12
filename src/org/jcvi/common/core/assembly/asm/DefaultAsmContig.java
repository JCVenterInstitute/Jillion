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

package org.jcvi.common.core.assembly.asm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AbstractContig;
import org.jcvi.common.core.assembly.ContigBuilder;
import org.jcvi.common.core.assembly.AssembledReadBuilder;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public final class DefaultAsmContig extends AbstractContig<AsmAssembledRead> implements AsmContig{

    private final boolean isDegenerate;
    
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus){
        return createBuilder(id,consensus,false);
    }
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus, boolean isDegenerate){
        return new DefaultAsmContigBuilder(id, consensus, isDegenerate);
    }
    private DefaultAsmContig(String id, NucleotideSequence consensus,
            Set<AsmAssembledRead> reads,boolean isDegenerate) {
        super(id,consensus,reads);
        this.isDegenerate = isDegenerate;
    }

   

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isDegenerate() {
        return isDegenerate;
    }

    private static class DefaultAsmContigBuilder implements AsmContigBuilder{

        private final NucleotideSequence fullConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private final Map<String, AsmAssembledReadBuilder>aceReadBuilderMap = new HashMap<String, AsmAssembledReadBuilder>();
   
        boolean isDegenerate;
        DefaultAsmContigBuilder(String id, NucleotideSequence consensus,boolean isDegenerate){
            this.contigId = id;
            this.fullConsensus = consensus;
            this.mutableConsensus = new NucleotideSequenceBuilder(fullConsensus);
            this.isDegenerate = isDegenerate;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> setContigId(String contigId) {
            this.contigId =contigId;
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return contigId;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return aceReadBuilderMap.size();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> addRead(
                AsmAssembledRead placedRead) {
            return addRead(placedRead.getId(),
                    placedRead.getNucleotideSequence().toString(),
                    (int)placedRead.getGappedStartOffset(),
                    placedRead.getDirection(),
                    placedRead.getReadInfo().getValidRange(),
                    placedRead.getReadInfo().getUngappedFullLength(),
                    placedRead.isRepeatSurrogate());
        }
        

         /**
          * {@inheritDoc}
          */
          @Override
          public AsmContigBuilder addRead(String readId, String validBases,
                  int offset, Direction dir, Range clearRange,
                  int ungappedFullLength, boolean isSurrogate) {
              aceReadBuilderMap.put(readId, DefaultAsmPlacedRead.createBuilder(
                      this.fullConsensus, readId, validBases, offset, dir, clearRange, ungappedFullLength, isSurrogate));
              return this;
          }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> addAllReads(
                Iterable<AsmAssembledRead> reads) {
           for(AsmAssembledRead read : reads){
               addRead(read);
           }
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<? extends AssembledReadBuilder<AsmAssembledRead>> getAllAssembledReadBuilders() {
           
            return aceReadBuilderMap.values();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledReadBuilder<AsmAssembledRead> getAssembledReadBuilder(String readId) {
            return aceReadBuilderMap.get(readId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmAssembledRead, AsmContig> removeRead(String readId) {
            aceReadBuilderMap.remove(readId);   
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return mutableConsensus;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AsmContig build() {
            Set<AsmAssembledRead> reads = new HashSet<AsmAssembledRead>(aceReadBuilderMap.size()+1);
            for(AsmAssembledReadBuilder builder : aceReadBuilderMap.values()){
                reads.add(builder.build());
            }
            aceReadBuilderMap.clear();
            return new DefaultAsmContig(contigId,mutableConsensus.build(),reads, isDegenerate);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void setDegenerate(boolean isDegenerate) {
            this.isDegenerate = isDegenerate;
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean isDegenerate() {
            return isDegenerate;
        }
       
        
    }
}
