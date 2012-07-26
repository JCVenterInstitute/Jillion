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

package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Ranges;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapUtil;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.iter.StreamingIterator;

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
    private <P extends AssembledRead, C extends Contig<P>> List<Range> filterCandidates(C contig,
            List<Range> ungappedCandidateRanges) {
        CoverageMap<P> coverageMap = CoverageMapFactory.createGappedCoverageMapFromContig(contig);
        NucleotideSequence consensus = contig.getConsensusSequence();
        List<Range> errorRanges = new ArrayList<Range>(ungappedCandidateRanges.size());
        for(Range ungappedCandidateRange : ungappedCandidateRanges){           
            int gappedStart = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getBegin())+1;
            int gappedEnd = consensus.getGappedOffsetFor((int)ungappedCandidateRange.getEnd()+1) -1;
            Range gappedCandidateRange = Range.create(gappedStart, gappedEnd);
            Set<String> readIds = new HashSet<String>();
            for(CoverageRegion<P> region : CoverageMapUtil.getRegionsWhichIntersect(coverageMap, gappedCandidateRange)){
                for(P read : region){
                    readIds.add(read.getId());
                }
            }
            boolean isAbacusError=true;
            for(String readId : readIds){
                P read =contig.getRead(readId);              
                long adjustedStart = Math.max(gappedCandidateRange.getBegin(), read.getGappedStartOffset());
                long adjustedEnd = Math.min(gappedCandidateRange.getEnd(), read.getGappedEndOffset());
                boolean spansEntireRegion = (adjustedStart == gappedCandidateRange.getBegin()) && (adjustedEnd == gappedCandidateRange.getEnd());
                if(spansEntireRegion){
                    Range rangeOfInterest = Range.create(
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
                int ungappedStart =consensus.getUngappedOffsetFor((int)error.getBegin());
                int ungappedEnd =consensus.getUngappedOffsetFor((int)error.getEnd());
                
                ungappedRanges.add(Range.create(ungappedStart, ungappedEnd)); 
            }
        }

        List<Range> candidateRanges = Ranges.merge(ungappedRanges);
        return candidateRanges;
    }
    public <P extends AssembledRead, C extends Contig<P>> List<Range>  findAbacusErrors(C contig){
        List<Range> ungappedCandidateRanges = getUngappedCandidateRanges(contig);
        return filterCandidates(contig, ungappedCandidateRanges) ;
        
    }
    private <P extends AssembledRead, C extends Contig<P>> List<Range> getUngappedCandidateRanges(C contig) {
        
        List<Range> gapRangesPerRead = new ArrayList<Range>(contig.getNumberOfReads());
        StreamingIterator<P> readIterator = null;
        try{
        	readIterator = contig.getReadIterator();
        	while(readIterator.hasNext()){         
	            P placedRead = readIterator.next();
        		List<Range> gaps = new ArrayList<Range>(placedRead.getNucleotideSequence().getNumberOfGaps());
	            for(Integer gapOffset : placedRead.getNucleotideSequence().getGapOffsets()){
	                Range buildRange = Range.create(gapOffset.intValue() + placedRead.getGappedStartOffset());
	                gaps.add(buildRange);
	            }
	            List<Range> mergeRanges = Ranges.merge(gaps);
	            for(Range mergedRange: mergeRanges ){               
	                if(mergedRange.getLength() >=minAbacusLength){
	                    gapRangesPerRead.add(mergedRange);
	                }
	            }
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(readIterator);
        }
        CoverageMap<Range> clusteredGapCoverage = CoverageMapFactory.create(gapRangesPerRead);
    
        List<Range> abacusErrors = new ArrayList<Range>();
       
        for(CoverageRegion<Range> gapRegion : clusteredGapCoverage){          
            if(gapRegion.getCoverageDepth() >0){
                abacusErrors.add(gapRegion.asRange());
            }            
        }
        
        List<Range> ungappedCandidateRanges = convertToUngappedRanges(Ranges.merge(abacusErrors,clusterDistance), contig.getConsensusSequence());
        return ungappedCandidateRanges;
    }
    
   
}
