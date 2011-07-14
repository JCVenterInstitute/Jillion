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
 * Created on Feb 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.jcvi.Range;
import org.jcvi.glyph.Sequence;

public final class DefaultReferenceEncodedNucleotideSequence extends AbstractNucleotideSequence implements ReferenceEncodedNucleotideSequence{

    private final int[] gaps;
    private final int[] snpIndexes;
    private final NucleotideSequence snpValues;
    private NucleotideSequence beforeValues=null;
    private NucleotideSequence afterValues=null;
    private int overhangOffset=0;
    private final int length;
    private final int startOffset;
    private final Range validRange;
    private final NucleotideSequence reference;

    
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesBeforeReference() {
        return beforeValues==null?0 : (int)beforeValues.getLength();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfBasesAfterReference() {
        return afterValues==null?0 : (int)afterValues.getLength();
    }

    @Override
    public List<Integer> getSnpOffsets() {
        List<Integer> snps = new ArrayList<Integer>(snpIndexes.length);
        for(int i =0; i< snpIndexes.length; i++){
            snps.add(Integer.valueOf(snpIndexes[i]));
        }
        return snps;
    }

    public DefaultReferenceEncodedNucleotideSequence(NucleotideSequence reference,
            String toBeEncoded, int startOffset,Range validRange){
        List<Integer> tempGapList = new ArrayList<Integer>();     
        this.startOffset = startOffset;
        this.length = toBeEncoded.length();
        this.validRange = validRange;
        this.reference = reference;
        TreeMap<Integer, NucleotideGlyph> differentGlyphMap = new TreeMap<Integer, NucleotideGlyph>();
        populateFields(reference, toBeEncoded, startOffset, tempGapList,differentGlyphMap);
        gaps = convertToPrimitiveArray(tempGapList);
        snpIndexes = createSNPIndexes(differentGlyphMap);
        snpValues = createSNPValues(differentGlyphMap);
    }
    
    private NucleotideSequence createSNPValues(
            TreeMap<Integer, NucleotideGlyph> differentGlyphMap) {
        return new DefaultNucleotideSequence(differentGlyphMap.values());

    }
    private int[] createSNPIndexes(TreeMap<Integer, NucleotideGlyph> snpMap){
        int[]snps = new int[snpMap.size()];
        int i=0;
        for(Integer index : snpMap.keySet()){
            snps[i]=index.intValue();
            i++;
        }
        return snps;
    }
    private int[] convertToPrimitiveArray(List<Integer> list){
        int[] array = new int[list.size()];
        for(int i=0; i<list.size(); i++){
            array[i] = list.get(i).intValue();
        }
        return array;
    }
    private TreeMap<Integer, NucleotideGlyph> populateFields(Sequence<NucleotideGlyph> reference,
            String toBeEncoded, int startOffset, List<Integer> tempGapList,TreeMap<Integer, NucleotideGlyph> differentGlyphMap) {
        handleBeforeReference(toBeEncoded, startOffset);
        handleAfterReference(reference, toBeEncoded, startOffset);
        
        int startReferenceEncodingOffset = computeStartReferenceEncodingOffset();
        int endReferenceEncodingOffset = computeEndReferenceEncodingOffset(toBeEncoded);
        
        for(int i=startReferenceEncodingOffset; i<endReferenceEncodingOffset; i++){
            //get the corresponding index to this reference
            int referenceIndex = i + startOffset;
            NucleotideGlyph g = NucleotideGlyph.getGlyphFor(toBeEncoded.charAt(i));
            final NucleotideGlyph referenceGlyph = reference.get(referenceIndex);            
            
            final Integer indexAsInteger = Integer.valueOf(i);
            if(g.isGap()){
                tempGapList.add(indexAsInteger);
            }
            if(isDifferent(g, referenceGlyph)){
                    differentGlyphMap.put(indexAsInteger, g);
            }
        }
        return differentGlyphMap;
    }

    private int computeStartReferenceEncodingOffset(){
        return beforeValues==null?0: (int)beforeValues.getLength();
    }

    private int computeEndReferenceEncodingOffset(String toBeEncoded){
        return afterValues==null?toBeEncoded.length(): overhangOffset;
    }
    private void handleAfterReference(Sequence<NucleotideGlyph> reference,
            String toBeEncoded, int startOffset) {
        int lastOffsetOfSequence = toBeEncoded.length()+startOffset;
        if(lastOffsetOfSequence > reference.getLength()){
            int overhang = (int)(toBeEncoded.length()+startOffset - reference.getLength());
            overhangOffset = toBeEncoded.length()-overhang;
            afterValues = new DefaultNucleotideSequence(toBeEncoded.substring(overhangOffset));
        }
    }


    private void handleBeforeReference(String toBeEncoded, int startOffset) {
        if(startOffset<0){
            //handle before values
            beforeValues = new DefaultNucleotideSequence(toBeEncoded.substring(0, Math.abs(startOffset)));
        }
    }

    private boolean isDifferent(NucleotideGlyph g, final NucleotideGlyph referenceGlyph) {
        return g!=referenceGlyph;
    }

    @Override
    public List<NucleotideGlyph> decode() {
        List<NucleotideGlyph> result = new ArrayList<NucleotideGlyph>(length);
        for(int i=0; i< length; i++){
            result.add(get(i));
        }
        return result;
    }
    @Override
    public NucleotideGlyph get(int index) {
        if(isBeforeReference(index)){
            return beforeValues.get(index);
        }
        if(isAfterReference(index)){
            return afterValues.get(index-overhangOffset);
        }
        if(isGap(index)){
            return NucleotideGlyph.Gap;
        }
        int snpIndex =Arrays.binarySearch(snpIndexes, index);
        if(snpIndex>=0){
            return snpValues.get(snpIndex);
        }
        int referenceIndex = index+startOffset;
        return reference.get(referenceIndex);
    }


    private boolean isAfterReference(int index) {
        return afterValues !=null && index >=overhangOffset;
    }


    private boolean isBeforeReference(int index) {
        return beforeValues!=null && beforeValues.getLength()>index;
    }

    @Override
    public boolean isGap(int index) {
        for(int i=0; i<this.gaps.length; i++){
            if(gaps[i] == index){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public List<Integer> getGapIndexes() {
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<this.gaps.length; i++){
            result.add(this.gaps[i]);
        }
        return result;
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(snpIndexes);
        result = prime * result + snpValues.hashCode();
        result = prime * result + Arrays.hashCode(gaps);
        result = prime * result + length;
        result = prime * result + startOffset;
        result = prime * result
                + ((validRange == null) ? 0 : validRange.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultReferenceEncodedNucleotideSequence)) {
            return false;
        }
        DefaultReferenceEncodedNucleotideSequence other = (DefaultReferenceEncodedNucleotideSequence) obj;
        if (!Arrays.equals(snpIndexes,other.snpIndexes)) {
            return false;
        }
        if (snpValues == null) {
            if (other.snpValues != null) {
                return false;
            }
        } else if (!snpValues.equals(other.snpValues)) {
            return false;
        }
        if (gaps == null) {
            if (other.gaps != null) {
                return false;
            }
        } else if (!Arrays.equals(gaps,other.gaps)) {
            return false;
        }
        if (length != other.length) {
            return false;
        }
        if (startOffset != other.startOffset) {
            return false;
        }
        if (validRange == null) {
            if (other.validRange != null) {
                return false;
            }
        } else if (!validRange.equals(other.validRange)) {
            return false;
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfGaps() {
         return gaps.length;
     }
    
    

    
}
