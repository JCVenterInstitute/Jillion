package org.jcvi.jillion.assembly;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegion;
import org.jcvi.jillion.assembly.clc.cas.CasAlignmentRegionType;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

public class ReAlignReads {

	private final NucleotideSequence gappedReference;
	private final GrowableIntArray referenceGaps;
	
	private final boolean referenceEncode;
	public ReAlignReads(NucleotideSequence gappedReference){
		this(gappedReference, false);
	}
	public ReAlignReads(NucleotideSequence gappedReference, boolean referenceEncode){
		this.gappedReference = gappedReference;
		this.referenceGaps = new GrowableIntArray(gappedReference.getGapOffsets());
		this.referenceEncode = referenceEncode;
	}
	
	
	
	public ReAlignResult realignValidBases(NucleotideSequence fullLengthUngappedRead,
			long ungappedStartOffset, Direction dir, List<CasAlignmentRegion> alignmentRegions){
		return realignValidBases(fullLengthUngappedRead, ungappedStartOffset, dir, alignmentRegions, null);
	}
	
	public ReAlignResult realignValidBases(NucleotideSequence fullLengthUngappedRead,
			long ungappedStartOffset, Direction dir, List<CasAlignmentRegion> alignmentRegions, Range validRange){
		 NucleotideSequenceBuilder validUngappedBases = new NucleotideSequenceBuilder(fullLengthUngappedRead);
	        if(validRange!=null){
	        	validUngappedBases.trim(validRange);
	        }
	        long validRangeStart;													
	        long fullUngappedLength = fullLengthUngappedRead.getLength();
			if(dir==Direction.REVERSE){
	        	validUngappedBases.reverseComplement();
	            validRangeStart = validRange ==null?0:AssemblyUtil.reverseComplementValidRange(validRange, fullUngappedLength).getBegin();
	        }
	        else{
	            validRangeStart = validRange ==null?0:validRange.getBegin();
	        }
	        
	        NucleotideSequenceBuilder gappedValidSequenceBuilder = new NucleotideSequenceBuilder((int)(validUngappedBases.getLength() *2));
	        
	        boolean outsideValidRange = true;
	        long gappedStartOffset = gappedReference.getGappedOffsetFor((int)ungappedStartOffset);
	        long referenceOffset =gappedStartOffset;
	        long currentOffset= 0;
	        for(CasAlignmentRegion region : getRegionsToConsider(alignmentRegions)){
	        	 CasAlignmentRegionType type =region.getType();
	             
	             if(outsideValidRange){
	                 if(type ==CasAlignmentRegionType.INSERT){
	                     validRangeStart+=region.getLength();
	                  
	                   
	                     currentOffset+=region.getLength();
	                     continue;
	                 }           
	                 outsideValidRange=false;
	             }
	             
	             long allBasesLength = validUngappedBases.getUngappedLength();
	             if(currentOffset + region.getLength() > allBasesLength){
	             	throw new IllegalStateException(
	             			String.format("alignment region %s extends beyond read; (current offset = %d total read length = %d)", 
	             					region, currentOffset,allBasesLength));
	             }
	             for(long i=0; i< region.getLength();i++){
	                 if(type != CasAlignmentRegionType.INSERT){
	                     //add any extra gaps we added to the reference
	                 	//reference should not have any initial
	                 	//gaps so any gaps we see we put there during
	                 	//the 1st pass to build a gapped alignment.
	                     while(referenceGaps.binarySearch((int)referenceOffset) >=0){
	                         gappedValidSequenceBuilder.append(Nucleotide.Gap);
	                         referenceOffset++;
	                     }
	                 }
	                 if(type == CasAlignmentRegionType.DELETION){
	                     gappedValidSequenceBuilder.append(Nucleotide.Gap);
	                     referenceOffset++;
	                 }
	                 else{      
	                     gappedValidSequenceBuilder.append(validUngappedBases.get((int)(currentOffset+i)));
	                     referenceOffset++;
	                 }
	                 
	             }//end for
	             if(type != CasAlignmentRegionType.DELETION){
	                 currentOffset+=region.getLength();
	             }
	        }
	        NucleotideSequence gappedValidBases;
	        if(referenceEncode){
	        	gappedValidBases = gappedValidSequenceBuilder
	        			.setReferenceHint(gappedReference, (int)gappedStartOffset)
	        			.buildReferenceEncodedNucleotideSequence();
	        }else{
	        	gappedValidBases = gappedValidSequenceBuilder
	        							.turnOffDataCompression(true)
	        							.build();
	        }
	        
	        Range newValidRange = new Range.Builder(gappedValidSequenceBuilder.getUngappedLength())
													.shift(validRangeStart)
													.build();
			if(dir==Direction.REVERSE){
				newValidRange = AssemblyUtil.reverseComplementValidRange(newValidRange, fullUngappedLength);
			}
	        return new ReAlignResult(gappedValidBases, newValidRange, (int)gappedStartOffset );
	        
	}

	private List<CasAlignmentRegion> getRegionsToConsider(List<CasAlignmentRegion> regions){
		List<CasAlignmentRegion> regionsToConsider = new ArrayList<CasAlignmentRegion>(regions);
        int lastIndex = regionsToConsider.size()-1;
        while(regionsToConsider.get(lastIndex).getType()==CasAlignmentRegionType.INSERT){
            regionsToConsider.remove(lastIndex);
            lastIndex--;
        }
        return regionsToConsider;
	}
	
	public static final class ReAlignResult{
		private final NucleotideSequence gappedValidBases;
		private final Range validRange;
		private final int gappedStartOffset;
		private ReAlignResult(NucleotideSequence gappedValidBases,
				Range validRange, int gappedStartOffset) {
			this.gappedValidBases = gappedValidBases;
			this.validRange = validRange;
			this.gappedStartOffset = gappedStartOffset;
		}

		
		public int getGappedStartOffset() {
			return gappedStartOffset;
		}


		public NucleotideSequence getGappedValidBases() {
			return gappedValidBases;
		}

		public Range getValidRange() {
			return validRange;
		}

		@Override
		public String toString() {
			return "ReAlignResult [gappedStartOffset=" + gappedStartOffset
					+ ", gappedValidBases=" + gappedValidBases
					+ ", validRange=" + validRange + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + gappedStartOffset;
			result = prime
					* result
					+ gappedValidBases.hashCode();
			result = prime * result
					+ validRange.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReAlignResult other = (ReAlignResult) obj;
			if (gappedStartOffset != other.gappedStartOffset)
				return false;
			if (!gappedValidBases.equals(other.gappedValidBases))
				return false;
			if (!validRange.equals(other.validRange))
				return false;
			return true;
		}
		
		
	}
}
