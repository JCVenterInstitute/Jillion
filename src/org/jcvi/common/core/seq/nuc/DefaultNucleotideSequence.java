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
package org.jcvi.common.core.seq.nuc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.common.core.seq.EncodedSequence;
import org.jcvi.common.core.seq.Sequence;

public class DefaultNucleotideSequence extends AbstractNucleotideSequence{
    private final int[] gaps;
    private final Range validRange;
    private final Sequence<NucleotideGlyph> encodedBasecalls;
    public DefaultNucleotideSequence(Collection<NucleotideGlyph> glyphs, Range validRange){
        this.validRange = validRange;
        this.gaps = computeGapIndexes(glyphs);
        this.encodedBasecalls = new EncodedSequence<NucleotideGlyph>(DefaultNucleotideGlyphCodec.getInstance(),glyphs);
   
    }
    public DefaultNucleotideSequence(Collection<NucleotideGlyph> glyphs){
        this(glyphs, Range.buildRange(0, glyphs.size()-1));
    }
    public DefaultNucleotideSequence(String basecalls, Range validRange){
        this(NucleotideGlyph.getGlyphsFor(basecalls), validRange);
    }
    public DefaultNucleotideSequence(char[] basecalls){
        this(NucleotideGlyph.getGlyphsFor(basecalls));
    }
    public DefaultNucleotideSequence(String basecalls){
        this(NucleotideGlyph.getGlyphsFor(basecalls));
    }
    private int[] computeGapIndexes(Collection<NucleotideGlyph> glyphs) {
       List<Integer> gaps = new ArrayList<Integer>();
       int i=0;
        for(NucleotideGlyph glyph :glyphs){
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
    public List<NucleotideGlyph> decode() {
        return encodedBasecalls.decode();
    }

    @Override
    public NucleotideGlyph get(int index) {
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
                + encodedBasecalls.decode().hashCode();
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
       return encodedBasecalls.decode().equals(other.encodedBasecalls.decode());
    }
    @Override
    public String toString() {
        return encodedBasecalls.decode().toString();
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps() {
        return gaps.length;
    }

    
}
