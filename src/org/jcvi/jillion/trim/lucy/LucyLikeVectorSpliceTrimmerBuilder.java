package org.jcvi.jillion.trim.lucy;

import java.io.IOException;
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

public class LucyLikeVectorSpliceTrimmerBuilder implements Builder<NucleotideTrimmer>{

	private static final SearchArea DEFAULT_AREA_1 = new SearchArea(Range.ofLength(40), 8);
	private static final SearchArea DEFAULT_AREA_2 = new SearchArea(new Range.Builder(60)
																	.shift(DEFAULT_AREA_1.range.getEnd()+1)
																	.build(),
																	12);
	private static final SearchArea DEFAULT_AREA_3 = new SearchArea(new Range.Builder(100)
																	.shift(DEFAULT_AREA_2.range.getEnd()+1)
																	.build(),
																	16);
	
	private static final int DEFAULT_GAP_OPEN = -17;
	private static final int DEFAULT_GAP_EXTENSION = -5;
	private static final NucleotideSubstitutionMatrix DEFAULT_MATRIX;
	
	static{
		try {
			DEFAULT_MATRIX = NucleotideSubstitutionMatrices.parsePropertyFile(LucyLikeVectorSpliceTrimmerBuilder.class.getResourceAsStream("lucy.matrix"));
		} catch (IOException e) {
			throw new IllegalStateException("error parsing lucy subsitution matrix file",e);
		}
	}
	
	private final NucleotideSequence upstreamSpliceSeq, downstreamSpliceSeq;
	
	private NucleotideSubstitutionMatrix matrix = DEFAULT_MATRIX;
	private int gapOpen = DEFAULT_GAP_OPEN;
	private int gapExtension = DEFAULT_GAP_EXTENSION;
	
	private SearchArea area1 = DEFAULT_AREA_1;
	private SearchArea area2 = DEFAULT_AREA_2;
	private SearchArea area3 = DEFAULT_AREA_3;
	
	private boolean checkBothDirections = true;
	
	/**
	 * Create a new Builder object with the given splice site sequences.
	 * 
	 * @param upstreamSpliceSeq the vector splice site upstream of the desired sequence;
	 * can not be null.
	 * @param downstreamSpliceSeq the vector splice site downstream of the desired sequence;
	 * can not be null.
	 * 
	 * @throws NullPointerException if either splice sequence is null.
	 */
	public LucyLikeVectorSpliceTrimmerBuilder(NucleotideSequence upstreamSpliceSeq, NucleotideSequence downstreamSpliceSeq) {
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
	public LucyLikeVectorSpliceTrimmerBuilder onlyCheckForwardDirection(){
		checkBothDirections = false;
		return this;
	}
	
	@Override
	public NucleotideTrimmer build() {
		return new LucyLikeVectorSpliceTrimmer(this);
	}

	private static final class LucyLikeVectorSpliceTrimmer implements NucleotideTrimmer{

		private final NucleotideSequence upstreamSpliceSeq, downstreamSpliceSeq;
		private final SearchArea area1, area2, area3;
		private final NucleotideSubstitutionMatrix matrix;
		private final int gapOpen;
		private final int gapExtension;
		private final Range upstreamSearchArea;
		
		private final boolean checkBothDirections;
		
		private LucyLikeVectorSpliceTrimmer(LucyLikeVectorSpliceTrimmerBuilder builder){
			upstreamSpliceSeq = builder.upstreamSpliceSeq;
			downstreamSpliceSeq = builder.downstreamSpliceSeq;
			
			area1 = builder.area1;
			area2 = builder.area2;
			area3 = builder.area3;
			
			matrix = builder.matrix;
			gapOpen = builder.gapOpen;
			gapExtension = builder.gapExtension;
			
			upstreamSearchArea = Range.ofLength(area3.range.getEnd()+1);
			
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
			//lucy paper fig 6 implies area 3 parameters used to determine downstream hit
			//which makes sense since it should be in high quality sanger area
			if(subjectRange.getLength() >= area3.minAlignmentLength){
				//have to shift range to compensate for amount we trimmed off
				return new Range.Builder(subjectRange)
								.shift(shiftAmount)
								.build();
			}
			return null;
		}

		private Range findUpstreamVectorHit(NucleotideSequence seq) {
			
			NucleotideSequence subseq = seq.toBuilder()
											.trim(upstreamSearchArea)
											.build();
			//uses adaptive alignment as described in the Lucy paper
			NucleotidePairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(upstreamSpliceSeq, subseq, matrix)
																						.gapPenalty(gapOpen, gapExtension)
																						.useLocalAlignment()
																						.build();
			
			Range subjectRange = alignment.getSubjectRange().getRange();
			if(subjectRange.intersects(area3.range) && subjectRange.getLength() >= area3.minAlignmentLength){
					//found alignment!
					return subjectRange;
			}else if(subjectRange.intersects(area2.range) && subjectRange.getLength() >= area2.minAlignmentLength){
				//found alignment!
				return subjectRange;
			}else if(subjectRange.intersects(area1.range) && subjectRange.getLength() >= area1.minAlignmentLength){
				//found alignment!
				return subjectRange;
			}
			//either didn't find any alignment or the alignment was too small
			//for the adapted min length
			
			//search through the rest of the sequence looking for any hit >=75% ident
			NucleotidePairwiseSequenceAlignment downstreamAlignment = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(upstreamSpliceSeq, seq, matrix)
					.gapPenalty(gapOpen, gapExtension)
					.useLocalAlignment()
					.build();
			
			if(downstreamAlignment.getSubjectRange().getRange().getLength()>= area3.minAlignmentLength &&  downstreamAlignment.getPercentIdentity() >= .75){
				return downstreamAlignment.getSubjectRange().getRange();
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

	
}
