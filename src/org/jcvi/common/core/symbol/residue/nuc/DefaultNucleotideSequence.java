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
 * Created on Mar 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.Sequence;
/**
 * {@code DefaultNucleotideSequence} is the default
 * implementation of a {@link NucleotideSequence}.  Internally,
 * each {@link Nucleotide} is encoded as 2 bits.
 * @author dkatzel
 *
 *
 */
public class DefaultNucleotideSequence extends AbstractNucleotideSequence{
    private final int[] gaps;
    private final Sequence<Nucleotide> encodedBasecalls;
    
    public static DefaultNucleotideSequence create(String nucleotides){
        return new DefaultNucleotideSequence(Nucleotides.parse(nucleotides));
    }
    public static DefaultNucleotideSequence create(char[] nucleotides){
        return new DefaultNucleotideSequence(Nucleotides.parse(nucleotides));
    }
    
    public static DefaultNucleotideSequence create(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides);
    }
    public static DefaultNucleotideSequence createGappy(Collection<Nucleotide> nucleotides){
        return new DefaultNucleotideSequence(nucleotides, DefaultNucleotideGlyphCodec.INSTANCE);
    }
    
    public DefaultNucleotideSequence(Collection<Nucleotide> nucleotides){
        this(nucleotides,NucleotideCodecs.getNucleotideCodecFor(nucleotides));
   
    }
    public DefaultNucleotideSequence(Collection<Nucleotide> nucleotides,NucleotideCodec codec ){
        this.gaps = computeGapIndexes(nucleotides);
        this.encodedBasecalls = new EncodedSequence<Nucleotide>(codec,nucleotides);
   
    }
    
    public DefaultNucleotideSequence(String basecalls){
        this(Nucleotides.parse(basecalls));
    }
    public DefaultNucleotideSequence(char[] basecalls){
        this(Nucleotides.parse(basecalls));
    }
    private int[] computeGapIndexes(Collection<Nucleotide> glyphs) {
       List<Integer> gaps = new ArrayList<Integer>();
       int i=0;
        for(Nucleotide glyph :glyphs){
            if(glyph.isGap()){
                gaps.add(Integer.valueOf(i));
            }
            i++;
        }
        int[] array = new int[gaps.size()];
        for(int j=0; j<gaps.size(); j++){
            array[j] = gaps.get(j).intValue();
        }
        return array;
    }
    
    @Override
    public List<Integer> getGapOffsets() {
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<this.gaps.length; i++){
            result.add(this.gaps[i]);
        }
        return result;
    }

    @Override
    public List<Nucleotide> asList() {
        return encodedBasecalls.asList();
    }

    @Override
    public Nucleotide get(int index) {
        if(isGap(index)){
            return Nucleotide.Gap;
        }
        return encodedBasecalls.get(index);
    }

    @Override
    public long getLength() {
        return encodedBasecalls.getLength();
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + encodedBasecalls.asList().hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof DefaultNucleotideSequence)){
            return false;
        }
        DefaultNucleotideSequence other = (DefaultNucleotideSequence) obj;
       return encodedBasecalls.asList().equals(other.encodedBasecalls.asList());
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder((int)encodedBasecalls.getLength());
        for(Nucleotide base : this){
            result.append(base);
        }
        return result.toString();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps() {
        return gaps.length;
    }

    
}
