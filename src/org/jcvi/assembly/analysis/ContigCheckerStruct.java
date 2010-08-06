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
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.ContigStruct;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.LowestFlankingQualityValueStrategy;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.assembly.slice.LargeSliceMap;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.glyph.phredQuality.QualityDataStore;

import com.google.inject.Inject;

public class ContigCheckerStruct<R extends PlacedRead> implements ContigStruct<R>{

    private Contig<R> contig;
    private QualityDataStore qualityDataStore;
    private CoverageMap<CoverageRegion<R>> sequenceCoverageMap;
    private CoverageMap<CoverageRegion<Placed>> validRangeCoverageMap;
    private final QualityValueStrategy qualityValueStrategy;
    private SliceMap sliceMap;
    
    public QualityValueStrategy getQualityValueStrategy() {
        return qualityValueStrategy;
    }
    @Inject
    public ContigCheckerStruct(Contig<R> contig, QualityDataStore qualityDataStore){
        this.contig = contig;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = LowestFlankingQualityValueStrategy.getInstance();
    }
    
    @Override
    public synchronized CoverageMap<CoverageRegion<R>> getSequenceCoverageMap(){
        if(sequenceCoverageMap ==null){
            final DefaultCoverageMap.Builder<R> builder = new DefaultCoverageMap.Builder<R>(contig.getPlacedReads());
            sequenceCoverageMap= builder.build();
        }
        return sequenceCoverageMap;
    }
    public synchronized CoverageMap<CoverageRegion<Placed>> getSequenceValidRangeCoverageMap(){
        if(validRangeCoverageMap==null){
            List<Placed> validRanges = new ArrayList<Placed>();
            for(R placedRead : contig.getPlacedReads()){
                validRanges.add(placedRead.getValidRange());
            }
            final DefaultCoverageMap.Builder<Placed> builder = new DefaultCoverageMap.Builder<Placed>(validRanges);
            validRangeCoverageMap= builder.build();
        }
        return validRangeCoverageMap;
    }

    public synchronized Contig<R> getContig() {
        return contig;
    }

    public synchronized void setContig(Contig<R> contig) {
        this.contig = contig;
    }

    public QualityDataStore getQualityDataStore() {
        return qualityDataStore;
    }

    

    public synchronized void  setSequenceCoverageMap(
            CoverageMap<CoverageRegion<R>> sequenceCoverageMap) {
        this.sequenceCoverageMap = sequenceCoverageMap;
    }
    @Override
    public synchronized SliceMap getSliceMap() {
        if(sliceMap ==null){
            sliceMap = new LargeSliceMap(
                    getSequenceCoverageMap(), 
                    getQualityDataStore(), 
                    qualityValueStrategy,
                    Range.buildRangeOfLength(0, contig.getConsensus().getLength()));
        }
        return sliceMap;
    }
    
  
}
