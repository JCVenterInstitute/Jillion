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
/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import static org.jcvi.jillion.core.residue.nt.Nucleotide.Adenine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Cytosine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Gap;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Guanine;
import static org.jcvi.jillion.core.residue.nt.Nucleotide.Thymine;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * <code>NoAmbiguityConsensusCaller</code>
 * will always return the non-ambiguous base
 * in the consensus with the lowest error probability.
 * @author dkatzel
 *
 *
 */
public class NoAmbiguityConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    public NoAmbiguityConsensusCaller(PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities, Slice slice) {
      //assume A is the answer initially
        Nucleotide result = Adenine;
        double lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Adenine);
        
        if(normalizedConsensusProbabilities.getProbabilityFor(Cytosine).compareTo(lowestErrorProbability) <0){
            result = Cytosine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Cytosine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Guanine).compareTo(lowestErrorProbability) <0){
            result = Guanine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Guanine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Thymine).compareTo(lowestErrorProbability) <0){
            result = Thymine;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Thymine);
        }
        if(normalizedConsensusProbabilities.getProbabilityFor(Gap).compareTo(lowestErrorProbability) <0){
            result = Gap;
            lowestErrorProbability = normalizedConsensusProbabilities.getProbabilityFor(Gap);
        }
        return result;
    }
}
