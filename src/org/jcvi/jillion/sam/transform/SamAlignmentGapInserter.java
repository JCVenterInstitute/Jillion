package org.jcvi.jillion.sam.transform;

import java.util.Iterator;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.cigar.CigarElement;
import org.jcvi.jillion.sam.cigar.CigarOperation;

import lombok.Data;
/**
 * Adds Gaps to read alignments to keep SAM/BAM mapped reads aligned to each other.
 * 
 * This is mostly used by the {@link SamTransformationService} but has been factored
 * out in case other application need access to the same algorithm.
 * 
 * @author dkatzel
 *
 * @since 6.0
 */
public class SamAlignmentGapInserter {
	private final NucleotideSequence gappedReference;

	private final GrowableIntArray gapOffsets;
	
	public SamAlignmentGapInserter(NucleotideSequence gappedReference) {
		this.gappedReference = gappedReference;
		this.gapOffsets = new GrowableIntArray(gappedReference.gaps().toArray());
	}
	
	@Data
	public static class Result{
		private final int gappedStartOffset;
		private final NucleotideSequenceBuilder gappedSequence;
	}
	public Result computeExtraInsertions(Cigar cigar, NucleotideSequence rawUngappedSequence, int ungappedReferenceStartOffset, Direction dir) {
		if(rawUngappedSequence.getNumberOfGaps() !=0){
			throw new IllegalArgumentException("rawUngapped Sequence can not have gaps");
		}
		//give some wiggle room for inserting gaps
		NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder((int)rawUngappedSequence.getLength()+20)
												.turnOffDataCompression(true);
		int gappedReferenceOffset = gappedReference.getGappedOffsetFor(ungappedReferenceStartOffset);
		int currentOffset = gappedReferenceOffset;
		Iterator<Nucleotide> ungappedBasesIter;
		if(dir == Direction.FORWARD){
			ungappedBasesIter= rawUngappedSequence.iterator();
		}else{
			ungappedBasesIter= rawUngappedSequence.reverseComplementIterator();
		}
		for(CigarElement e : cigar){
			if(e.getOp().isClip() ){
				//skip over clipped bases
				for(int i=0; i<e.getLength(); i++){
					ungappedBasesIter.next();
				}
				continue;
			}
			currentOffset = appendBases(builder, ungappedBasesIter, currentOffset, e);
			
		}
		
		return new Result(gappedReferenceOffset, builder);
	}
	
	private int appendBases(NucleotideSequenceBuilder builder, Iterator<Nucleotide> ungappedReadBaseIterator, int refOffset, CigarElement e){
		
		int ret = refOffset;
		for(int i=0; i<e.getLength(); i++){
			
			if(e.getOp() != CigarOperation.INSERTION){
				while(gapOffsets.binarySearch(ret) >=0){
					//insert gap
					builder.append(Nucleotide.Gap);
					ret++;
				}
			}
			if(e.getOp() == CigarOperation.PADDING){
				//remove this many gaps
				builder.delete(new Range.Builder(1)
										.shift(builder.getLength()-1)
										.build());
			}
			else if(e.getOp() ==CigarOperation.DELETION ||e.getOp() == CigarOperation.SKIPPED){
				//insert gap
				builder.append(Nucleotide.Gap);
				
			}else{
				builder.append(ungappedReadBaseIterator.next());			
				
			}
			ret++;
		}
		return ret;
	}
}
