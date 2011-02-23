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
import org.jcvi.glyph.nuc.DefaultReferencedEncodedNucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
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
        private ReferencedEncodedNucleotideGlyphs referencedGlyphs;
        private int offset;
        private Range clearRange;
        private PhdInfo phdInfo;
        private NucleotideEncodedGlyphs reference;
        private final SequenceDirection dir;
        private final int ungappedFullLength;
        
        public Builder(NucleotideEncodedGlyphs reference, String readId,String validBases,
                            int offset, SequenceDirection dir, Range clearRange,PhdInfo phdInfo,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.clearRange = clearRange;
            this.offset = offset;
            this.phdInfo = phdInfo;
            //NucleotideEncodedGlyphs reference,
            //String toBeEncoded, int startOffset, Range validRange
            this.referencedGlyphs = new DefaultReferencedEncodedNucleotideGlyph(
                    reference, validBases, offset, clearRange);
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        public Builder reference(NucleotideEncodedGlyphs reference, int newOffset){
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
            ReferencedEncodedNucleotideGlyphs updatedEncodedBasecalls = 
                new DefaultReferencedEncodedNucleotideGlyph(reference,
                        NucleotideGlyph.convertToString(referencedGlyphs.decode()),offset,clearRange);
            Read read = new DefaultRead(readId, 
                    updatedEncodedBasecalls);
            return new DefaultAcePlacedRead(read, offset, dir, phdInfo,ungappedFullLength);
        }
        
    }
    
   
}
