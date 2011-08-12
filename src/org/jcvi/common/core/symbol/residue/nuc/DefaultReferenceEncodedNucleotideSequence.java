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
package org.jcvi.common.core.symbol.residue.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.jcvi.common.core.symbol.Sequence;

public final class DefaultReferenceEncodedNucleotideSequence extends AbstractNucleotideSequence implements ReferenceEncodedNucleotideSequence{

   // private final int[] gaps;
   // private final int[] snpIndexes;
  //  private final NucleotideSequence snpValues;
    private NucleotideSequence beforeValues=null;
    private NucleotideSequence afterValues=null;
    private int overhangOffset=0;
    private final int length;
    private final int startOffset;
    private final NucleotideSequence reference;
    private final byte[] encodedSnpsInfo;
    
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
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        int size = buf.getInt();
        List<Integer> snps = new ArrayList<Integer>(size);
        for(int i=0; i<size; i++){
            snps.add(Integer.valueOf(buf.getInt()));
        }
        return snps;
    }

    public DefaultReferenceEncodedNucleotideSequence(NucleotideSequence reference,
            String toBeEncoded, int startOffset){
        List<Integer> tempGapList = new ArrayList<Integer>();     
        this.startOffset = startOffset;
        this.length = toBeEncoded.length();
        this.reference = reference;
        TreeMap<Integer, Nucleotide> differentGlyphMap = populateFields(reference, toBeEncoded, startOffset, tempGapList);
        
        int numSnps = differentGlyphMap.size();
        
       
        ByteBuffer buffer = ByteBuffer.allocate(4+5*numSnps);
        buffer.putInt(numSnps);
        for(Integer offset : differentGlyphMap.keySet()){
            buffer.putInt(offset);
        }
        for(Nucleotide n : differentGlyphMap.values()){
            buffer.put((byte)n.ordinal());
        }
        encodedSnpsInfo = buffer.array();
       /* gaps = convertToPrimitiveArray(tempGapList);
        snpIndexes = createSNPIndexes(differentGlyphMap);
        snpValues = createSNPValues(differentGlyphMap);
        */
    }
    
    private NucleotideSequence createSNPValues(
            TreeMap<Integer, Nucleotide> differentGlyphMap) {
        return DefaultNucleotideSequence.createGappy(differentGlyphMap.values());

    }
    private int[] createSNPIndexes(TreeMap<Integer, Nucleotide> snpMap){
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
    private TreeMap<Integer, Nucleotide> populateFields(Sequence<Nucleotide> reference,
            String toBeEncoded, int startOffset, List<Integer> tempGapList) {
        handleBeforeReference(toBeEncoded, startOffset);
        handleAfterReference(reference, toBeEncoded, startOffset);
        TreeMap<Integer, Nucleotide> differentGlyphMap = new TreeMap<Integer, Nucleotide>();
        
        int startReferenceEncodingOffset = computeStartReferenceEncodingOffset();
        int endReferenceEncodingOffset = computeEndReferenceEncodingOffset(toBeEncoded);
        
        for(int i=startReferenceEncodingOffset; i<endReferenceEncodingOffset; i++){
            //get the corresponding index to this reference
            int referenceIndex = i + startOffset;
            Nucleotide g = Nucleotide.parse(toBeEncoded.charAt(i));
            final Nucleotide referenceGlyph = reference.get(referenceIndex);            
            
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
    private void handleAfterReference(Sequence<Nucleotide> reference,
            String toBeEncoded, int startOffset) {
        int lastOffsetOfSequence = toBeEncoded.length()+startOffset;
        if(lastOffsetOfSequence > reference.getLength()){
            int overhang = (int)(toBeEncoded.length()+startOffset - reference.getLength());
            overhangOffset = toBeEncoded.length()-overhang;
            afterValues = DefaultNucleotideSequence.create(toBeEncoded.substring(overhangOffset));
        }
    }


    private void handleBeforeReference(String toBeEncoded, int startOffset) {
        if(startOffset<0){
            //handle before values
            beforeValues = DefaultNucleotideSequence.create(toBeEncoded.substring(0, Math.abs(startOffset)));
        }
    }

    private boolean isDifferent(Nucleotide g, final Nucleotide referenceGlyph) {
        return g!=referenceGlyph;
    }

    @Override
    public List<Nucleotide> asList() {
        List<Nucleotide> result = new ArrayList<Nucleotide>(length);
        for(int i=0; i< length; i++){
            result.add(get(i));
        }
        return result;
    }
    @Override
    public Nucleotide get(int index) {
        if(isBeforeReference(index)){
            return beforeValues.get(index);
        }
        if(isAfterReference(index)){
            return afterValues.get(index-overhangOffset);
        }
        
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        int size = buf.getInt();
        for(int i=0; i<size; i++){
            if(index ==buf.getInt()){
                return Nucleotide.values()[encodedSnpsInfo[4+size*4+i]];
            }
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
        return getGapOffsets().contains(Integer.valueOf(index));
        
    }
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public List<Integer> getGapOffsets() {
      //first, get gaps from our aligned section of the reference
        //we may have a snp in the gap location
        //so we need to check for that
       
        List<Integer> refGapOffsets = reference.getGapOffsets();
        List<Integer> gaps = new ArrayList<Integer>(refGapOffsets.size());
        for(Integer refGap : refGapOffsets){
            int adjustedCoordinate = refGap.intValue() - startOffset;
            if(adjustedCoordinate >=0 && adjustedCoordinate<length){
                gaps.add(Integer.valueOf(adjustedCoordinate));
            }
        }
        //now check our snps to see
        //1. if we have snp where the ref has a gap
        //2. if we have gap
        ByteBuffer buf = ByteBuffer.wrap(encodedSnpsInfo);
        int size = buf.getInt();
        List<Integer> snps = new ArrayList<Integer>(size);
        for(int i=0; i<size; i++){
            Integer snpOffset = Integer.valueOf(buf.getInt());
            //we have a snp where the ref has a gap
            //remove it from our list of gaps
            if(gaps.contains(snpOffset)){
                gaps.remove(snpOffset);
            }
            snps.add(snpOffset);
        }
        int i=0;
        while(buf.hasRemaining()){
            if(Nucleotide.values()[buf.get()] == Nucleotide.Gap){
                gaps.add(snps.get(i));
            }
            i++;
        }
        //sort gaps so they are in order
        //before this line, our gaps are in
        //sorted ref gaps
        //followed by sorted snps
        Collections.sort(gaps);
        return gaps;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(encodedSnpsInfo);
        result = prime * result + reference.hashCode();
        result = prime * result + length;
        result = prime * result + startOffset;
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
        if (!Arrays.equals(encodedSnpsInfo,other.encodedSnpsInfo)) {
            return false;
        }
        if(!reference.equals(other.reference)){
            return false;
        }
        if (length != other.length) {
            return false;
        }
        if (startOffset != other.startOffset) {
            return false;
        }
       
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public int getNumberOfGaps() {
         return getGapOffsets().size();
     }
    
    

    
}
