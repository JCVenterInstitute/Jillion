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
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.slice.consensus;

import java.util.Set;

import org.jcvi.jillion.assembly.util.slice.Slice;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
public final class BasicChurchillWatermanConsensusCaller extends AbstractChurchillWatermanConsensusCaller{

    private static final int MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY = 5;


    public BasicChurchillWatermanConsensusCaller(
            PhredQuality highQualityThreshold) {
        super(highQualityThreshold);
    }

    

    @Override
    protected Nucleotide getConsensus(
            ConsensusProbabilities normalizedConsensusProbabilities,
            Slice slice) {
        final Set<Nucleotide> basesUsedTowardsAmbiguity = getBasesUsedTowardsAmbiguity(normalizedConsensusProbabilities,
                        MAX_NUMBER_OF_BASES_TOWARDS_AMBIGUITY);
        return Nucleotide.getAmbiguityFor(basesUsedTowardsAmbiguity);
        
    }
    
    

    


    
}
