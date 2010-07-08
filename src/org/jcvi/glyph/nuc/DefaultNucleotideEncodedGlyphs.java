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
package org.jcvi.glyph.nuc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.EncodedGlyphs;

public class DefaultNucleotideEncodedGlyphs extends AbstractEnocdedNucleotideGlyphs{
    private final List<Integer> gaps;
    private final Range validRange;
    private final EncodedGlyphs<NucleotideGlyph> encodedBasecalls;
    public DefaultNucleotideEncodedGlyphs(List<NucleotideGlyph> glyphs, Range validRange){
        this.validRange = validRange;
        this.gaps = computeGapIndexes(glyphs);
        this.encodedBasecalls = new DefaultEncodedGlyphs<NucleotideGlyph>(DefaultNucleotideGlyphCodec.getInstance(),glyphs);
   
    }
    public DefaultNucleotideEncodedGlyphs(List<NucleotideGlyph> glyphs){
        this(glyphs, Range.buildRange(0, glyphs.size()-1));
    }
    public DefaultNucleotideEncodedGlyphs(String basecalls, Range validRange){
        this(NucleotideGlyph.getGlyphsFor(basecalls), validRange);
    }
    public DefaultNucleotideEncodedGlyphs(char[] basecalls){
        this(NucleotideGlyph.getGlyphsFor(basecalls));
    }
    public DefaultNucleotideEncodedGlyphs(String basecalls){
        this(NucleotideGlyph.getGlyphsFor(basecalls));
    }
    private List<Integer> computeGapIndexes(List<NucleotideGlyph> glyphs) {
       List<Integer> gaps = new ArrayList<Integer>();
        for(int i=0; i< glyphs.size(); i++){
           NucleotideGlyph glyph = glyphs.get(i);
            if(glyph.isGap()){
                gaps.add(Integer.valueOf(i));
            }
        }
        return Collections.unmodifiableList(gaps);
    }
    
    @Override
    public List<Integer> getGapIndexes() {
        return gaps;
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
        return gaps.contains(Integer.valueOf(index));
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
        if (!(obj instanceof DefaultNucleotideEncodedGlyphs)){
            return false;
        }
        DefaultNucleotideEncodedGlyphs other = (DefaultNucleotideEncodedGlyphs) obj;
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
        return gaps.size();
    }

    
}
