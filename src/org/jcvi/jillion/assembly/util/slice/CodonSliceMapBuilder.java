package org.jcvi.jillion.assembly.util.slice;

import java.util.Arrays;
import java.util.Objects;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;


public class CodonSliceMapBuilder{

	private VariableWidthNucleotideSliceMap.Builder builder;
	private final RnaEdit rnaEdit;
	
	//TODO support splicing (multiple exons)
	public CodonSliceMapBuilder(NucleotideSequence fullgappedSequence, Range ungappedExon){
		this.builder = new VariableWidthNucleotideSliceMap.Builder(fullgappedSequence, 3,ungappedExon);
		this.rnaEdit = null;
	}
	
	public CodonSliceMapBuilder(NucleotideSequence fullgappedSequence, Range ungappedExon, RnaEdit rnaEdit){
		this.rnaEdit = rnaEdit;
		NucleotideSequence editedReference = rnaEdit.editReference(fullgappedSequence)
													.build();
		Range gappedExon = Range.of(fullgappedSequence.getGappedOffsetFor((int)ungappedExon.getBegin()),
									fullgappedSequence.getGappedOffsetFor((int)ungappedExon.getEnd()));
		Range updatedGappedExon = new Range.Builder(gappedExon)
											  .expandEnd(rnaEdit.getNumberOfBasesAdded())
											  .build();
		this.builder = new VariableWidthNucleotideSliceMap.Builder(editedReference, 3,updatedGappedExon);
	}
	
	public CodonSliceMapBuilder add(int offset, NucleotideSequence seq){
		if(rnaEdit ==null){
			builder.add(offset, seq);
		}else{
			//edit read's sequence
			
			//offset is affected
			//if it is downstream of edit region
			int adjustedStartOffset = rnaEdit.adjustStartOffset(offset);
			NucleotideSequenceBuilder editedReadBuilder = rnaEdit.editRead(
																	seq, 
																	new Range.Builder(seq.getLength())
																				.shift(adjustedStartOffset)
																				.build());
			
			/*int numberOfLeadingGaps = editedReadBuilder.getGappedOffsetFor(0);
			if(numberOfLeadingGaps >0){
				editedReadBuilder.delete(Range.ofLength(numberOfLeadingGaps));
				//have to adjust start offset but by how much?
				//can't be leading number of gaps because that would include
				//entire region
				int numberOfAdjustedBases = adjustedStartOffset - offset;
				adjustedStartOffset +=numberOfLeadingGaps- numberOfAdjustedBases;
				
			}
			*/
			int lastNonGapOffset = editedReadBuilder.getGappedOffsetFor((int)(editedReadBuilder.getUngappedLength() -1));
			long numberOfTrailingGaps = editedReadBuilder.getLength() -	lastNonGapOffset -1;
			if(numberOfTrailingGaps >0){
				editedReadBuilder.delete(new Range.Builder(numberOfTrailingGaps)
											.shift(lastNonGapOffset +1)
											.build());
			}
			
			builder.add(adjustedStartOffset, editedReadBuilder.build());
			
		}
		return this;
	}
	
	public VariableWidthNucleotideSliceMap build(){
		return builder.build();
	}
	
	
	public static class RnaEdit{
		private final Range ungappedRegion;
		private Range gappedCdsRange;
		private final NucleotideSequence inputSequence, editedSequence;

		private final int numberOfBasesAdded;
		
		public RnaEdit(Range ungappedRegion, NucleotideSequence inputSequence, NucleotideSequence editedSequence) {
			Objects.requireNonNull(ungappedRegion);
			Objects.requireNonNull(inputSequence);
			Objects.requireNonNull(editedSequence);
			
			if(ungappedRegion.getLength() != inputSequence.getUngappedLength()){
				throw new IllegalArgumentException("ungapped region does not match ungapped input sequence length "+
			ungappedRegion.getLength() + "  vs  " + inputSequence.getUngappedLength());
			}
			this.ungappedRegion = ungappedRegion;
			this.inputSequence = inputSequence;
			this.editedSequence = editedSequence;
			this.numberOfBasesAdded = (int)(editedSequence.getLength() - inputSequence.getLength());
		}
		
		
		public int getNumberOfBasesAdded() {
			return numberOfBasesAdded;
		}

		public int adjustStartOffset(int oldGappedOffset){
			if(oldGappedOffset > gappedCdsRange.getEnd()){
				//need to shift it downstream by number of added bases
				return oldGappedOffset + numberOfBasesAdded;
			}
			return oldGappedOffset;
			
		}

		//plan:
		//change this method to instead only pass in the seqBuilder
		//which has been trimmed down to the rna edit region
		//this way we will be able to reuse the method for both refs and reads
		//and not have to worry the user about how to change things
		//possibly provide the builder and an extra parameter of the start position
		//or Range of interest that has to be edited.
		protected void doEdit(int offset, NucleotideSequenceBuilder seq){
			//only make edit if matches exactly
			//ignoring gaps
			if(seq.isEqualToIgnoringGaps(inputSequence)){
				
				int[] gaps =seq.getGapOffsets();
				seq.clear()
					.append(editedSequence);
				//add the gaps back in
				//in the same locations
				//to keep alignment
				for(int i=0; i<gaps.length; i++){
					seq.insert(gaps[i], Nucleotide.Gap);
				}
			}else{
				long unGappedLength = seq.getUngappedLength();
				if(unGappedLength ==0){
					//all gaps in this edit region
					//just add more gaps to account for
					//added bases
					char[] gaps = new char[numberOfBasesAdded];
					Arrays.fill(gaps, '-');
					seq.append(gaps);					
				}else{
					//TODO possibly some kind of alignment
					//and adding gaps where appropriate to keep
					//everything in frame...but
					
					//for now just replace with gaps?
					//if we are missing bases this number will be negative
					int numberOfMissingBases = (int)(unGappedLength - inputSequence.getLength());
					
					char[] gaps = new char[seq.getNumGaps() + (int)editedSequence.getLength() - numberOfMissingBases];
					Arrays.fill(gaps, '-');
					seq.clear();
					seq.append(gaps);
				}
			}
			
		}
		protected void doEditMissing(NucleotideSequenceBuilder builder, long numberOfLeadingBasesMissing){
			
			char[] trailingGaps = new char[numberOfBasesAdded];
			Arrays.fill(trailingGaps, '-');
			builder.append(trailingGaps);

		}
		
		public final NucleotideSequenceBuilder editReference(NucleotideSequence ref){
			NucleotideSequenceBuilder builder = ref.toBuilder();
			
			gappedCdsRange = builder.toGappedRange(ungappedRegion);
			NucleotideSequenceBuilder portionToEdit = builder.copy(gappedCdsRange);
			
			doEdit((int)gappedCdsRange.getBegin(),portionToEdit);
			
			
			return builder.replace(gappedCdsRange, portionToEdit);
									
		}
		
		public final NucleotideSequenceBuilder editRead(NucleotideSequence seq, Range gappedRefRange){
			Range intersection = gappedRefRange.intersection(gappedCdsRange);
			if(intersection.isEmpty()){
				//does not intersect
				//so do not make any edits
				return seq.toBuilder();
			}
			NucleotideSequenceBuilder builder = seq.toBuilder();
			Range editRegion = new Range.Builder(intersection)
											.shift(-gappedRefRange.getBegin())
											.build();
			NucleotideSequenceBuilder portionToEdit =builder.copy(editRegion);
			
			long numberOfLeadingBasesMissing = intersection.getBegin() - gappedCdsRange.getBegin(); 
			if(numberOfLeadingBasesMissing >0){
				//we are missing the beginning of the edit region
				doEditMissing(portionToEdit, numberOfLeadingBasesMissing);
			}else{			
				doEdit((int)gappedCdsRange.getBegin(),portionToEdit);
			}
			return builder.replace(editRegion, portionToEdit);
		}
	}
}
