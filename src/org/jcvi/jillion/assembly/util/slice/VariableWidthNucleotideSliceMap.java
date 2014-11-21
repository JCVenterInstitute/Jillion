package org.jcvi.jillion.assembly.util.slice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

		private final List<Range> gappedExons;
		
		
		public Builder(NucleotideSequence gappedReferenceSequence, int ungappedWidthPerSlice){
			this(gappedReferenceSequence, ungappedWidthPerSlice, Range.ofLength(gappedReferenceSequence.getLength()));
		}
		public Builder(NucleotideSequence gappedReferenceSequence, int ungappedWidthPerSlice, Range...gappedExons){
			this.gappedExons = Arrays.asList(gappedExons);
			this.trimmedGappedReferenceSequence = getSplicedSequenceFor(gappedReferenceSequence, 0);

			
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
		private NucleotideSequence getSplicedSequenceFor(
				NucleotideSequence gappedReferenceSequence,
				int startOffset) {
			Range gappedFullReferenceRange = new Range.Builder(gappedReferenceSequence.getLength())
														.shift(startOffset)
														.build();
			//check to make sure we intersect the exons somewhere...
			Optional<Range> intersect =gappedExons.stream()
											.filter(exon -> exon.intersects(gappedFullReferenceRange))
											.findAny();
			if(!intersect.isPresent()){
				//sequence doesn't intersect any exons 
				return new NucleotideSequenceBuilder().build();
				
			}
			List<Range> gappedIntrons = gappedFullReferenceRange.complement(this.gappedExons);
			
			NucleotideSequenceBuilder trimmedGappedReferenceBuilder =new NucleotideSequenceBuilder(gappedReferenceSequence);
			//iterate backwards to avoid having to shift coordinates
			for(int i= gappedIntrons.size()-1; i>=0; i--){
				//have to re-shift by start offset to get into read coord space
				Range refRangeToDelete = gappedIntrons.get(i);
				Range seqRangeToDelete = new Range.Builder(refRangeToDelete)
													.shift(-startOffset)
													.build();
				trimmedGappedReferenceBuilder.delete(seqRangeToDelete);
			}
			
			return trimmedGappedReferenceBuilder.build();
		}
		private NucleotideSequence computeNumberOfGappedBasesReferenceSlice(int ungappedWidthPerSlice, Iterator<Nucleotide> iter){
			NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder(ungappedWidthPerSlice *2);

			while(iter.hasNext() && builder.getUngappedLength() < ungappedWidthPerSlice ){
				builder.append(iter.next());
			}
			return builder.build();
		}
		
		public Builder add(int offset, NucleotideSequence seq){
			
			
			NucleotideSequence splicedSequence = getSplicedSequenceFor(seq, offset);
			if(splicedSequence.getLength() ==0){
				//read entirely spliced out or doesn't intersect
				//with CDS so skip it
				return this;
			}
			int splicedStartOffset = getSplicedStartOffsetFor(offset);
			
			//because our slices may start with gaps, we need to get the right flanking non-gap
			//offset to find the correct first builder bin to use
			int flankingGappedRefOffset =	AssemblyUtil.getRightFlankingNonGapIndex(trimmedGappedReferenceSequence, splicedStartOffset);

			int ungappedStartOffset = trimmedGappedReferenceSequence.getUngappedOffsetFor(flankingGappedRefOffset);
			int currentOffset = ungappedStartOffset/widthPerSlice;
			
			Iterator<Nucleotide> iter = splicedSequence.iterator();
			//handle initial specially to check for leading gaps
			
			
			builders[currentOffset++].addBeginningOfRead(splicedStartOffset, iter);
			
			//handle the rest
			while(iter.hasNext() && currentOffset < builders.length){			
				builders[currentOffset++].add(iter);
				
			}
			
			
			
			return this;
		}
		private int getSplicedStartOffsetFor(int offset) {
			//we need to subtract the gapped length of our introns
			
			Range upstreamOfRead = 	Range.ofLength(offset);
			List<Range> upstreamIntrons = upstreamOfRead.complement(gappedExons);
			if(upstreamIntrons.isEmpty()){
				//the splicedStartOffset doesn't take into account any bases 
				//BEFORE THE exons so we have to still adjust it to get it into
				//exon coordinate space
				//this should be safe since we should always have at least 1 exon
				
				int exonStart = (int) gappedExons.get(0).getBegin();
				//only adjust if offset is >= our first exon start
				if(exonStart <= offset){
					return offset - exonStart;
				}
				return offset;
			}
			
			long intronLength = 0;
			for(Range r : upstreamIntrons){
				intronLength+= r.getLength();
			}
			return offset - (int) intronLength;
		}
		
		public VariableWidthNucleotideSliceMap build(){
			return new VariableWidthNucleotideSliceMap(this);
		}
	}
}
