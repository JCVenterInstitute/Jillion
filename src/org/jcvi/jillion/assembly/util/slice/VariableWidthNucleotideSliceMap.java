package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class VariableWidthNucleotideSliceMap implements VariableWidthSliceMap<Nucleotide, NucleotideSequence>{

	private final VariableWidthNucleotideSlice[] slices;
	private final int widthPerSlice;

	private final NucleotideSequence gappedReferenceSequence;
	
	private VariableWidthNucleotideSliceMap(Builder builder){
		this.widthPerSlice = builder.widthPerSlice;
		this.gappedReferenceSequence = builder.trimmedGappedReferenceSequence;
		
		slices = new VariableWidthNucleotideSlice[builder.builders.length];
		
		for(int i=0; i<slices.length; i++){
			slices[i] = builder.builders[i].build();
		}
	}
	
	@Override
	public VariableWidthSlice<Nucleotide, NucleotideSequence> getSlice(int offset) {
		return slices[offset];
	}

	@Override
	public int getConsensusLength() {
		return (int) gappedReferenceSequence.getLength();
	}
	
	public Stream<VariableWidthSlice<Nucleotide, NucleotideSequence>> getSlicesThatIntersectGapped(Range gappedRange){
		
		int startSliceOffset = gappedReferenceSequence.getUngappedOffsetFor((int)gappedRange.getBegin()) /widthPerSlice;
		int endSliceOffset = gappedReferenceSequence.getUngappedOffsetFor((int)gappedRange.getEnd()) /widthPerSlice;
		
		List<VariableWidthSlice<Nucleotide, NucleotideSequence>> list = new ArrayList<>(endSliceOffset-startSliceOffset +1);
		for(int i= Math.max(0, startSliceOffset); i< slices.length && i <=endSliceOffset; i++){
			list.add(slices[i]);
		}
		return list.stream();
	}
	
	public Stream<VariableWidthSlice<Nucleotide, NucleotideSequence>> getSlicesThatIntersectUngapped(Range ungappedRange){
		
		int startSliceOffset = (int) (ungappedRange.getBegin() /widthPerSlice);
		int endSliceOffset = (int) (ungappedRange.getEnd() /widthPerSlice);
		
		List<VariableWidthSlice<Nucleotide, NucleotideSequence>> list = new ArrayList<>(endSliceOffset-startSliceOffset +1);
		for(int i= Math.max(0, startSliceOffset); i< slices.length && i <=endSliceOffset; i++){
			list.add(slices[i]);
		}
		return list.stream();
	}
	
	
	@Override
	public int getNumberOfSlices() {
		return slices.length;
	}
	
	
	public static class Builder{
		private final VariableWidthNucleotideSlice.Builder[] builders;
		private final int widthPerSlice;
		private final NucleotideSequence trimmedGappedReferenceSequence;
		private final Range gappedInclusiveRange;
		
		
		public Builder(NucleotideSequence gappedReferenceSequence, int ungappedWidthPerSlice){
			this(gappedReferenceSequence, ungappedWidthPerSlice, Range.ofLength(gappedReferenceSequence.getLength()));
		}
		public Builder(NucleotideSequence gappedReferenceSequence, int ungappedWidthPerSlice, Range gappedIncludeRange){

			this.trimmedGappedReferenceSequence = new NucleotideSequenceBuilder(gappedReferenceSequence)
															.trim(gappedIncludeRange)
															.build();
			this.gappedInclusiveRange = gappedIncludeRange;
			
			long ungappedLength = this.trimmedGappedReferenceSequence.getUngappedLength();
			if(ungappedWidthPerSlice > ungappedLength){
				throw new IllegalArgumentException("width per slice must be less than ungapped length");
			}
			long rem = ungappedLength % ungappedWidthPerSlice;
			if(rem !=0){
				throw new IllegalArgumentException("ungapped width per slice (" +ungappedWidthPerSlice + 
						") must be a factor of the ungapped sequence length " + ungappedLength);
			}
			int numberOfSlices = (int) (ungappedLength/ungappedWidthPerSlice);

			this.widthPerSlice = ungappedWidthPerSlice;
			builders = new VariableWidthNucleotideSlice.Builder[numberOfSlices];
			
			Iterator<Nucleotide> iter = this.trimmedGappedReferenceSequence.iterator();
			int i=0;
			int currentGappedOffset=0;
			while(iter.hasNext()){
				NucleotideSequence gappedRefSubSeq = computeNumberOfGappedBasesReferenceSlice(ungappedWidthPerSlice, iter);
				builders[i++] =  new VariableWidthNucleotideSlice.Builder(gappedRefSubSeq,currentGappedOffset);
				currentGappedOffset += (int) gappedRefSubSeq.getLength();
			}
			
		}
		private NucleotideSequence computeNumberOfGappedBasesReferenceSlice(int ungappedWidthPerSlice, Iterator<Nucleotide> iter){
			NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(ungappedWidthPerSlice *2);

			while(iter.hasNext() && builder.getUngappedLength() < ungappedWidthPerSlice ){
				builder.append(iter.next());
			}
			return builder.build();
		}
		
		public Builder add(int offset, NucleotideSequence seq){
			Range readRange = new Range.Builder(seq.getLength())
									.shift(offset)
									.build();
			Range intersectedRange = readRange.intersection(gappedInclusiveRange);
			if(intersectedRange.isEmpty()){
				//don't add
				return this;
			}
			
			Range readTrimRange = new Range.Builder(intersectedRange.getLength())
											.shift(intersectedRange.getBegin() -offset)
											.build();
			NucleotideSequence trimmedSeq = new NucleotideSequenceBuilder(seq)
													.trim(readTrimRange)
													.build();
			int adjustedTrimmedGappedRefOffset = (int) (intersectedRange.getBegin() - gappedInclusiveRange.getBegin());
			//because our slices may start with gaps, we need to get the right flanking non-gap
			//offset to find the correct first builder bin to use
			int flankingGappedRefOffset =	AssemblyUtil.getRightFlankingNonGapIndex(trimmedGappedReferenceSequence, adjustedTrimmedGappedRefOffset);
			int ungappedStartOffset = trimmedGappedReferenceSequence.getUngappedOffsetFor(flankingGappedRefOffset);
			int currentOffset = ungappedStartOffset/widthPerSlice;
			
			Iterator<Nucleotide> iter = trimmedSeq.iterator();
			//handle initial specially to check for leading gaps
			
			
			builders[currentOffset++].addBeginningOfRead(adjustedTrimmedGappedRefOffset, iter);
			/*
			int frame = ungappedStartOffset % widthPerSlice +1; 
			if(frame!=1){
				builders[currentOffset++].skipBases(frame, iter);	
			}else{
				//just because we start in frame 1
				//doesn't mean we start at the beginning of the slice bin
				//the slice could start with gaps and we could start 
				//beyond those gaps
				int ungappedCodonOffset = currentOffset* widthPerSlice;
				int firstGappedOffsetOfCodon = ungappedCodonOffset ==0 ? 0: trimmedGappedReferenceSequence.getGappedOffsetFor(ungappedCodonOffset -1) +1;
				if(firstGappedOffsetOfCodon !=adjustedTrimmedGappedRefOffset){
					//skip the entire codon!
					int numberOfPadsToAdd = adjustedTrimmedGappedRefOffset - firstGappedOffsetOfCodon;
					List<Nucleotide> l = new ArrayList<>(numberOfPadsToAdd);
					for(int i=0; i< numberOfPadsToAdd; i++){
						l.add(Nucleotide.Gap);
					}
					iter = IteratorUtil.createChainedIterator(Arrays.asList(l.iterator(), iter));
					builders[currentOffset++].skipBases(iter);	
				}
			}
			
			*/
			//handle the rest
			while(iter.hasNext() && currentOffset < builders.length){			
				builders[currentOffset++].add(iter);
				
			}
			
			return this;
		}
		
		public VariableWidthNucleotideSliceMap build(){
			return new VariableWidthNucleotideSliceMap(this);
		}
	}
}
