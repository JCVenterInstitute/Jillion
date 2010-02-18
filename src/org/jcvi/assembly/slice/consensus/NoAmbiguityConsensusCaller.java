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
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import static org.jcvi.glyph.nuc.NucleotideGlyph.*;
/**
 * <code>NoAmbiguityConsensusCaller</code>
 * will always return the non-ambigious base
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
    protected NucleotideGlyph getConsensus(
            ProbabilityStruct normalizedErrorProbabilityStruct, Slice slice) {
      //assume A is the answer initially
        NucleotideGlyph result = Adenine;
        double lowestErrorProbability = normalizedErrorProbabilityStruct.getProbabilityFor(Adenine);
        
        if(normalizedErrorProbabilityStruct.getProbabilityFor(Cytosine).compareTo(lowestErrorProbability) <0){
            result = Cytosine;
            lowestErrorProbability = normalizedErrorProbabilityStruct.getProbabilityFor(Cytosine);
        }
        if(normalizedErrorProbabilityStruct.getProbabilityFor(Guanine).compareTo(lowestErrorProbability) <0){
            result = Guanine;
            lowestErrorProbability = normalizedErrorProbabilityStruct.getProbabilityFor(Guanine);
        }
        if(normalizedErrorProbabilityStruct.getProbabilityFor(Thymine).compareTo(lowestErrorProbability) <0){
            result = Thymine;
            lowestErrorProbability = normalizedErrorProbabilityStruct.getProbabilityFor(Thymine);
        }
        if(normalizedErrorProbabilityStruct.getProbabilityFor(Gap).compareTo(lowestErrorProbability) <0){
            result = Gap;
            lowestErrorProbability = normalizedErrorProbabilityStruct.getProbabilityFor(Gap);
        }
        return result;
    }
}
