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
package org.jcvi.common.core.assembly.contig.ace;

import org.jcvi.Range;
import org.jcvi.common.core.assembly.contig.DefaultPlacedRead;
import org.jcvi.common.core.seq.read.DefaultRead;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.symbol.residue.nuc.DefaultReferenceEncodedNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.ReferenceEncodedNucleotideSequence;

public class DefaultAcePlacedRead extends DefaultPlacedRead implements AcePlacedRead {
    private final PhdInfo phdInfo;
    private final int ungappedFullLength;
    public DefaultAcePlacedRead(Read<ReferenceEncodedNucleotideSequence> read,
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
        private ReferenceEncodedNucleotideSequence referencedGlyphs;
        private int offset;
        private Range clearRange;
        private PhdInfo phdInfo;
        private NucleotideSequence reference;
        private final SequenceDirection dir;
        private final int ungappedFullLength;
        
        public Builder(NucleotideSequence reference, String readId,String validBases,
                            int offset, SequenceDirection dir, Range clearRange,PhdInfo phdInfo,
                            int ungappedFullLength){
            this.readId = readId;
            this.dir =dir;
            this.clearRange = clearRange;
            this.offset = offset;
            this.phdInfo = phdInfo;
            
            
            //NucleotideEncodedGlyphs reference,
            //String toBeEncoded, int startOffset, Range validRange
            this.referencedGlyphs = new DefaultReferenceEncodedNucleotideSequence(
                    reference, validBases, offset, clearRange);
            if(referencedGlyphs.getNumberOfBasesAfterReference()<0 || referencedGlyphs.getNumberOfBasesAfterReference()>0){
                throw new IllegalArgumentException(String.format("read %s goes off the reference before %d, after %d",
                        readId,
                        referencedGlyphs.getNumberOfBasesBeforeReference(),
                        referencedGlyphs.getNumberOfBasesAfterReference()));
            }
            this.ungappedFullLength = ungappedFullLength;
        }
        
        
        public Builder reference(NucleotideSequence reference, int newOffset){
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
            ReferenceEncodedNucleotideSequence updatedEncodedBasecalls = 
                new DefaultReferenceEncodedNucleotideSequence(reference,
                        NucleotideGlyph.convertToString(referencedGlyphs.decode()),offset,clearRange);
            Read read = new DefaultRead(readId, 
                    updatedEncodedBasecalls);
            return new DefaultAcePlacedRead(read, offset, dir, phdInfo,ungappedFullLength);
        }
        
    }
    
   
}
