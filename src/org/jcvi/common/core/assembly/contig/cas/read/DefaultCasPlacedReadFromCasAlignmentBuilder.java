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
package org.jcvi.common.core.assembly.contig.cas.read;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegion;
import org.jcvi.common.core.assembly.contig.cas.align.CasAlignmentRegionType;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.util.Builder;

public class DefaultCasPlacedReadFromCasAlignmentBuilder implements Builder<DefaultCasPlacedRead>{
    private final String readId;
    private long startOffset;
    private long validRangeStart;
    private long currentOffset=0;
    private boolean outsideValidRange=true;
    private final List<NucleotideGlyph> allBases;
    private List<NucleotideGlyph> validBases = new ArrayList<NucleotideGlyph>();
    private final SequenceDirection dir;
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
        
        if(isReversed){
            allBases = NucleotideGlyph.reverseCompliment(fullRangeSequence.decode(traceTrimRange));
            validRangeStart = traceTrimRange ==null?0:AssemblyUtil.reverseComplimentValidRange(traceTrimRange, fullUngappedLength).getStart();
        }
        else{
            allBases = fullRangeSequence.decode(traceTrimRange);
            validRangeStart = traceTrimRange ==null?0:traceTrimRange.getStart();
        }
        dir = isReversed? SequenceDirection.REVERSE: SequenceDirection.FORWARD;
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
    public DefaultCasPlacedReadFromCasAlignmentBuilder addAlignmentRegions(List<CasAlignmentRegion> regions,Sequence<NucleotideGlyph> referenceBases){
        
        for(CasAlignmentRegion region : regions){
            addAlignmentRegion(region,referenceBases);
        }
        //validBases = NucleotideGlyph.convertToUngapped(validBases);
        return this;
    }
    private void addAlignmentRegion(CasAlignmentRegion region,Sequence<NucleotideGlyph> referenceBases){
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
                
                while(referenceOffset < referenceBases.getLength() && referenceBases.get((int)(referenceOffset)).isGap()){
                    validBases.add(NucleotideGlyph.Gap);
                    referenceOffset++;
                    numberOfGaps++;
                }
            }
            if(type == CasAlignmentRegionType.DELETION){
                validBases.add(NucleotideGlyph.Gap);
                numberOfGaps++;
                referenceOffset++;
            }
            else{      
                validBases.add(allBases.get((int)(currentOffset+i)));
                
                referenceOffset++;
            }
            
        }
        if(type != CasAlignmentRegionType.DELETION){
            currentOffset+=region.getLength();
        }
        //referenceOffset +=region.getLength();
    }
  
    public String validBases(){
        return NucleotideGlyph.convertToString(validBases);
    }
    @Override
    public DefaultCasPlacedRead build() {
        Range validRange = Range.buildRangeOfLength(0, validBases.size()-numberOfGaps).shiftRight(validRangeStart).convertRange(CoordinateSystem.RESIDUE_BASED);
        if(dir==SequenceDirection.REVERSE){
            validRange = AssemblyUtil.reverseComplimentValidRange(validRange, fullUngappedLength);
        }
        Read<NucleotideSequence> read = new DefaultRead(readId,
                        new DefaultNucleotideSequence(validBases,validRange));
        return new DefaultCasPlacedRead(read, startOffset, validRange, dir,(int)fullUngappedLength);
    }

}
