package org.jcvi.jillion.trim.lucy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jcvi.jillion.align.NucleotideSubstitutionMatrices;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrix;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.trim.NucleotideTrimmer;
/**
 * {@code LucyVectorSpliceTrimmerBuilder} is a class that builds a
 * {@link NucleotideTrimmer} that finds the "vector free" {@link Range}
 * of a {@link NucleotideSequence} using a simplified version
 * of the algorithm that the TIGR program Lucy used.
 * <p/>
 * This Builder object allows you to configure the trimmer
 * to use customized settings.
 * 
 * @author dkatzel
 *
 */
public class LucyVectorSpliceTrimmerBuilder implements Builder<NucleotideTrimmer>{
	/**
	 * Default {@link AdaptiveSearchArea} instance which uses search area
	 * values as defined in the Lucy paper.
	 */
	private static final AdaptiveSearchArea DEFAULT_SEARCH_AREA = new AdaptiveSearchAreaBuilder()
																			.addAreaRange(40, 8)
																			.addAreaRange(60, 12)
																			.addAreaRange(100, 16)
																			.build();
	
	private static final int DEFAULT_GAP_OPEN = -17;
	private static final int DEFAULT_GAP_EXTENSION = -5;
	private static final NucleotideSubstitutionMatrix DEFAULT_MATRIX;
	
	static{
		try {
			DEFAULT_MATRIX = NucleotideSubstitutionMatrices.parsePropertyFile(LucyVectorSpliceTrimmerBuilder.class.getResourceAsStream("lucy.matrix"));
		} catch (IOException e) {
			throw new IllegalStateException("error parsing lucy subsitution matrix file",e);
		}
	}
	
	private final NucleotideSequence upstreamSpliceSeq, downstreamSpliceSeq;
	
	private NucleotideSubstitutionMatrix matrix = DEFAULT_MATRIX;
	private int gapOpen = DEFAULT_GAP_OPEN;
	private int gapExtension = DEFAULT_GAP_EXTENSION;
	
	private boolean checkBothDirections = true;
	
	private AdaptiveSearchArea adaptiveSearchArea = DEFAULT_SEARCH_AREA;
	
	/**
	 * Create a new Builder object with the given splice site sequences
	 * and initialized to use the default Lucy settings.
	 * 
	 * @param upstreamSpliceSeq the vector splice site upstream of the desired sequence;
	 * can not be null.
	 * @param downstreamSpliceSeq the vector splice site downstream of the desired sequence;
	 * can not be null.
	 * 
	 * @throws NullPointerException if either splice sequence is null.
	 */
	public LucyVectorSpliceTrimmerBuilder(NucleotideSequence upstreamSpliceSeq, NucleotideSequence downstreamSpliceSeq) {
		Objects.requireNonNull(upstreamSpliceSeq);
		Objects.requireNonNull(downstreamSpliceSeq);
		
		
		this.upstreamSpliceSeq = upstreamSpliceSeq;
		this.downstreamSpliceSeq = downstreamSpliceSeq;
	}
	/**
	 * Only check for splice sites in the forward direction.
	 * By default, this trimmer will check for splice sites in both
	 * directions (reverse complemented).  Call this method to only check
	 * for splice sites in the forward direction, no reverse complementing.
	 * This is not recommended unless you really know for sure that the sequences
	 * you will be providing this trimmer are all in the same orientation as
	 * the provided splice sequences.
	 * 
	 * @return this
	 */
	public LucyVectorSpliceTrimmerBuilder onlyCheckForwardDirection(){
		checkBothDirections = false;
		return this;
	}
	/**
	 * Create a new {@link NucleotideTrimmer} instance
	 * which follows the Lucy vector trim algorithm using
	 * the specified configuration.
	 * 
	 * @return a new {@link NucleotideTrimmer} will never be null.
	 */
	@Override
	public NucleotideTrimmer build() {
		return new LucyLikeVectorSpliceTrimmer(this);
	}
	/**
	 * Change the parameters used when aligning sequences to the vectors.
	 * <p>
	 * Lucy performs pairwise local alignments between the vector sequences and
	 * the input {@link NucleotideSequence}s.  By default, this class uses
	 * the same {@link org.jcvi.jillion.align.SubstitutionMatrix}
	 * values, and gap open and extension penalties that the LUCY program used.
	 * </p>
	 * @param matrix the {@link NucleotideSubstitutionMatrix} to use instead of the default; can not be null.
	 * @param gapOpen the gap open penalty to use instead of the default; usually a negative number.
	 * @param gapExtension the gap extension penalty to use instead of default; usually a negative number.
	 * 
	 * @return this
	 * 
	 * @throws NullPointerException if matrix is null.
	 */
	public LucyVectorSpliceTrimmerBuilder alignmentMatrix(NucleotideSubstitutionMatrix matrix, int gapOpen, int gapExtension){
		Objects.requireNonNull(matrix);
		this.gapOpen = gapOpen;
		this.gapExtension = gapExtension;
		return this;
	}
	
	/**
	 * Lucy uses an "adaptive search criteria" to find the vector splice sites
	 * that varies the minimum alignment criteria depending on where on the sanger sequence
	 * the splice site lies.
	 * 
	 * Change the {@link AdaptiveSearchArea} that this trimmer will use instead 
	 * of the default value.
	 * 
	 * @param adaptiveSearchArea a {@link AdaptiveSearchArea} to use instead of the default; can not be null.
	 * 
	 * @throws NullPointerException if adaptiveSearchArea is null.
	 * 
	 * @return this
	 * 
	 * @implNote the default {@link AdaptiveSearchArea} as defined by the Lucy paper is equivalent to
	 * <pre>
	 * {@code 
	 * new AdaptiveSearchAreaBuilder()
	 * 					.addAreaRange(40, 8)
	 * 					.addAreaRange(60, 12)
	 * 					.addAreaRange(100, 16)
	 * 					.build();
	 * }
	 * </pre>
	 */
	public LucyVectorSpliceTrimmerBuilder adaptiveSearchArea(AdaptiveSearchArea adaptiveSearchArea){
		Objects.requireNonNull(adaptiveSearchArea);
		this.adaptiveSearchArea = adaptiveSearchArea;
		return this;
	}

	private static final class LucyLikeVectorSpliceTrimmer implements NucleotideTrimmer{

		private final NucleotideSequence upstreamSpliceSeq, downstreamSpliceSeq;
		private final AdaptiveSearchArea adaptiveSearchArea;
		
		private final NucleotideSubstitutionMatrix matrix;
		private final int gapOpen;
		private final int gapExtension;
		
		private final boolean checkBothDirections;
		
		private LucyLikeVectorSpliceTrimmer(LucyVectorSpliceTrimmerBuilder builder){
			upstreamSpliceSeq = builder.upstreamSpliceSeq;
			downstreamSpliceSeq = builder.downstreamSpliceSeq;
			
			this.adaptiveSearchArea = builder.adaptiveSearchArea;
			matrix = builder.matrix;
			gapOpen = builder.gapOpen;
			gapExtension = builder.gapExtension;
			
			
			this.checkBothDirections = builder.checkBothDirections;
		}
		
		@Override
		public Range trim(NucleotideSequence seq) {
			Range fwdRange= getForwardGoodRange(seq);
			
			if(!checkBothDirections){
				return fwdRange;
			}
			
			Range revRange = getReverseGoodRange(seq);
			
			//get the smaller of the 2?
			//a smaller good range implies that something was found and trimmed
			if(revRange.getLength() < fwdRange.getLength()){
				return revRange;
			}
			return fwdRange;
		}

		private Range getReverseGoodRange(NucleotideSequence seq) {
			NucleotideSequence revComp = seq.toBuilder().reverseComplement().build();
			Range revRange = getForwardGoodRange(revComp);
			return AssemblyUtil.reverseComplementValidRange(revRange, revComp.getLength());
		}

		private Range getForwardGoodRange(NucleotideSequence seq) {
			Range upstreamVectorHit = findUpstreamVectorHit(seq);
			
			Range downstreamVectorHit = findDownstreamVectorHit(seq, upstreamVectorHit);
			
			Range.Builder goodRangeBuilder = new Range.Builder(seq.getLength());
			
			if(downstreamVectorHit !=null){
				goodRangeBuilder.setEnd(downstreamVectorHit.getBegin() -1);
			}
			if(upstreamVectorHit !=null){
				goodRangeBuilder.setBegin(upstreamVectorHit.getEnd() +1);
			}
			
									
			return goodRangeBuilder.build();
		}

		private Range findDownstreamVectorHit(NucleotideSequence seq, Range upstreamVectorHit) {
			long shiftAmount;
			NucleotideSequence subseq;
			if(upstreamVectorHit ==null){
				subseq = seq;
				shiftAmount = 0;
			}else{
				//only consider seq after upstream hit
				shiftAmount = upstreamVectorHit.getEnd()+1;
				subseq = seq.toBuilder()
								.delete(Range.ofLength(shiftAmount))
								.build();
				
				
			}
			NucleotidePairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(downstreamSpliceSeq, subseq, matrix)
					.gapPenalty(gapOpen, gapExtension)
					.useLocalAlignment()
					.build();

			Range subjectRange = alignment.getSubjectRange().getRange();
			if(this.adaptiveSearchArea.meetsDownstreamMatchingCriteria(subjectRange, alignment.getPercentIdentity())){			
				//have to shift range to compensate for amount we trimmed off
				return new Range.Builder(subjectRange)
								.shift(shiftAmount)
								.build();
			}
			return null;
		}

		private Range findUpstreamVectorHit(NucleotideSequence seq) {
			
			NucleotideSequence subseq = seq.toBuilder()
											.trim(adaptiveSearchArea.getSearchAreaRange())
											.build();
			//uses adaptive alignment as described in the Lucy paper
			NucleotidePairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(upstreamSpliceSeq, subseq, matrix)
																						.gapPenalty(gapOpen, gapExtension)
																						.useLocalAlignment()
																						.build();
			
			Range subjectRange = alignment.getSubjectRange().getRange();
			if(adaptiveSearchArea.meetsUpstreamMatchingCriteria(subjectRange)){
				return subjectRange;
			}
			
			//either didn't find any alignment or the alignment was too small
			//for the adapted min length
			
			//search through the rest of the sequence looking for any hit >=75% ident
			NucleotidePairwiseSequenceAlignment downstreamAlignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(upstreamSpliceSeq, seq, matrix)
					.gapPenalty(gapOpen, gapExtension)
					.useLocalAlignment()
					.build();
			
			Range downstreamSubjectRange = downstreamAlignment.getSubjectRange().getRange();
			if(adaptiveSearchArea.meetsDownstreamMatchingCriteria(downstreamSubjectRange, downstreamAlignment.getPercentIdentity())){
				return downstreamSubjectRange;
			}
			
			return null;
		}
		
	}
	
	private static final class SearchArea{
		private final Range range;
		private final int minAlignmentLength;
		
		public SearchArea(Range range, int minAlignmentLength) {
			Objects.requireNonNull(range);
			if(minAlignmentLength <1){
				throw new IllegalStateException("min alignment length can not be null");
			}
			this.range = range;
			this.minAlignmentLength = minAlignmentLength;
		}
		
		
	}
	/**
	 * Lucy uses an "adaptive search criteria" to find the vector splice sites
	 * that varies the minimum alignment criteria depending on where on the sanger sequence
	 * the splice site lies.
	 * <p>
	 * Because Sanger sequencing typically starts off with low quality
	 * basecalls and then gradually improves over the first 100 bp
	 * it is likely that the upstream vector splice site aligns to
	 * an area of poor quality.
	 * </p>
	 * <p>
	 * By having less stringent alignment criteria at the start
	 * of the sequence and then getting stricter as the quality goes up
	 * improves the chance that we will identity the upstream splice site
	 * without compromising downstream alignments.
	 * </p>
	 * @author dkatzel
	 *
	 */
	public static final class AdaptiveSearchArea{
		private final List<SearchArea> areas;
		private final Range areaRange;
		private AdaptiveSearchArea(AdaptiveSearchAreaBuilder builder){
			//add elements backwards
			areas = new ArrayList<LucyVectorSpliceTrimmerBuilder.SearchArea>(builder.areas);
			Collections.reverse(areas);
			areaRange = Range.ofLength(builder.currentOffset);
			
			if(areas.isEmpty()){
				throw new IllegalStateException("must have at least one search area");
			}
		}
		boolean meetsDownstreamMatchingCriteria(Range downstreamSubjectRange, double percentIdentity) {
			
			//must be >=75% ident
			if(percentIdentity < .75D){
				return false;
			}
			//lucy paper fig 6 implies area 3 parameters used to determine downstream hit
			//which makes sense since it should be in high quality sanger area
		
			//only check the first (which is actually the last, since we reversed the list)
			//the match can be anywhere as long as the length meets the criteria
			SearchArea area = areas.get(0);
			if(downstreamSubjectRange.getLength() >=area.minAlignmentLength){
				return true;
			}
			return false;
		}
		Range getSearchAreaRange(){
			return areaRange;
		}
		boolean meetsUpstreamMatchingCriteria(Range alignmentRange){
			long alignmentLength = alignmentRange.getLength();
			for(SearchArea area : areas){
				if(area.range.intersects(alignmentRange) && alignmentLength >=area.minAlignmentLength){
					return true;
				}
			}
			return false;
		}
	}
	/**
	 * Builder object that creates a new {@link AdaptiveSearchArea} object.
	 * 
	 * @author dkatzel
	 *
	 *@see LucyVectorSpliceTrimmerBuilder#adaptiveSearchArea(AdaptiveSearchArea)
	 */
	public static final class AdaptiveSearchAreaBuilder implements Builder<AdaptiveSearchArea>{
		private final List<SearchArea> areas = new ArrayList<LucyVectorSpliceTrimmerBuilder.SearchArea>();
		private long currentOffset=0;
		/**
		 * Add a new adaptive search area range of the specified length and minimum alignment length
		 * a vector splice site alignment must have to be considered a match if it intersects this search area range.
		 * Area lengths are appended to the previous lengths so successive calls to this method
		 * should increase the minimum match length since the sanger read should be in better and better
		 * quality area the further the area moves downstream.
		 * 
		 * @param areaLength the area length of this new search area range.  Since this area
		 * is appended to the previous areas, the actual coordinates are caluclated by summing
		 * the prevous area lengths.  Length can not be < 1.
		 * @param minimumMatchLength the minimum vector splice site alignment length; can not be < 1.
		 * 
		 * @return this
		 * 
		 * @throws IllegalArgumentException if either parameter is < 1.
		 */
		public AdaptiveSearchAreaBuilder addAreaRange(long areaLength, int minimumMatchLength){
			if(areaLength < 1){
				throw new IllegalArgumentException("area length can not be < 1");
			}
			if(minimumMatchLength < 1){
				throw new IllegalArgumentException("min match length can not be < 1");
			}
			Range range = new Range.Builder(areaLength)
									.shift(currentOffset)
									.build();
			currentOffset+=areaLength;
			areas.add(new SearchArea(range, minimumMatchLength));
			return this;
		}
		/**
		 *Create a new {@link AdaptiveSearchArea} using the current area ranges that have been added.
		 */
		@Override
		public AdaptiveSearchArea build() {
			return new AdaptiveSearchArea(this);
		}
	}
	
}
