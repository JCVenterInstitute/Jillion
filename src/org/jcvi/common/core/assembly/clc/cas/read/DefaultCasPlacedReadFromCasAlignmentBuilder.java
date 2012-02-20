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
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.clc.cas.read;

import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignmentRegion;
import org.jcvi.common.core.assembly.clc.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;

public class DefaultCasPlacedReadFromCasAlignmentBuilder implements Builder<DefaultCasPlacedRead>{
    private final String readId;
    private long startOffset;
    private long validRangeStart;
    private long currentOffset=0;
    private boolean outsideValidRange=true;
    private final NucleotideSequence allBases;
    private NucleotideSequenceBuilder validBases = new NucleotideSequenceBuilder();
    private final Direction dir;
    private int numberOfGaps=0;
    private long referenceOffset;
    private final long fullUngappedLength;
    
    public DefaultCasPlacedReadFromCasAlignmentBuilder(String readId,
            NucleotideSequence fullRangeSequence, 
            boolean isReversed, long startOffset,
            Range traceTrimRange){
        if(fullRangeSequence ==null){
            throw new NullPointerException("null fullRangeSequence for id "+ readId);
        }
        try{
        this.readId = readId;
        this.startOffset = startOffset;
        this.referenceOffset = startOffset;
        this.fullUngappedLength = fullRangeSequence.getUngappedLength();
        NucleotideSequenceBuilder allBasesBuilder = new NucleotideSequenceBuilder(fullRangeSequence.asList(traceTrimRange));
        if(isReversed){
        	allBasesBuilder.reverseCompliment();
            validRangeStart = traceTrimRange ==null?0:AssemblyUtil.reverseComplimentValidRange(traceTrimRange, fullUngappedLength).getStart();
        }
        else{
            validRangeStart = traceTrimRange ==null?0:traceTrimRange.getStart();
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
    public DefaultCasPlacedReadFromCasAlignmentBuilder addAlignmentRegions(List<CasAlignmentRegion> regions,Sequence<Nucleotide> referenceBases){
        
        for(CasAlignmentRegion region : regions){
            addAlignmentRegion(region,referenceBases);
        }
        //validBases = NucleotideGlyph.convertToUngapped(validBases);
        return this;
    }
    private void addAlignmentRegion(CasAlignmentRegion region,Sequence<Nucleotide> referenceBases){
        CasAlignmentRegionType type =region.getType();
        
        if(outsideValidRange){
            if(type ==CasAlignmentRegionType.INSERT){
                validRangeStart+=region.getLength();
             
              
                currentOffset+=region.getLength();
                return;
            }           
            outsideValidRange=false;
        }
        
        
        for(long i=0; i< region.getLength();i++){
            if(type != CasAlignmentRegionType.INSERT){
                //add any extra gaps we added to the reference
            	//reference should not have any initial
            	//gaps so any gaps we see we put there during
            	//the 1st pass to build a gapped alignment.
                while(referenceOffset < referenceBases.getLength() && referenceBases.get((int)(referenceOffset)).isGap()){
                    validBases.append(Nucleotide.Gap);
                    referenceOffset++;
                    numberOfGaps++;
                }
            }
            if(type == CasAlignmentRegionType.DELETION){
                validBases.append(Nucleotide.Gap);
                numberOfGaps++;
                referenceOffset++;
            }
            else{      
                validBases.append(allBases.get((int)(currentOffset+i)));
                
                referenceOffset++;
            }
            
        }
        if(type != CasAlignmentRegionType.DELETION){
            currentOffset+=region.getLength();
        }
    }
  
    public String validBases(){
        return validBases.toString();
    }
    @Override
    public DefaultCasPlacedRead build() {
        Range validRange = Range.buildRangeOfLength(0, validBases.getLength()-numberOfGaps).shiftRight(validRangeStart).convertRange(CoordinateSystem.RESIDUE_BASED);
        if(dir==Direction.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(validRange, fullUngappedLength);
        }
        Read<NucleotideSequence> read = new DefaultRead<NucleotideSequence>(readId,
        		validBases.build());
        return new DefaultCasPlacedRead(read, startOffset, validRange, dir,(int)fullUngappedLength);
    }

}
