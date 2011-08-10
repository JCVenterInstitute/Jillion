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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.contig.AbstractContig;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotides;

public class  DefaultAceContig extends AbstractContig<AcePlacedRead> implements AceContig{

    

    private DefaultAceContig(String id, NucleotideSequence consensus,
            Set<AcePlacedRead> reads) {
        super(id, consensus, reads);
    }

    public static class Builder{
        private NucleotideSequence fullConsensus;
        private String contigId;
        private CoordinateSystem adjustedContigIdCoordinateSystem=null;
        
        private List<DefaultAcePlacedRead.Builder> aceReadBuilders = new ArrayList<DefaultAcePlacedRead.Builder>();
        private int contigLeft= -1;
        private int contigRight = -1;
        
        public Builder(String contigId, String fullConsensus){
           this(contigId,
        		   DefaultNucleotideSequence.create(
                    Nucleotides.parse(ConsedUtil.convertAceGapsToContigGaps(fullConsensus)))
            );
        }
        public Builder(String contigId, List<Nucleotide> fullConsensus){
            this(contigId,
                    DefaultNucleotideSequence.create(fullConsensus)
             );
         }
        public Builder(String contigId, NucleotideSequence fullConsensus){
        	this.fullConsensus = fullConsensus;
        	 this.contigId = contigId;
        }
        
        public Builder adjustContigIdToReflectCoordinates(CoordinateSystem coordinateSystem){
            adjustedContigIdCoordinateSystem = coordinateSystem;
            return this;
        }
        public Builder setContigId(String contigId){
            this.contigId = contigId;
            return this;
        }
        public String getContigId() {
            return contigId;
        }
        public int numberOfReads(){
            return aceReadBuilders.size();
        }
        
        public Builder addRead(AcePlacedRead acePlacedRead) {
         return addRead(acePlacedRead.getId(),
        		 Nucleotides.asString(acePlacedRead.getNucleotideSequence().asList()),
        		 (int)acePlacedRead.getStart(),
        		 acePlacedRead.getDirection(),
        		 acePlacedRead.getValidRange(),
        		 acePlacedRead.getPhdInfo(),
        		 acePlacedRead.getUngappedFullLength());
        }
        
        public Builder addAllReads(Iterable<AcePlacedRead> reads){
            for(AcePlacedRead read : reads){
                addRead(read);
            }
            return this;
        }
    	
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
                
                
                aceReadBuilders.add(aceReadBuilder);
            
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
            final int endOfNewRead = offset+ validBases.length();
            if(endOfNewRead <= fullConsensus.getLength() && (contigRight ==-1 || endOfNewRead > contigRight)){
                contigRight = endOfNewRead ;
            }
        }
        private void adjustContigLeft(int offset) {
            
            if(contigLeft ==-1 || offset <contigLeft){
                contigLeft = offset;
            }
        }
        public DefaultAceContig build(){
            Set<AcePlacedRead> placedReads = new HashSet<AcePlacedRead>(aceReadBuilders.size()+1,1F);
            
            if(numberOfReads()==0){
                //force empty contig if no reads...
                return new DefaultAceContig(contigId, new DefaultNucleotideSequence(""),placedReads);
            }
            
            List<Nucleotide> updatedConsensus = updateConsensus(fullConsensus.asList());
            //contig left (and right) might be beyond consensus depending on how
            //trimmed the data is and what assembly/consensus caller is used.
            //force contig left and right to be within the called consensus
            //BCISD-211
            contigLeft = Math.max(contigLeft, 0);
            contigRight = Math.min(contigRight,(int)fullConsensus.getLength());
            //here only include the gapped valid range consensus bases
            //throw away the rest            
            NucleotideSequence validConsensus = DefaultNucleotideSequence.create(updatedConsensus.subList(contigLeft, contigRight));
            for(DefaultAcePlacedRead.Builder aceReadBuilder : aceReadBuilders){
                int newOffset = aceReadBuilder.offset() - contigLeft;
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
            aceReadBuilders.clear();
            fullConsensus = null;
            return new DefaultAceContig(newContigId, validConsensus,placedReads);
        }
        
        protected List<Nucleotide> updateConsensus(List<Nucleotide> validConsensusGlyphs){
            return validConsensusGlyphs;
        }
    }
    
}
