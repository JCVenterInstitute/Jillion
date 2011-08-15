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

import java.util.Collection;

import org.jcvi.common.core.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
final class NucleotideCodecs {
    /**
     * Get the optimal NucleotideCodec for the given
     * collection of Nucleotides.  This method must 
     * iterate through potentially the entire collection.
     * @param nucleotides the nucleotides to be encoded by a NucleotideCodec.
     * @return a NucleotideCodec implementation, never null.
     */
    public static NucleotideCodec getNucleotideCodecFor(Collection<Nucleotide> nucleotides){
        int numGaps=0;
        int size = nucleotides.size();
        for(Nucleotide nuc: nucleotides){
            if(nuc.isAmbiguity()){
                return DefaultNucleotideGlyphCodec.INSTANCE;
            }
            if(nuc.isGap()){
                numGaps++;
            }
        }
        //we have only ACGT-
        //if there are too many gaps, then
        //it isn't a good idea to use 2bit encoding + gaps
        int bytesPerGap =IOUtil.getUnsignedByteCount(size);
        int encodedGapSize = bytesPerGap *numGaps;
        //simple metric right now is use
        //2bit or 4 bit based on which one takes less memory
        if(encodedGapSize< size/4){
            return NoAmbiguitiesEncodedNucleotideCodec.INSTANCE; 
        }
        return DefaultNucleotideGlyphCodec.INSTANCE;
    }
    
}
