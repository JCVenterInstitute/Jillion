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
 * Created on Oct 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.ReadTrim;
import org.jcvi.sequence.ReadTrimUtil;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.sequence.TrimType;

public class QualityClassExtender<R extends PlacedRead> {

    public TrimmedPlacedRead<R> extend(NucleotideEncodedGlyphs reference, R placedRead,
            ReadTrim trimPoints, 
            EncodedGlyphs<NucleotideGlyph> fullRangeBasecalls){
        
        Range clv = trimPoints.getTrimRange(TrimType.CLV);
        Range currentRange = placedRead.getValidRange();
        int newLeft = extendLeft(reference, placedRead, fullRangeBasecalls, clv, currentRange);
        int newRight = extendRight(reference, placedRead, fullRangeBasecalls, clv, currentRange);
        
        final Range extendedTrimRange = Range.buildRange(newLeft, newRight).convertRange(CoordinateSystem.RESIDUE_BASED);
        if(extendedTrimRange.equals(currentRange)){
            return null;
        }
        return new DefaultTrimmedPlacedRead<R>(placedRead, extendedTrimRange);
    }
    
    protected int extendLeft(NucleotideEncodedGlyphs reference, R placedRead,
            EncodedGlyphs<NucleotideGlyph> fullRangeBasecalls, Range clv,
            Range currentRange) {
        int fullLength = (int)fullRangeBasecalls.getLength();
        List<NucleotideGlyph> basecallstoUse;
        Range validRangeToUse;
        Range clvToUse;
        final int increment;
        if(placedRead.getSequenceDirection().equals(SequenceDirection.FORWARD)){
            basecallstoUse = fullRangeBasecalls.decode();
            validRangeToUse = currentRange;
            clvToUse = clv;
            increment=-1;
        }else{
            basecallstoUse =NucleotideGlyph.reverseCompliment(fullRangeBasecalls.decode());
            validRangeToUse =AssemblyUtil.reverseComplimentValidRange(currentRange, fullLength);
            clvToUse =AssemblyUtil.reverseComplimentValidRange(clv, basecallstoUse.size());
            increment =1;
            
        }
        int fullRangeLeftIndex =AssemblyUtil.convertToUngappedFullRangeIndex(placedRead, fullLength, (int)validRangeToUse.getStart(),validRangeToUse);
        
        int newLeft = fullRangeLeftIndex;
        int basesTrimmed=1;
            for( ;(int)validRangeToUse.getStart() -basesTrimmed>=clvToUse.getStart() && (placedRead.getStart() -basesTrimmed) >=0; basesTrimmed++){
                newLeft+=increment;
                
                NucleotideGlyph basecall =basecallstoUse.get(newLeft);
                
                NucleotideGlyph consensusBasecall =reference.get((int)placedRead.getStart() -basesTrimmed);
                if(!consensusBasecall.equals(basecall)){
                    break;
                }
            }
        //we have moved 1 too far so pull back 1
        return (int)currentRange.getStart() -basesTrimmed+1;
    }
    
    protected int extendRight(NucleotideEncodedGlyphs reference, R placedRead,
            EncodedGlyphs<NucleotideGlyph> fullRangeBasecalls, Range clv,
            Range currentRange) {
        int fullLength = (int)fullRangeBasecalls.getLength();
        List<NucleotideGlyph> basecallstoUse;
        Range validRangeToUse;
        Range clvToUse;
        final int increment;
        if(placedRead.getSequenceDirection().equals(SequenceDirection.FORWARD)){
            basecallstoUse = fullRangeBasecalls.decode();
            validRangeToUse = currentRange;
            clvToUse = clv;
            increment=1;
        }else{
            basecallstoUse =NucleotideGlyph.reverseCompliment(fullRangeBasecalls.decode());
            validRangeToUse =AssemblyUtil.reverseComplimentValidRange(currentRange, fullLength);
            clvToUse =AssemblyUtil.reverseComplimentValidRange(clv, basecallstoUse.size());
            increment =-1;
            
        }
        int fullRangeRightIndex =AssemblyUtil.convertToUngappedFullRangeIndex(placedRead, fullLength, (int)validRangeToUse.getEnd(),validRangeToUse);
        
        int newRight = fullRangeRightIndex;
        int basesTrimmed=1;
            for( ;(int)validRangeToUse.getEnd() +basesTrimmed<=clvToUse.getEnd() && (newRight+increment) <basecallstoUse.size() && (newRight+increment) >=0; basesTrimmed++){
                newRight+=increment;
                
                NucleotideGlyph basecall =basecallstoUse.get(newRight);
                
                NucleotideGlyph consensusBasecall =reference.get((int)placedRead.getEnd() +basesTrimmed);
                if(!consensusBasecall.equals(basecall)){
                    break;
                }
            }
        //we have moved 1 too far so pull back 1
        return (int)currentRange.getEnd() +basesTrimmed-1;
    }
}
