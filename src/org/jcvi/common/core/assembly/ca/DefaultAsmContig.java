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

package org.jcvi.common.core.assembly.ca;

import java.util.Collection;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.AbstractContigBuilder;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.ContigBuilder;
import org.jcvi.common.core.assembly.contig.DefaultContig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.PlacedReadBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultAsmContig implements AsmContig{

    private final Contig<AsmPlacedRead> contig;
    private final boolean isDegenerate;
    
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus){
        return createBuilder(id,consensus,false);
    }
    public static AsmContigBuilder createBuilder(String id, NucleotideSequence consensus, boolean isDegenerate){
        return new DefaultAsmContigBuilder(id, consensus, isDegenerate);
    }
    private DefaultAsmContig(Contig<AsmPlacedRead> contig, boolean isDegenerate) {
        this.contig = contig;
        this.isDegenerate = isDegenerate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public String getId() {
        return contig.getId();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfReads() {
        return contig.getNumberOfReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Set<AsmPlacedRead> getPlacedReads() {
        return contig.getPlacedReads();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideSequence getConsensus() {
        return contig.getConsensus();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AsmPlacedRead getPlacedReadById(String id) {
        return contig.getPlacedReadById(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean containsPlacedRead(String placedReadId) {
        return contig.containsPlacedRead(placedReadId);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isDegenerate() {
        return isDegenerate;
    }

    private static class DefaultAsmContigBuilder implements AsmContigBuilder{

        AbstractContigBuilder<AsmPlacedRead, Contig<AsmPlacedRead>> delegate;
        boolean isDegenerate;
        DefaultAsmContigBuilder(String id, NucleotideSequence consensus,boolean isDegenerate){
           // delegate = new DefaultContig.Builder(id, consensus);
            //TODO write builder code for asm
            delegate =null;
            this.isDegenerate = isDegenerate;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmPlacedRead, AsmContig> setContigId(String contigId) {
            delegate.setContigId(contigId);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public String getContigId() {
            return delegate.getContigId();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public int numberOfReads() {
            return delegate.numberOfReads();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmPlacedRead, AsmContig> addRead(
                AsmPlacedRead placedRead) {
            delegate.addRead(placedRead);
            return this;
        }
        
        /**
         * {@inheritDoc}
         */
         @Override
         public AsmContigBuilder addRead(String readId, String validBases,
                 int offset, Direction dir, Range clearRange,
                 int ungappedFullLength) {
             delegate.addRead(readId, offset, clearRange, validBases, dir, ungappedFullLength);
             return this;
         }

        /**
        * {@inheritDoc}
        */
        @Override
        public ContigBuilder<AsmPlacedRead, AsmContig> addAllReads(
                Iterable<AsmPlacedRead> reads) {
            delegate.addAllReads(reads);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public Collection<? extends PlacedReadBuilder<AsmPlacedRead>> getAllPlacedReadBuilders() {
           
            return delegate.getAllPlacedReadBuilders();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedReadBuilder<AsmPlacedRead> getPlacedReadBuilder(String readId) {
            return delegate.getPlacedReadBuilder(readId);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void removeRead(String readId) {
            delegate.removeRead(readId);            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public NucleotideSequenceBuilder getConsensusBuilder() {
            return delegate.getConsensusBuilder();
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public AsmContig build() {
            
            return new DefaultAsmContig(delegate.build(), isDegenerate);
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
