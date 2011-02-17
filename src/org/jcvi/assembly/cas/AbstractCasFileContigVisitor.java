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

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.cas.alignment.CasAlignment;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegion;
import org.jcvi.assembly.cas.alignment.CasAlignmentRegionType;
import org.jcvi.assembly.cas.read.CasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasPlacedRead;
import org.jcvi.assembly.cas.read.DefaultCasPlacedReadFromCasAlignmentBuilder;
import org.jcvi.assembly.util.TrimDataStore;
import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasFileContigVisitor extends AbstractOnePassCasFileVisitor{

    private final CasIdLookup referenceIdLookup;
    private final CasIdLookup readIdLookup;
    private final CasGappedReferenceMap gappedReferenceMap;
    private final DataStore<NucleotideEncodedGlyphs> nucleotideDataStore;
    private final TrimDataStore validRangeDataStore;
    
    /**
     * @param referenceIdLookup
     * @param readIdLookup
     * @param gappedReferenceMap
     * @param nucleotideDataStore
     * @param qualityDataStore
     * @param consensusCaller
     * @param qualityValueStrategy
     */
    public AbstractCasFileContigVisitor(CasIdLookup referenceIdLookup,
            CasIdLookup readIdLookup, CasGappedReferenceMap gappedReferenceMap,
            DataStore<NucleotideEncodedGlyphs> nucleotideDataStore,TrimDataStore trimDataStore) {
        this.referenceIdLookup = referenceIdLookup;
        this.readIdLookup = readIdLookup;
        this.gappedReferenceMap = gappedReferenceMap;
        this.nucleotideDataStore = nucleotideDataStore;
        this.validRangeDataStore = trimDataStore;
    }

    @Override
    protected synchronized void visitMatch(CasMatch match, long readCounter) {
        if(match.matchReported()){
            String readId = readIdLookup.getLookupIdFor(readCounter);
            CasAlignment alignment = match.getChosenAlignment();
            long referenceId = alignment.contigSequenceId();
            
            DefaultCasPlacedReadFromCasAlignmentBuilder builder;
            
            final NucleotideEncodedGlyphs gappedReference = gappedReferenceMap.getGappedReferenceFor(referenceId);
            long ungappedStartOffset = alignment.getStartOfMatch();
            long gappedStartOffset = gappedReference.convertUngappedValidRangeIndexToGappedValidRangeIndex((int)ungappedStartOffset);
            try {
                builder = new DefaultCasPlacedReadFromCasAlignmentBuilder(readId,
                        nucleotideDataStore.get(readId),
                        alignment.readIsReversed(),
                        gappedStartOffset,
                        validRangeDataStore.get(readId)
                       );
                List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(alignment.getAlignmentRegions());
                int lastIndex = regionsToConsider.size()-1;
                if(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
                    regionsToConsider.remove(lastIndex);
                }
                builder.addAlignmentRegions(regionsToConsider,gappedReference);
                
                
                final DefaultCasPlacedRead casPlacedRead = builder.build();
                visitPlacedRead(referenceId,casPlacedRead);
            } catch (DataStoreException e) {
                throw new IllegalStateException("could not create read placement for "+ alignment, e);
            }
        }
    }

    protected abstract void visitPlacedRead(long referenceId, CasPlacedRead casPlacedRead);
    
    public final CasIdLookup getReferenceIdLookup() {
        return referenceIdLookup;
    }

    public final CasIdLookup getReadIdLookup() {
        return readIdLookup;
    }
}
