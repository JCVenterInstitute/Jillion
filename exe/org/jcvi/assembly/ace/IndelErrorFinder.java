package org.jcvi.assembly.ace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class IndelErrorFinder {

	 private final float percentSNP;
	 private final int minlength;
	 
	public IndelErrorFinder(float percentSNP, int minlength) {
		this.percentSNP = percentSNP;
		this.minlength = minlength;
	}
	 
	public <P extends PlacedRead, C extends Contig<P>> List<Range>  findAbacusErrors(C contig){
       List<Range> allSnpRanges = new ArrayList<Range>();
       NucleotideSequence consensus = contig.getConsensus();
		for(P placedRead : contig.getPlacedReads()){
    	   List<Range> snpRanges = new ArrayList<Range>();
    	   for(Entry<Integer,Nucleotide> snp : placedRead.getSnps().entrySet()){
    		   int readOffset = snp.getKey().intValue();
    		   long consensusOffset = readOffset+ placedRead.getStart();
    		   Nucleotide consensusBase = consensus.get((int)consensusOffset);
 
    		   if(consensusBase.isGap()){
				snpRanges.add(Range.buildRange(consensusOffset));
    		   }
    	   }
    	   allSnpRanges.addAll( Range.mergeRanges(snpRanges));
       }
		List<Range> candidateRanges = new ArrayList<Range>();
		CoverageMap<CoverageRegion<Range>> snpCoverageMap = DefaultCoverageMap.buildCoverageMap(allSnpRanges);
		CoverageMap<CoverageRegion<P>> readCoverageMap = DefaultCoverageMap.buildCoverageMap(contig);
		for(CoverageRegion<Range> snpCoverageRegion : snpCoverageMap){
			int snpDepth = snpCoverageRegion.getCoverage();
			
			if(snpDepth >0){
				for(Long gappedOffset : snpCoverageRegion.asRange()){
					CoverageRegion<P> readRange =readCoverageMap.getRegionWhichCovers(gappedOffset.longValue());
					float readDepth = readRange.getCoverage();
					if(snpDepth/readDepth >= percentSNP){
						candidateRanges.add(Range.buildRange(gappedOffset.longValue()));
					}
				}
				
			}
		}
		List<Range> ungappedRanges = new ArrayList<Range>();
		for(Range mergedGappedCandidate : Range.mergeRanges(candidateRanges)){
			if(mergedGappedCandidate.getLength()>=minlength){
				Range ungappedRange = AssemblyUtil.toUngappedRange(consensus, mergedGappedCandidate).convertRange(CoordinateSystem.RESIDUE_BASED);
				ungappedRanges.add(ungappedRange);
			}
		}
		return ungappedRanges;
	}
}
