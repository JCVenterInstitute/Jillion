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
 * Created on Feb 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.contig.AbstractContig;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

public class  DefaultAceContig extends AbstractContig<AcePlacedRead> implements AceContig{

    

    private DefaultAceContig(String id, NucleotideSequence consensus,
            Set<AcePlacedRead> reads) {
        super(id, consensus, reads);
    }

    public static class Builder implements AceContigBuilder{
        private NucleotideSequence fullConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private CoordinateSystem adjustedContigIdCoordinateSystem=null;
        private final Map<String, DefaultAcePlacedRead.Builder>aceReadBuilderMap = new HashMap<String, DefaultAcePlacedRead.Builder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        private boolean built=false;
        public Builder(String contigId, String fullConsensus){
           this(contigId,                   
                    Nucleotides.parse(ConsedUtil.convertAceGapsToContigGaps(fullConsensus))
            );
        }
        public Builder(String contigId, List<Nucleotide> fullConsensus){
            this(contigId,
                    NucleotideSequenceFactory.create(fullConsensus)
             );
         }
        public Builder(String contigId, NucleotideSequence fullConsensus){
        	this.fullConsensus = fullConsensus;
        	 this.contigId = contigId;
        	 this.mutableConsensus = new NucleotideSequenceBuilder(fullConsensus);
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder adjustContigIdToReflectCoordinates(CoordinateSystem coordinateSystem){
            adjustedContigIdCoordinateSystem = coordinateSystem;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setContigId(String contigId){
            this.contigId = contigId;
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
        public int numberOfReads(){
            return aceReadBuilderMap.size();
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder addRead(AcePlacedRead acePlacedRead) {
         return addRead(acePlacedRead.getId(),
        		 Nucleotides.asString(acePlacedRead.getNucleotideSequence().asList()),
        		 (int)acePlacedRead.getStart(),
        		 acePlacedRead.getDirection(),
        		 acePlacedRead.getValidRange(),
        		 acePlacedRead.getPhdInfo(),
        		 acePlacedRead.getUngappedFullLength());
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder addAllReads(Iterable<AcePlacedRead> reads){
            for(AcePlacedRead read : reads){
                addRead(read);
            }
            return this;
        }
    	/**
        * {@inheritDoc}
        */
    	@Override
        public Collection<DefaultAcePlacedRead.Builder> getAllAcePlacedReadBuilders(){
    	    return aceReadBuilderMap.values();
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAcePlacedRead.Builder getAcePlacedReadBuilder(String readId){
            return aceReadBuilderMap.get(readId);
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder addRead(String readId, String validBases, int offset,
                Direction dir, Range clearRange,PhdInfo phdInfo,int ungappedFullLength) {
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            int correctedOffset = Math.max(0,offset);
            adjustContigLeftAndRight(validBases, correctedOffset);
                DefaultAcePlacedRead.Builder aceReadBuilder = createNewAceReadBuilder(readId, validBases, correctedOffset, dir, 
                        clearRange,phdInfo,ungappedFullLength);
                
                
                aceReadBuilderMap.put(readId,aceReadBuilder);
            
            return this;
        }
        private DefaultAcePlacedRead.Builder createNewAceReadBuilder(
                String readId, String validBases, int offset,
                Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength) {
            return new DefaultAcePlacedRead.Builder(
                    fullConsensus,readId,
                    ConsedUtil.convertAceGapsToContigGaps(validBases),
                    offset,dir,clearRange,phdInfo,ungappedFullLength);
        }
        private void adjustContigLeftAndRight(String validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private void adjustContigRight(String validBases, int offset) {
            final int endOfNewRead = offset+ validBases.length()-1;
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
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
        public synchronized DefaultAceContig build(){
             if(built){
                 throw new IllegalStateException("this contig has already been built");
             }
             built=true;
            if(numberOfReads()==0){
                //force empty contig if no reads...
                return new DefaultAceContig(contigId, NucleotideSequenceFactory.create(""),Collections.<AcePlacedRead>emptySet());
            }
            finalizeContig();
            Set<AcePlacedRead> placedReads = new HashSet<AcePlacedRead>(aceReadBuilderMap.size()+1,1F);
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211            
            contigLeft = Math.max(contigLeft, 0);
            contigRight = Math.min(contigRight,(int)mutableConsensus.getLength()-1);
            //here only include the gapped valid range consensus bases
            //throw away the rest            
            NucleotideSequence validConsensus = NucleotideSequenceFactory.create(mutableConsensus.asList(Range.buildRange(contigLeft, contigRight)));
            for(DefaultAcePlacedRead.Builder aceReadBuilder : aceReadBuilderMap.values()){
                int newOffset = (int)aceReadBuilder.getStart() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            }   
            final String newContigId;
            if(adjustedContigIdCoordinateSystem !=null){
                Range ungappedContigRange = Range.buildRange(
                            fullConsensus.getUngappedOffsetFor(contigLeft),
                            fullConsensus.getUngappedOffsetFor(contigRight))
                        .convertRange(adjustedContigIdCoordinateSystem);
                 //contig left and right are in 0 based use
                newContigId = String.format("%s_%d_%d",contigId,
                        ungappedContigRange.getLocalStart(),
                        ungappedContigRange.getLocalEnd());
            }else{
                newContigId = contigId;
            }
            aceReadBuilderMap.clear();
            fullConsensus = null;
            return new DefaultAceContig(newContigId, validConsensus,placedReads);
        }
        
        /**
         * This method will be called inside of {@link #build()}
         * to let subclasses further manipulate the contig conensus
         * or underlying reads before the final immutable contig is built.
         * Implementors can assume that all the reads that are to be added
         * to this contig have already been added and no further
         * additional data will be inserted into this contig.
         */
        protected void finalizeContig() {
            // no-op
            
        }        
    }
    
}
