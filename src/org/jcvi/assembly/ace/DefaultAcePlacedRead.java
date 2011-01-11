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
package org.jcvi.assembly.ace;

import org.jcvi.Range;
import org.jcvi.assembly.DefaultPlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.AbstractReferenceEncodedNucleotideGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.PrecomputedReferenceEncodedNucleotideGlyphs;
import org.jcvi.glyph.nuc.ReferencedEncodedNucleotideGlyphs;
import org.jcvi.sequence.DefaultRead;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public class DefaultAcePlacedRead extends DefaultPlacedRead implements AcePlacedRead {
    private final PhdInfo phdInfo;
    private final int ungappedFullLength;
    public DefaultAcePlacedRead(Read<ReferencedEncodedNucleotideGlyphs> read,
            long start, SequenceDirection dir,PhdInfo phdInfo, int ungappedFullLength) {
        super(read, start, dir);
        this.phdInfo =phdInfo;
        this.ungappedFullLength =ungappedFullLength;
    }

    @Override
    public PhdInfo getPhdInfo() {
        return phdInfo;
    }

    @Override
    public int getUngappedFullLength() {
        return ungappedFullLength;
    }


    public static class Builder{
        private String readId;
        private TemporaryReferenceGlyphs referencedGlyphs;
        private int offset;
        private int length;
        private Range clearRange;
        private PhdInfo phdInfo;
        private EncodedGlyphs<NucleotideGlyph> reference;
        private final SequenceDirection dir;
        private final int ungappedFullLength;
        
        public Builder(EncodedGlyphs<NucleotideGlyph> reference, String readId,String validBases,
                            int offset, SequenceDirection dir, Range clearRange,PhdInfo phdInfo,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.length = validBases.length();
            this.clearRange = clearRange;
            this.offset = offset;
            this.phdInfo = phdInfo;
            this.referencedGlyphs = new TemporaryReferenceGlyphs(reference, validBases, offset, clearRange);
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        public Builder reference(EncodedGlyphs<NucleotideGlyph> reference, int newOffset){
            this.reference = reference;
            this.offset = newOffset;
            return this;
        }
        public int offset(){
            return offset;
        }
        public String id(){
            return readId;
        }
        public Builder setOffset(int newOffset){
            this.offset = newOffset;
            return this;
        }
        
        public DefaultAcePlacedRead build(){
            Read read = new DefaultRead(readId, 
                    referencedGlyphs.buildReferenceEncodedNucleotideGlyphWithCorrectedReference(reference,offset, length, clearRange));
            return new DefaultAcePlacedRead(read, offset, dir, phdInfo,ungappedFullLength);
        }
        
    }
    
    private static class TemporaryReferenceGlyphs extends AbstractReferenceEncodedNucleotideGlyphs{

        public TemporaryReferenceGlyphs(
                EncodedGlyphs<NucleotideGlyph> reference, String toBeEncoded,
                int startOffset, Range validRange) {
            super(reference, toBeEncoded, startOffset, validRange);
        }

        @Override
        protected NucleotideGlyph getFromReference(int referenceIndex) {
            // TODO Auto-generated method stub
            return null;
        }
        
        public ReferencedEncodedNucleotideGlyphs buildReferenceEncodedNucleotideGlyphWithCorrectedReference(
                EncodedGlyphs<NucleotideGlyph> reference,int startOffset, int length, Range validRange){
            return new PrecomputedReferenceEncodedNucleotideGlyphs(reference,
                    this.getSnpIndexes(),this.getSnpValues(), this.getGapIndexes(),startOffset, length, validRange );
        }
        
    }
}
