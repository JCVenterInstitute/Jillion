/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.consensus;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder;
import org.jcvi.jillion.assembly.util.SliceBuilder.SliceElementFilter;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code NextGenReferenceConsensusRecaller} is a 
 * special {@link ConsensusCaller} implementation
 * designed for recalling the consensus
 * from a reference based mapping assembly
 * that contains reads from "next-generation"
 * sequencing technologies.
 * 
 * Some next-generation sequencing technologies 
 * have known sequencing error profiles that are
 * directionally biased sequence specific errors (SSE).
 * These SSEs  cause insertions or deletions (indels) which may
 * introduce frameshifts in the consensus.
 * This consensus caller tries to limit the number
 * of sequence specific errors by comparing
 * the most frequent base in the given {@link Slice}
 * to the reference consensus call {@link Slice#getConsensusCall()}.
 * If the most frequent base vs the reference would cause an indel,
 * then re-examine the slice by direction, if either direction
 * agrees with the reference, then keep the reference as the consensus
 * call.
 * @author dkatzel
 *
 *@see <a href="http://nar.oxfordjournals.org/content/39/13/e90">Nakamura et al. Sequence-specific error profile of Illumina sequencers. Nuclic Acids Res. 2011. PMID 21576222</a>
 */
public class NextGenReferenceConsensusRecaller implements ConsensusCaller {

	private static final SliceElementFilter FORWARD_FILTER = new SliceElementFilter() {
		
		@Override
		public boolean accept(SliceElement e) {
			return e.getDirection()==Direction.FORWARD;
		}
	};
	
	private static final SliceElementFilter REVERSE_FILTER = new SliceElementFilter() {
		
		@Override
		public boolean accept(SliceElement e) {
			return e.getDirection()==Direction.REVERSE;
		}
	};

	private static final int MIN_QUALITY = 5;
	
	private final ConsensusCaller delegateConsensusCaller;
	
	public NextGenReferenceConsensusRecaller(){
		this(MostFrequentBasecallConsensusCaller.INSTANCE);
	}
	public NextGenReferenceConsensusRecaller(ConsensusCaller delegateConsensusCaller) {
		if(delegateConsensusCaller==null){
			throw new NullPointerException("delegate consensus caller can not be null");
		}
		this.delegateConsensusCaller = delegateConsensusCaller;
	}

	@Override
	public ConsensusResult callConsensus(Slice slice) {
		ConsensusResult delegatedResult =getDelegateConsensus(slice);
		Nucleotide originalConsensus= slice.getConsensusCall();
		if(delegatedResult.getConsensus().isGap() && !originalConsensus.isGap()){
			return handleDeletion(slice, delegatedResult, originalConsensus);
		}else if(!delegatedResult.getConsensus().isGap() && originalConsensus.isGap()){
			return handleInsertion(slice,delegatedResult);
		}
		
		return delegatedResult;
	}

	private ConsensusResult handleInsertion(Slice slice,
			ConsensusResult majorityBase) {
		SliceBuilder all = new SliceBuilder(slice);
		Slice forwardSlice = all.copy().filter(FORWARD_FILTER).build();		
		Slice reverseSlice = all.copy().filter(REVERSE_FILTER).build();
		
		if(forwardSlice.getCoverageDepth() ==0 || reverseSlice.getCoverageDepth()==0){
			return majorityBase;
		}
		
		ConsensusResult forwardMajority = getDelegateConsensus(forwardSlice);
		ConsensusResult reverseMajority = getDelegateConsensus(reverseSlice);
		
		if(forwardMajority.getConsensus().equals(reverseMajority.getConsensus())){
			return majorityBase;
		}
		final Nucleotide recalledConsensus;
		if(forwardMajority.getConsensus().isGap()){
			recalledConsensus = forwardMajority.getConsensus();				
		}else{
			recalledConsensus = reverseMajority.getConsensus();
		}
		int qualScore = computeCumlativeQualityConsensus(slice, recalledConsensus);
		return new DefaultConsensusResult(recalledConsensus, qualScore);
	}

	private ConsensusResult handleDeletion(Slice slice,
			ConsensusResult majorityBase, Nucleotide originalConsensus) {
		SliceBuilder all = new SliceBuilder(slice);
		Slice forwardSlice = all.copy().filter(FORWARD_FILTER).build();		
		Slice reverseSlice = all.copy().filter(REVERSE_FILTER).build();
		
		ConsensusResult forwardMajority = getDelegateConsensus(forwardSlice);
		ConsensusResult reverseMajority = getDelegateConsensus(reverseSlice);
		
		if(forwardSlice.getCoverageDepth() ==0 || reverseSlice.getCoverageDepth()==0){
			return majorityBase;
		}
		
		if(forwardMajority.getConsensus().equals(reverseMajority.getConsensus())){
			return majorityBase;
		}
		final Nucleotide recalledConsensus;
		if(forwardMajority.getConsensus().isGap()){
			recalledConsensus = reverseMajority.getConsensus();				
		}else if(reverseMajority.getConsensus().isGap()){
			recalledConsensus = forwardMajority.getConsensus();
		}else{
			//do we have the original base?
			if(forwardMajority.getConsensus().equals(originalConsensus)){
				recalledConsensus = forwardMajority.getConsensus();
			}else if(reverseMajority.getConsensus().equals(originalConsensus)){
				recalledConsensus = reverseMajority.getConsensus();
			}else{
				//none match?
				return majorityBase;
			}
		}
		int qualScore = computeCumlativeQualityConsensus(slice, recalledConsensus);
		return new DefaultConsensusResult(recalledConsensus, qualScore);
	}

	private ConsensusResult getDelegateConsensus(Slice slice){
		return delegateConsensusCaller.callConsensus(slice);
		
	}
	private int computeCumlativeQualityConsensus(Slice slice, Nucleotide consensus){
		int sum=0;
		for(SliceElement e : slice){
	        if(e.getBase() == consensus){
	            sum+= e.getQuality().getQualityScore();
	        }
	        else{
	            sum -= e.getQuality().getQualityScore();
	        }
		}
		//we could have a negative quality sum
		//if the number of the picked consensus is much less
		//than the rest
		return Math.max(MIN_QUALITY, sum);
	}

}
