/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.util.Collection;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * @author dkatzel
 *
 *
 */
final class NucleotideCodecs {
	
	private NucleotideCodecs(){
		//can not instantiate
	}
    /**
     * Get the optimal NucleotideCodec for the given
     * collection of Nucleotides.  This method must 
     * iterate through potentially the entire collection.
     * @param nucleotides the nucleotides to be encoded by a NucleotideCodec.
     * @return a NucleotideCodec implementation, never null.
     */
    public static NucleotideCodec getNucleotideCodecFor(Collection<Nucleotide> nucleotides){
        int numGaps=0;
        int numNs=0;
        int size = nucleotides.size();
        for(Nucleotide nuc: nucleotides){
            if(nuc.isGap()){
                numGaps++;
            }else if(nuc == Nucleotide.Unknown){
                numNs++;
            }
            else if(nuc.isAmbiguity()){
                return DefaultNucleotideCodec.INSTANCE;
            }
            
        }
        
       
        return getCodecForGappedSequence(numGaps,numNs, size);
    }
    /**
     * Get the best {@link NucleotideCodec} for a gapped sequence.
     * Some codecs handle gaps differently which can either increase
     * random access time or take up more memory.  This method
     * knows implementation details of each codec inorder to decide
     * which is the optimal NucleotideCodec for a gapped sequence
     * with the given number of gaps vs the entire gapped size.
     * @param numGaps the number of gaps.
     * @param totalSize the total gapped size of the sequence to encode.
     * @return a NucleotideCodec instance; never null.
     */
    public static NucleotideCodec getCodecForGappedSequence(int numGaps, int numNs, int totalSize) {
        if(numGaps>0 && numNs >0){
            return DefaultNucleotideCodec.INSTANCE;
        }
        //we have only ACGT- or ACGTN
        //if there are too many gaps, then
        //it isn't a good idea to use 2bit encoding + gaps
        int bytesPerGapOrN =IOUtil.getUnsignedByteCount(totalSize);
        int encodedGapSize = bytesPerGapOrN *numGaps;
        //simple metric right now is use
        //2bit or 4 bit based on which one takes less memory
        if(encodedGapSize< totalSize/4){
            if(numGaps>0){
                return NoAmbiguitiesEncodedNucleotideCodec.INSTANCE; 
            }
            return ACGTNNucloetideCodec.INSTANCE;
        }
        return DefaultNucleotideCodec.INSTANCE;
    }
    
}
