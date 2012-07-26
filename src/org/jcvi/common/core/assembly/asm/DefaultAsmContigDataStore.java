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

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.asm.AsmVisitor.NestedContigMessageTypes;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.seq.read.trace.frg.FragmentDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * @author dkatzel
 *
 *
 */
public final class DefaultAsmContigDataStore implements AsmContigDataStore{
    private final DataStore<AsmContig> delegate;
    
    public static AsmContigDataStoreBuilder createBuilder(FragmentDataStore frgDataStore){
        return new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.ALL);
    }
    public static AsmContigDataStoreBuilder createDegenerateBuilder(FragmentDataStore frgDataStore){
        return new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.DEGENERATE_ONLY);
    }
    public static AsmContigDataStoreBuilder createPlacedBuilder(FragmentDataStore frgDataStore){
        return new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.PLACED);
    }
    
    public static AsmContigDataStore createDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.ALL);
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    public static AsmContigDataStore createDegenerateDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.DEGENERATE_ONLY);
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    public static AsmContigDataStore createPlacedDataStore(File asmFile, FragmentDataStore frgDataStore) throws IOException{
        AsmContigDataStoreBuilder builder =  new DefaultAsmContigDataStoreBuilder(frgDataStore,IncludeType.PLACED);
        AsmParser.parseAsm(asmFile, builder);
        return builder.build();
    }
    
    
    private enum IncludeType{
        ALL{
            @Override
            boolean include(boolean isDegenerate) {
                return true;
            }            
        },
        DEGENERATE_ONLY{
            @Override
            boolean include(boolean isDegenerate) {
                return isDegenerate;
            }            
        },
        PLACED{
            @Override
            boolean include(boolean isDegenerate) {
                return !isDegenerate;
            }            
        }
        ;
        
        abstract boolean include(boolean isDegenerate);
    }
    private DefaultAsmContigDataStore(DataStore<AsmContig> delegate) {
        this.delegate = delegate;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return delegate.idIterator();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AsmContig get(String id) throws DataStoreException {
        return delegate.get(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return delegate.getNumberOfRecords();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return delegate.isClosed();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void close() throws IOException {
        delegate.close();
        
    }

    /**
    * {@inheritDoc}
     * @throws DataStoreException 
    */
    @Override
    public StreamingIterator<AsmContig> iterator() throws DataStoreException {
        return delegate.iterator();
    }
    
    private static class DefaultAsmContigDataStoreBuilder extends AbstractAsmVisitor implements AsmContigDataStoreBuilder{
        private AsmContigBuilder currentBuilder=null;
        private final FragmentDataStore frgDataStore;
        private final Map<String,AsmContig> asmMap = new HashMap<String, AsmContig>();
        private final Map<String, Range> clearRanges = new HashMap<String, Range>();
        private final IncludeType includeType;
        public DefaultAsmContigDataStoreBuilder(FragmentDataStore frgDataStore,IncludeType includeType) {
            this.frgDataStore = frgDataStore;
            this.includeType = includeType;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitRead(String externalId, long internalId,
                MateStatus mateStatus, boolean isSingleton, Range clearRange) {
            clearRanges.put(externalId, clearRange);
        }

        /**
        * {@inheritDoc}
        * Returns an empty set if this contig is a degenerate
        * and we don't want degenerates in this datastore;
        * returns a set containing just {@link NestedContigMessageTypes#READ_MAPPING}
        * otherwise.
        */
        @Override
        public Set<NestedContigMessageTypes> visitContig(String externalId, long internalId,
                boolean isDegenerate, NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads,
                int numberOfUnitigs, int numberOfVariants) {
            if(includeType.include(isDegenerate)){
                currentBuilder =  DefaultAsmContig.createBuilder(externalId, consensusSequence,isDegenerate);
                return EnumSet.of(NestedContigMessageTypes.READ_MAPPING);
            }
            return EnumSet.noneOf(NestedContigMessageTypes.class);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public boolean visitUnitig(String externalId, long internalId,
                float aStat, float measureOfPolymorphism, UnitigStatus status,
                NucleotideSequence consensusSequence,
                QualitySequence consensusQualities, int numberOfReads) {
            
            
            return false;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitEndOfContig() {
            if(currentBuilder !=null){
                asmMap.put(currentBuilder.getContigId(),currentBuilder.build());
                currentBuilder=null;
            }
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public void visitReadLayout(char readType, String externalReadId,
                DirectedRange readRange, List<Integer> gapOffsets) {
            if(currentBuilder !=null){
                //in contig
                try {
                    NucleotideSequence fullLengthSequence = frgDataStore.get(externalReadId).getNucleotideSequence();
                    Range clearRange = clearRanges.get(externalReadId);
                    if(clearRange==null){
                        throw new IllegalStateException("do not have clear range information for read "+ externalReadId);
                    }
                   
                    NucleotideSequenceBuilder validBases = new NucleotideSequenceBuilder(fullLengthSequence.asList(clearRange));
                    if(readRange.getDirection() == Direction.REVERSE){
                        validBases.reverseComplement();
                    }
                    String gappedValidBases = AsmUtil.computeGappedSequence(
                            validBases.asList(), gapOffsets);
                    currentBuilder.addRead(externalReadId, gappedValidBases,
                            (int)readRange.asRange().getBegin(),readRange.getDirection(),
                            clearRange, 
                            (int)fullLengthSequence.getLength(),
                            false);
                } catch (DataStoreException e) {
                    throw new IllegalStateException("error getting read id "+ externalReadId +
                            " from frg file",e);
                }
            }
        }
        @Override
        public AsmContigDataStore build(){
            clearRanges.clear();
            DataStore<AsmContig> datastore = MapDataStoreAdapter.adapt(asmMap);
            return new DefaultAsmContigDataStore(datastore);
        }

    }

}
