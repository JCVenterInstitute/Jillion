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
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jcvi.Range;
import org.jcvi.glyph.nuc.AbstractEnocdedNucleotideGlyphs;
import org.jcvi.glyph.nuc.DefaultReferencedEncodedNucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;

public class SplitReferenceEncodedNucleotideGlyphs extends AbstractEnocdedNucleotideGlyphs implements ReferencedEncodedNucleotideGlyphs{

    private final ReferencedEncodedNucleotideGlyphs leftSplit, rightSplit;
    private final int splitIndex;
    private final List<Integer> snps;
    private final Range validRange;
    private final List<Integer> gaps;
    
    public SplitReferenceEncodedNucleotideGlyphs(NucleotideEncodedGlyphs reference,
            String gappedSequenceToEncode, int negativeStartOffset,Range validRange){
        if(negativeStartOffset >-1){
            throw new IllegalArgumentException("start offset must be < 0");
        }
        this.validRange = validRange;
        int lengthOfLeftSplit = -1 * negativeStartOffset;
        int leftStartOffset = (int)(reference.getLength()-lengthOfLeftSplit);
        
        splitIndex = lengthOfLeftSplit;
        final String leftConsensus = gappedSequenceToEncode.substring(0, splitIndex);
        leftSplit = new DefaultReferencedEncodedNucleotideGlyph(
                                                    reference, 
                                                    leftConsensus, 
                                                    leftStartOffset, 
                                                    validRange);
        
        rightSplit = new DefaultReferencedEncodedNucleotideGlyph(
                reference, 
                gappedSequenceToEncode.substring(splitIndex), 
                0, 
                validRange);
        
        snps = new ArrayList<Integer>();
        snps.addAll(leftSplit.getSnps());
        
        for(Integer snp : rightSplit.getSnps()){
            snps.add(Integer.valueOf(snp.intValue()+splitIndex));
        }
        gaps = new ArrayList<Integer>();
        gaps.addAll(leftSplit.getGapIndexes());
        for(int rightGapIndex : rightSplit.getGapIndexes()){
            gaps.add(splitIndex+rightGapIndex);
        }
    }
    
   

    @Override
    public Range getValidRange() {
        return validRange;
    }

    @Override
    public List<Integer> getGapIndexes() {
        return gaps;
    }

    @Override
    public boolean isGap(int index) {
        return gaps.contains(index);
    }

    @Override
    public List<NucleotideGlyph> decode() {
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>();
        result.addAll(leftSplit.decode());
        result.addAll(rightSplit.decode());
        return result;
    }

    @Override
    public NucleotideGlyph get(int index) {
        if(index < splitIndex){
            return leftSplit.get(index);
        }
        int rightIndex = index - splitIndex;
        return rightSplit.get(rightIndex);
    }

    @Override
    public long getLength() {
        return leftSplit.getLength() + rightSplit.getLength();
    }

    public ReferencedEncodedNucleotideGlyphs getLeftSplit() {
        return leftSplit;
    }

    public ReferencedEncodedNucleotideGlyphs getRightSplit() {
        return rightSplit;
    }

    public int getSplitIndex() {
        return splitIndex;
    }

    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfGaps() {
         return gaps.size();
     }



    /**
    * {@inheritDoc}
    */
    @Override
    public List<Integer> getSnps() {
        return Collections.unmodifiableList(snps);
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesBeforeReference() {
        // TODO Auto-generated method stub
        return 0;
    }



    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesAfterReference() {
        // TODO Auto-generated method stub
        return 0;
    }

}
