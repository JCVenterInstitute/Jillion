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

package org.jcvi.common.analysis.contig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * @author dkatzel
 *
 *
 */
public class AbacusErrorFinder {
    private final int clusterDistance;
    private final int minAbacusLength;
    private final double percentGap;
    public AbacusErrorFinder(int clusterDistance, int minAbacusLength,double percentGap){
        this.clusterDistance = clusterDistance;
        this.minAbacusLength = minAbacusLength;
        this.percentGap = percentGap;
    }
    private <P extends PlacedRead, C extends Contig<P>> List<Range> filterCandidates(C contig,
            List<Range> ungappedCandidateRanges) {
        CoverageMap<CoverageRegion<P>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
        NucleotideSequence consensus = contig.getConsensus();
        List<Range> errorRanges = new ArrayList<Range>(ungappedCandidateRanges.size());
        for(Range ungappedCandidateRange : ungappedCandidateRanges){           
            int gappedStart = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getStart())+1;
            int gappedEnd = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getEnd()+1) -1;
            Range gappedCandidateRange = Range.buildRange(gappedStart, gappedEnd);
            Set<String> readIds = new HashSet<String>();
            for(CoverageRegion<P> region : coverageMap.getRegionsWhichIntersect(gappedCandidateRange)){
                for(P read : region){
                    readIds.add(read.getId());
                }
            }
            boolean isAbacusError=true;
            for(String readId : readIds){
                P read =contig.getPlacedReadById(readId);              
                long adjustedStart = Math.max(gappedCandidateRange.getStart(), read.getStart());
                long adjustedEnd = Math.min(gappedCandidateRange.getEnd(), read.getEnd());
                boolean spansEntireRegion = (adjustedStart == gappedCandidateRange.getStart()) && (adjustedEnd == gappedCandidateRange.getEnd());
                if(spansEntireRegion){
                    Range rangeOfInterest = Range.buildRange(
                            read.toGappedValidRangeOffset(adjustedStart),
                            read.toGappedValidRangeOffset(adjustedEnd));
                   double numGaps=0;
                    for(Nucleotide n :read.getNucleotideSequence().asList(rangeOfInterest)){
                        if(n.isGap()){
                            numGaps++;
                        }
                    }
                    double percentGaps = numGaps/rangeOfInterest.getLength();
                    if(percentGaps <percentGap){
                        isAbacusError=false;
                        break;
                    }
                    
                }
            }
            if(isAbacusError){
                errorRanges.add(ungappedCandidateRange);
            }
            
        }
        return errorRanges;
    }

    private List<Range> convertToUngappedRanges(List<Range> abacusErrors,
            NucleotideSequence consensus) {
        List<Range> ungappedRanges = new ArrayList<Range>(abacusErrors.size());
        for(Range error : abacusErrors){
            if(error.getLength() >=5){
                int ungappedStart =consensus.getUngappedOffsetFor((int)error.getStart());
                int ungappedEnd =consensus.getUngappedOffsetFor((int)error.getEnd());
                
                ungappedRanges.add(Range.buildRange(ungappedStart, ungappedEnd)); 
            }
        }

        List<Range> candidateRanges = Range.mergeRanges(ungappedRanges);
        return candidateRanges;
    }
    public <P extends PlacedRead, C extends Contig<P>> List<Range>  findAbacusErrors(C contig){
        List<Range> ungappedCandidateRanges = getUngappedCandidateRanges(contig);
        return filterCandidates(contig, ungappedCandidateRanges) ;
        
    }
    private <P extends PlacedRead, C extends Contig<P>> List<Range> getUngappedCandidateRanges(C contig) {
        
        List<Range> gapRangesPerRead = new ArrayList<Range>(contig.getNumberOfReads());
        for(P placedRead : contig.getPlacedReads()){           
            List<Range> gaps = new ArrayList<Range>(placedRead.getNucleotideSequence().getNumberOfGaps());
            for(Integer gapOffset : placedRead.getNucleotideSequence().getGapOffsets()){
                Range buildRange = Range.buildRange(gapOffset.intValue() + placedRead.getStart());
                gaps.add(buildRange);
            }
            List<Range> mergeRanges = Range.mergeRanges(gaps);
            for(Range mergedRange: mergeRanges ){               
                if(mergedRange.getLength() >=minAbacusLength){
                    gapRangesPerRead.add(mergedRange);
                }
            }
        }
        
        CoverageMap<CoverageRegion<Range>> clusteredGapCoverage = DefaultCoverageMap.buildCoverageMap(gapRangesPerRead);
    
        List<Range> abacusErrors = new ArrayList<Range>();
       
        for(CoverageRegion<Range> gapRegion : clusteredGapCoverage){          
            if(gapRegion.getCoverage() >0){
                abacusErrors.add(gapRegion.asRange());
            }            
        }
        
        List<Range> ungappedCandidateRanges = convertToUngappedRanges(Range.mergeRanges(abacusErrors,clusterDistance), contig.getConsensus());
        return ungappedCandidateRanges;
    }
    
   
}
