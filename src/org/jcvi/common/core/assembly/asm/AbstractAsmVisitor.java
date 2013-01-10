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

package org.jcvi.common.core.assembly.asm;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range;

/**
 * {@code AbstractAsmVisitor} is an {@link AsmVisitor}
 * implementation that implements all void methods
 * as no-ops and all methods that return
 * some kind of filter return value as the 
 * most permissive return so all objects
 * will get visited.
 * 
 * This allows sub-classes to only override
 * whatever they want to change without requiring
 * boilerplate code of lots of no-ops.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractAsmVisitor implements AsmVisitor{

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLine(String line) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitFile() {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfFile() {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitLibraryStatistics(String externalId, long internalId,
            float meanOfDistances, float stdDev, int min, int max,
            List<Integer> histogram) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitRead(String externalId, long internalId,
            MateStatus mateStatus, boolean isSingleton, Range clearRange) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitMatePair(String externalIdOfRead1,
            String externalIdOfRead2, MateStatus mateStatus) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    * <p/>
    * Defaults to no-op and returns {@code true}.
    */
    @Override
    public boolean visitUnitig(String externalId, long internalId, float aStat,
            float measureOfPolymorphism, UnitigStatus status,
            NucleotideSequence consensusSequence,
            QualitySequence consensusQualities, int numberOfReads) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfUnitig() {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitReadLayout(char readType, String externalReadId,
            DirectedRange readRange, List<Integer> gapOffsets) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitUnitigLayout(UnitigLayoutType type,
            String unitigExternalId, DirectedRange unitigRange,
            List<Integer> gapOffsets) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitUnitigLink(String externalUnitigId1,
            String externalUnitigId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status,
            boolean isPossibleChimera, int numberOfEdges, float meanDistance,
            float stddev, Set<MatePairEvidence> matePairEvidence) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitContigLink(String externalContigId1,
            String externalContigId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status, int numberOfEdges,
            float meanDistance, float stddev,
            Set<MatePairEvidence> matePairEvidence) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    * <p/>
    * Defaults to no-op and returns a Set
    * containing all {@link NestedContigMessageTypes}.
    */
    @Override
    public Set<NestedContigMessageTypes> visitContig(String externalId, long internalId,
            boolean isDegenerate, NucleotideSequence consensusSequence,
            QualitySequence consensusQualities, int numberOfReads,
            int numberOfUnitigs, int numberOfVariants) {
        return EnumSet.allOf(NestedContigMessageTypes.class);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitVariance(Range range, int numberOfSpanningReads,
            int anchorSize, long internalAccessionId,
            long accessionForPhasedVariant,
            SortedSet<VariantRecord> variantRecords) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfContig() {
    	//no-op
    }

    /**
    * {@inheritDoc}
    * <p/>
    * Defaults to no-op and returns {@code true}.
    */
    @Override
    public boolean visitScaffold(String externalId, long internalId,
            int numberOfContigPairs) {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitSingleContigInScaffold(String externalContigId) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitContigPair(String externalContigId1,
            String externalContigId2, float meanDistance, float stddev,
            LinkOrientation orientation) {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfScaffold() {
    	//no-op
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitScaffoldLink(String externalScaffoldId1,
            String externalScaffoldId2, LinkOrientation orientation,
            OverlapType overlapType, OverlapStatus status, int numberOfEdges,
            float meanDistance, float stddev,
            Set<MatePairEvidence> matePairEvidence) {
    	//no-op
    }

}
