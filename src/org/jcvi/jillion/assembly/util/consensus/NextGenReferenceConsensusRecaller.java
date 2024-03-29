/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.consensus;


import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.SliceBuilder.SliceElementFilter;
import org.jcvi.jillion.assembly.util.SliceCollectors;
import org.jcvi.jillion.assembly.util.SliceElement;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumn;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumnCollectors;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumnConsensusCaller;
import org.jcvi.jillion.assembly.util.columns.AssemblyColumnElement;
import org.jcvi.jillion.assembly.util.columns.QualifiedAssemblyColumn;
import org.jcvi.jillion.assembly.util.columns.QualifiedAssemblyColumnElement;
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

	private static final Predicate<AssemblyColumnElement> FORWARD_FILTER = e-> e.getDirection()==Direction.FORWARD;
	

	private static final int MIN_QUALITY = 5;
	
	private final ConsensusCaller delegateConsensusCaller;
	
	
//	@SuppressWarnings("unchecked")
//	public static <E extends QualifiedAssemblyColumnElement, C extends QualifiedAssemblyColumn<E>> AssemblyColumnConsensusCaller<E, QualifiedAssemblyColumn<E>> createDefault() {
//		//need to have separate variable for type inference
//		AssemblyColumnConsensusCaller<E,QualifiedAssemblyColumn<E>> delegate = MostFrequentBasecallConsensusCaller.instance();
//		return create(delegate);
//	}

//	public static <E extends QualifiedAssemblyColumnElement, C extends QualifiedAssemblyColumn<E>> AssemblyColumnConsensusCaller<E, QualifiedAssemblyColumn<E>> create(AssemblyColumnConsensusCaller<E, QualifiedAssemblyColumn<E>> delegate) {
//		return new NextGenReferenceConsensusRecaller<>(delegate);
//	}
	public NextGenReferenceConsensusRecaller() {
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

		Map<Boolean, Slice> dirMap = slice.elements().collect(
				Collectors.partitioningBy(FORWARD_FILTER, SliceCollectors.toSlice(slice.getCoverageDepth())));
		
		Slice forwardSlice = dirMap.get(Boolean.TRUE);
		Slice reverseSlice = dirMap.get(Boolean.FALSE);
		
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
		
		Map<Boolean, Slice> dirMap = slice.elements().collect(
				Collectors.partitioningBy(FORWARD_FILTER, SliceCollectors.toSlice(slice.getCoverageDepth())));
		
		Slice forwardSlice = dirMap.get(Boolean.TRUE);
		Slice reverseSlice = dirMap.get(Boolean.FALSE);
		
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
	            sum+= e.getQualityScore();
	        }
	        else{
	            sum -= e.getQualityScore();
	        }
		}
		//we could have a negative quality sum
		//if the number of the picked consensus is much less
		//than the rest
		return Math.max(MIN_QUALITY, sum);
	}

}
