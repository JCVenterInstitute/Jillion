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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;

public abstract class AbstractReferenceEncodedNucleotideGlyphs extends AbstractEnocdedNucleotideGlyphs implements ReferencedEncodedNucleotideGlyphs{

    private final List<Integer> gaps;
    private final Map<Integer, NucleotideGlyph> differentGlyphMap;
    private final int length;
    private final int startOffset;
    private final Range validRange;
    protected AbstractReferenceEncodedNucleotideGlyphs(Map<Integer, NucleotideGlyph> differentGlyphMap, List<Integer> gaps, int startOffset,int length,Range validRange){
        this.differentGlyphMap = differentGlyphMap;
        this.gaps = gaps;
        this.startOffset = startOffset;
        this.length = length;
        this.validRange = validRange;
    }
    public AbstractReferenceEncodedNucleotideGlyphs(EncodedGlyphs<NucleotideGlyph> reference,
            String toBeEncoded, int startOffset,Range validRange){
        List<Integer> tempGapList = new ArrayList<Integer>();     
        this.startOffset = startOffset;
        this.length = toBeEncoded.length();
        this.validRange = validRange;
        differentGlyphMap = new TreeMap<Integer, NucleotideGlyph>();
        populateFields(reference, toBeEncoded, startOffset, tempGapList);
        gaps = Collections.unmodifiableList(tempGapList);
    }
    private void populateFields(EncodedGlyphs<NucleotideGlyph> reference,
            String toBeEncoded, int startOffset, List<Integer> tempGapList) {
        for(int i=0; i<toBeEncoded.length(); i++){
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
    }

    private boolean isDifferent(NucleotideGlyph g, final NucleotideGlyph referenceGlyph) {
        return g!=referenceGlyph;
    }

    protected List<Integer> getGaps() {
        return gaps;
    }

    protected Map<Integer, NucleotideGlyph> getDifferentGlyphMap() {
        return differentGlyphMap;
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
        final Integer indexAsInteger = Integer.valueOf(index);
        if(gaps.contains(indexAsInteger)){
            return NucleotideGlyph.Gap;
        }
        if(differentGlyphMap.containsKey(indexAsInteger)){
            return differentGlyphMap.get(indexAsInteger);
        }
        int referenceIndex = index+startOffset;
        return getFromReference(referenceIndex);
    }

    @Override
    public boolean isGap(int index) {
        return gaps.contains(Integer.valueOf(index));
    }
    protected abstract NucleotideGlyph getFromReference(int referenceIndex);
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public List<Integer> getGapIndexes() {
        return gaps;
    }
    @Override
    public Map<Integer, NucleotideGlyph> getSnps(){
        return differentGlyphMap;
    }
    @Override
    public Range getValidRange() {
        return validRange;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + differentGlyphMap.hashCode();
        result = prime * result + gaps.hashCode();
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
        if (!(obj instanceof AbstractReferenceEncodedNucleotideGlyphs)) {
            return false;
        }
        AbstractReferenceEncodedNucleotideGlyphs other = (AbstractReferenceEncodedNucleotideGlyphs) obj;
        if (differentGlyphMap == null) {
            if (other.differentGlyphMap != null) {
                return false;
            }
        } else if (!differentGlyphMap.equals(other.differentGlyphMap)) {
            return false;
        }
        if (gaps == null) {
            if (other.gaps != null) {
                return false;
            }
        } else if (!gaps.equals(other.gaps)) {
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
    
    
    
    

    
}
