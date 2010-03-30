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
package org.jcvi.assembly.cas.read;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Builder;
import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegion;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.DefaultNucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

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
            EncodedGlyphs<NucleotideGlyph> fullRangeSequence, 
            boolean isReversed, long startOffset,
            Range traceTrimRange){
        if(fullRangeSequence ==null){
            throw new NullPointerException("null fullRangeSequence for id "+ readId);
        }
        try{
        this.readId = readId;
        this.startOffset = startOffset;
        this.referenceOffset = startOffset;
        this.fullUngappedLength = fullRangeSequence.getLength();
        
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
    public DefaultCasPlacedReadFromCasAlignmentBuilder addAlignmentRegions(List<CasAlignmentRegion> regions,EncodedGlyphs<NucleotideGlyph> referenceBases){
        
        for(CasAlignmentRegion region : regions){
            addAlignmentRegion(region,referenceBases);
        }
        //validBases = NucleotideGlyph.convertToUngapped(validBases);
        return this;
    }
    private void addAlignmentRegion(CasAlignmentRegion region,EncodedGlyphs<NucleotideGlyph> referenceBases){
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
        Read<NucleotideEncodedGlyphs> read = new DefaultRead(readId,
                        new DefaultNucleotideEncodedGlyphs(validBases,validRange));
        return new DefaultCasPlacedRead(read, startOffset, validRange, dir);
    }

}
