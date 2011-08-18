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

package org.jcvi.common.core.symbol.residue.nuc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.GlyphCodec;

/**
 * {@code TwoBitEncodedNucleotideCodec} is a {@link GlyphCodec}
 * of Nucletotides that can encode a list of {@link Nucleotide}s
 * that only contain A,C,G,T and gaps (no ambiguities) in as little as 2 bits per base
 * plus some extra bytes for storing the gaps. This should 
 * greatly reduce the memory footprint of most kinds of read data.
 * @author dkatzel
 */
class NoAmbiguitiesEncodedNucleotideCodec extends TwoBitEncodedNucleotideCodec{
    public static final NoAmbiguitiesEncodedNucleotideCodec INSTANCE = new NoAmbiguitiesEncodedNucleotideCodec();
    
    static boolean canEncode(Iterable<Nucleotide> nucleotides){
        for(Nucleotide n :nucleotides){
            if(n.isAmbiguity()){
                return false;
            }
        }
        return true;
    }
    private NoAmbiguitiesEncodedNucleotideCodec(){
        super(Nucleotide.Gap);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public List<Integer> getGapOffsets(byte[] encodedGlyphs) {
        int[] gaps =getSentienelOffsetsFrom(encodedGlyphs);
        List<Integer> ret = new ArrayList<Integer>(gaps.length);
        for(int i=0; i<gaps.length; i++){
            ret.add(Integer.valueOf(gaps[i]));
        }
        return ret;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
        return (int)IOUtil.readUnsignedInt(Arrays.copyOfRange(encodedGlyphs, 4, 8));
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
        int[] gaps =getSentienelOffsetsFrom(encodedGlyphs);
        return Arrays.binarySearch(gaps, gappedOffset)>=0;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
        int gappedLength= (int)IOUtil.readUnsignedInt(Arrays.copyOfRange(encodedGlyphs, 0, 4));
        
        return gappedLength - getNumberOfGaps(encodedGlyphs);
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public List<Nucleotide> asUngappedList(byte[] encodedGlyphs) {
        ByteBuffer buf = ByteBuffer.wrap(encodedGlyphs);
        int length = decodedLengthOf(encodedGlyphs);
        int numberOfBytesPerGap = computeBytesPerSentinelOffset(length);
        int[] gaps = getSentinelOffsets(buf,numberOfBytesPerGap);
        List<Nucleotide> result = decodeNucleotidesWithSentinels(
                encodedGlyphs, length, numberOfBytesPerGap, gaps);
        for(int i= gaps.length-1; i>=0; i--){
            result.remove(i);
        }
        return result;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
        int[] gaps =getSentienelOffsetsFrom(encodedGlyphs);
        int i=Integer.MIN_VALUE;
        int numGaps=0;
        while(i<gappedOffset && numGaps<gaps.length){
            i =gaps[numGaps];
            numGaps++;
        }
        return numGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
        int numGaps=getNumberOfGapsUntil(encodedGlyphs,gappedOffset);
        return gappedOffset-numGaps;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
        int[] gaps =getSentienelOffsetsFrom(encodedGlyphs);
        int currentOffset=ungappedOffset;
        for(int i=0; i<gaps.length && gaps[i]>currentOffset; i++){
            currentOffset++;
        }
        return currentOffset;
    }
    
}
