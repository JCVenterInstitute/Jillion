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
 * Created on Feb 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.slice.QualityValueStrategy;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.glyph.qualClass.QualityClass;

public class DefaultContigQualityClassComputer<P extends PlacedRead> implements QualityClassComputer<P>{
   private final QualityValueStrategy qualityValueStrategy;
   private final PhredQuality qualityThreshold;
    
    public DefaultContigQualityClassComputer(QualityValueStrategy qualityValueStrategy,PhredQuality qualityThreshold){
        this.qualityValueStrategy = qualityValueStrategy;
        this.qualityThreshold = qualityThreshold;
    }
    @Override
    public QualityClass computeQualityClass( CoverageMap<CoverageRegion<P>> coverageMap,
            QualityDataStore qualityDataStore,
    NucleotideSequence consensus,int index) {
        CoverageRegion<P> region = coverageMap.getRegionWhichCovers(index);
        if(region ==null){
            return QualityClass.ZERO_COVERAGE;
        }
        final Nucleotide consensusBase = consensus.get(index);
        
        try {
            return computeQualityClassFor(qualityDataStore, index,
                    region, consensusBase);
        } catch (DataStoreException e) {
            throw new IllegalStateException("error getting quality values" ,e);
        }      
        
    }
    
    public QualityValueStrategy getQualityValueStrategy() {
        return qualityValueStrategy;
    }
    public PhredQuality getQualityThreshold() {
        return qualityThreshold;
    }
    protected QualityClass computeQualityClassFor(
            QualityDataStore qualityDataStore, int index,
            CoverageRegion<P> region, final Nucleotide consensusBase) throws DataStoreException {
        QualityClass.Builder builder = new QualityClass.Builder(consensusBase,qualityThreshold);
        return computeQualityClassFor(qualityDataStore, index, region,
                consensusBase, builder);
    }
    protected QualityClass computeQualityClassFor(
            QualityDataStore qualityDataStore, int index,
            CoverageRegion<P> region, final Nucleotide consensusBase,
            QualityClass.Builder builder) throws DataStoreException {
        for(P placedRead : region){
            final QualitySequence qualityRecord = qualityDataStore.get(placedRead.getId());
            if(qualityRecord !=null){
                int gappedOffset = (int) (index - placedRead.getGappedContigStart());
                NucleotideSequence gappedSequence = placedRead.getNucleotideSequence();
                final Nucleotide calledBase = gappedSequence.get(gappedOffset);
                PhredQuality qualityValue =qualityValueStrategy.getQualityFor(placedRead, qualityRecord, gappedOffset);
                boolean agreesWithConsensus = isSame(consensusBase, calledBase);
                boolean isHighQuality = isHighQuality(qualityValue);
                Direction direction =placedRead.getDirection();
                addRead(builder, agreesWithConsensus, isHighQuality,
                        direction);
            }
        }
        return builder.build();
    }
    private boolean isHighQuality(PhredQuality qualityValue) {
        return qualityValue.compareTo(qualityThreshold)>=0;
    }

    private boolean isSame(final Nucleotide base1,
            final Nucleotide base2) {
        return base1 == base2;
    }

    protected void addRead(QualityClass.Builder builder,
            boolean agreesWithConsensus, boolean isHighQuality,
            Direction direction) {
        if(agreesWithConsensus){
            if(isHighQuality){
                builder.addHighQualityAgreement(direction);
            }
            else{
                builder.addLowQualityAgreement(direction);
            }
        }
        else{
            if(isHighQuality){
                builder.addHighQualityConflict(direction);
            }
            else{
                builder.addLowQualityConflict(direction);
            }
        }
    }
}

