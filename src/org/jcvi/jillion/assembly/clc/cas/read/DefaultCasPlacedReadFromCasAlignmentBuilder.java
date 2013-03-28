/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas.read;

import java.util.List;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.util.Builder;

public class DefaultCasPlacedReadFromCasAlignmentBuilder implements Builder<DefaultCasPlacedRead>{
    private final String readId;
    private long startOffset;
    private long validRangeStart;
    private long currentOffset=0;
    private boolean outsideValidRange=true;
    private final NucleotideSequence allBases;
    private final NucleotideSequenceBuilder gappedSequenceBuilder = new NucleotideSequenceBuilder();
    private final Direction dir;
    private long referenceOffset;
    private final long fullUngappedLength;
    private final  NucleotideSequence gappedReference;
    public DefaultCasPlacedReadFromCasAlignmentBuilder(String readId,
    		 NucleotideSequence gappedReference,
            NucleotideSequence fullRangeSequence, 
            boolean isReversed, long startOffset,
            Range traceTrimRange){
        if(fullRangeSequence ==null){
            throw new NullPointerException("null fullRangeSequence for id "+ readId);
        }
        if(gappedReference ==null){
            throw new NullPointerException("null gappedReference for id "+ readId);
        }
        this.gappedReference = gappedReference;
        try{
        this.readId = readId;
        this.startOffset = startOffset;
        this.referenceOffset = startOffset;
        this.fullUngappedLength = fullRangeSequence.getUngappedLength();
        NucleotideSequenceBuilder allBasesBuilder = new NucleotideSequenceBuilder(fullRangeSequence);
        if(traceTrimRange!=null){
        	allBasesBuilder.trim(traceTrimRange);
        }
        													
        if(isReversed){
        	allBasesBuilder.reverseComplement();
            validRangeStart = traceTrimRange ==null?0:AssemblyUtil.reverseComplementValidRange(traceTrimRange, fullUngappedLength).getBegin();
        }
        else{
            validRangeStart = traceTrimRange ==null?0:traceTrimRange.getBegin();
        }
        allBases = allBasesBuilder.build();
        dir = isReversed? Direction.REVERSE: Direction.FORWARD;
    }catch(Exception e){
        throw new IllegalStateException("error building alignment for read "+ readId,e);
    }
       // dir= SequenceDirection.FORWARD;
        
    }
    public DefaultCasPlacedReadFromCasAlignmentBuilder startOfset(long newStartOffset){
        this.startOffset = newStartOffset;
        
        return this;
    }
    public long startOffset(){
        return startOffset;
    }
    public DefaultCasPlacedReadFromCasAlignmentBuilder addAlignmentRegions(List<CasAlignmentRegion> regions, NucleotideSequence referenceBases){
        
        for(CasAlignmentRegion region : regions){
            addAlignmentRegion(region,referenceBases);
        }
        return this;
    }
    private void addAlignmentRegion(CasAlignmentRegion region,NucleotideSequence gappedReference){
        CasAlignmentRegionType type =region.getType();
        
        if(outsideValidRange){
            if(type ==CasAlignmentRegionType.INSERT){
                validRangeStart+=region.getLength();
             
              
                currentOffset+=region.getLength();
                return;
            }           
            outsideValidRange=false;
        }
        
        long allBasesLength = allBases.getUngappedLength();
        if(currentOffset + region.getLength() > allBasesLength){
        	throw new IllegalStateException(
        			String.format("alignment region %s extends beyond read; (current offset = %d total read length = %d)", 
        					region, currentOffset,allBasesLength));
        }
        for(long i=0; i< region.getLength();i++){
            if(type != CasAlignmentRegionType.INSERT){
                //add any extra gaps we added to the reference
            	//reference should not have any initial
            	//gaps so any gaps we see we put there during
            	//the 1st pass to build a gapped alignment.
                while(referenceOffset < gappedReference.getLength() && gappedReference.get((int)(referenceOffset)).isGap()){
                    gappedSequenceBuilder.append(Nucleotide.Gap);
                    referenceOffset++;
                }
            }
            if(type == CasAlignmentRegionType.DELETION){
                gappedSequenceBuilder.append(Nucleotide.Gap);
                referenceOffset++;
            }
            else{      
                gappedSequenceBuilder.append(allBases.get((int)(currentOffset+i)));
                referenceOffset++;
            }
            
        }//end for
        if(type != CasAlignmentRegionType.DELETION){
            currentOffset+=region.getLength();
        }
    }
  
    public String validBases(){
        return gappedSequenceBuilder.toString();
    }
    @Override
    public DefaultCasPlacedRead build() {
        Range validRange = new Range.Builder(gappedSequenceBuilder.getUngappedLength())
        					.shift(validRangeStart)
        					.build();
        if(dir==Direction.REVERSE){
            validRange = AssemblyUtil.reverseComplementValidRange(validRange, fullUngappedLength);
        }
        
        ReferenceMappedNucleotideSequence sequence = gappedSequenceBuilder
									.setReferenceHint(gappedReference, (int)startOffset)
									.buildReferenceEncodedNucleotideSequence();
        
        return new DefaultCasPlacedRead(readId, sequence, startOffset, validRange, dir,(int)fullUngappedLength);
    }

}
