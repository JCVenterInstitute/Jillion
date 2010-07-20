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

package org.jcvi.assembly.cas;

import org.jcvi.assembly.cas.read.CasNucleotideDataStore;

/**
 * {@code SingleCasGappedReferenceMap} is a {@link CasGappedReferenceMap}
 * implementation that only keeps track of 1 gapped reference.
 * @author dkatzel
 *
 *
 */
public class SingleCasGappedReferenceMap extends DefaultCasGappedReferenceMap{

    private final long referenceId;
    /**
     * Builds a new SingleCasGappedReferenceMap.
     * @param referenceNucleotideDataStore the nucleotide datastore
     * that stores all the reference data.
     * @param contigNameLookup the cas id to name lookup
     * @param referenceId the (only) reference id to track.
     */
    public SingleCasGappedReferenceMap(
            CasNucleotideDataStore referenceNucleotideDataStore,
            CasIdLookup contigNameLookup, long referenceId) {
        super(referenceNucleotideDataStore, contigNameLookup);
        this.referenceId = referenceId;
    }
    @Override
    public synchronized void visitMatch(CasMatch match, long readCounter) {
        if(match.matchReported() && match.getChosenAlignment().contigSequenceId() ==referenceId){
            super.visitMatch(match, readCounter);
        }
       
    }
    
    

    
    
}
