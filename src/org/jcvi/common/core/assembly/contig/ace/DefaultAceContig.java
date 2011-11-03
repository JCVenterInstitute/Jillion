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
import org.jcvi.common.core.assembly.contig.AbstractContig;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceFactory;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;
/**
 * {@code DefaultAceContig} is the default implementation of {@link AceContig}.
 * @author dkatzel
 *
 *
 */
public class  DefaultAceContig extends AbstractContig<AcePlacedRead> implements AceContig{

    private final boolean complimented;

    private DefaultAceContig(String id, NucleotideSequence consensus,
            Set<AcePlacedRead> reads,boolean complimented) {
        super(id, consensus, reads);
        this.complimented = complimented;
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static AceContigBuilder createBuilder(String contigId, String consensus){
        return new Builder(contigId, consensus);
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static AceContigBuilder createBuilder(String contigId, List<Nucleotide> consensus){
        return new Builder(contigId, consensus);
    }
    /**
     * Create a new {@link AceContigBuilder} for a contig with the given
     * contig id and starting with the given consensus.  Both the contig id
     * and the consensus can be changed by calling methods on the returned
     * builder.
     * @param contigId the initial contig id to use for this contig (may later be changed)
     * @param consensus the initial contig consensus for this contig (may be changed later)
     * @return a new {@link AceContigBuilder} instance; never null.
     * @throws NullPointerException if contigId or consensus are null.
     */
    public static AceContigBuilder createBuilder(String contigId, NucleotideSequence consensus){
        return new Builder(contigId, consensus);
    }
    
    
    
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isComplemented() {
        return complimented;
    }



    private static class Builder implements AceContigBuilder{
        private NucleotideSequence fullConsensus;
        private final NucleotideSequenceBuilder mutableConsensus;
        private String contigId;
        private final Map<String, AcePlacedReadBuilder>aceReadBuilderMap = new HashMap<String, AcePlacedReadBuilder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        private boolean built=false;
        private boolean complimented=false;
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
            if(contigId ==null){
                throw new NullPointerException("contig id can not be null");
            }
            if(fullConsensus ==null){
                throw new NullPointerException("consensus can not be null");
            }
        	this.fullConsensus = fullConsensus;
        	 this.contigId = contigId;
        	 this.mutableConsensus = new NucleotideSequenceBuilder(fullConsensus);
        }
        /**
         * Set this contig as being complimented.
         * @param complimented
         * @return this
         */
        @Override
        public Builder setComplimented(boolean complimented){
            this.complimented = complimented;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public Builder setContigId(String contigId){
            if(contigId==null){
                throw new NullPointerException("contig id can not be null");
            }
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
        public Collection<AcePlacedReadBuilder> getAllPlacedReadBuilders(){
    	    return aceReadBuilderMap.values();
    	}
        /**
        * {@inheritDoc}
        */
        @Override
        public AcePlacedReadBuilder getPlacedReadBuilder(String readId){
            return aceReadBuilderMap.get(readId);
        }
        
        
        /**
        * {@inheritDoc}
        */
        @Override
        public void removeRead(String readId) {
            if(readId==null){
                throw new NullPointerException("read id can not be null");
            }
            aceReadBuilderMap.remove(readId);
            
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
            AcePlacedReadBuilder aceReadBuilder = createNewAceReadBuilder(readId, validBases, correctedOffset, dir, 
                        clearRange,phdInfo,ungappedFullLength);
                
                
                aceReadBuilderMap.put(readId,aceReadBuilder);
            
            return this;
        }
        private AcePlacedReadBuilder createNewAceReadBuilder(
                String readId, String validBases, int offset,
                Direction dir, Range clearRange, PhdInfo phdInfo,int ungappedFullLength) {
            return DefaultAcePlacedRead.createBuilder(
                    fullConsensus,readId,
                    ConsedUtil.convertAceGapsToContigGaps(validBases),
                    offset,dir,clearRange,phdInfo,ungappedFullLength);
        }
        private synchronized void adjustContigLeftAndRight(String validBases, int offset) {
            adjustContigLeft(offset);
            adjustContigRight(validBases, offset);
        }
        private synchronized void adjustContigRight(String validBases, int offset) {
            final int endOfNewRead = offset+ validBases.length()-1;
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private synchronized void adjustContigLeft(int offset) {
            
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
                return new DefaultAceContig(contigId, NucleotideSequenceFactory.create(""),Collections.<AcePlacedRead>emptySet(),complimented);
            }
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
            for(AcePlacedReadBuilder aceReadBuilder : aceReadBuilderMap.values()){
                int newOffset = (int)aceReadBuilder.getStart() - contigLeft;
                aceReadBuilder.reference(validConsensus,newOffset);
                placedReads.add(aceReadBuilder.build());                
            } 
            aceReadBuilderMap.clear();
            fullConsensus = null;
            return new DefaultAceContig(contigId, validConsensus,placedReads,complimented);
        }
    }
    
}
