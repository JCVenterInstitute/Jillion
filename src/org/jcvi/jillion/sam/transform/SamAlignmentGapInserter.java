package org.jcvi.jillion.sam.transform;

import java.util.Iterator;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.ReverseComplementable;
import org.jcvi.jillion.core.residue.nt.INucleotideSequence;
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
	private final ResidueSequence<?,?,?> gappedReference;

	private final GrowableIntArray gapOffsets;
	
	public SamAlignmentGapInserter(ResidueSequence<?,?,?> gappedReference) {
		this.gappedReference = gappedReference;
		this.gapOffsets = new GrowableIntArray(gappedReference.gaps().toArray());
	}
	
	@Data
	public static class Result<R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R,S,B>> {
		private final int gappedStartOffset;
		private final B gappedSequence;
	}

	public <R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R,S,B>> Result<R,S,B> computeExtraInsertions(Cigar cigar, S rawUngappedSequence) {
		return computeExtraInsertions(cigar, rawUngappedSequence, 0, Direction.FORWARD);
	}
	public <R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R,S,B>> Result<R,S,B> computeExtraInsertions(Cigar cigar, S rawUngappedSequence, int ungappedReferenceStartOffset, Direction dir) {
		if(rawUngappedSequence.getNumberOfGaps() !=0){
			throw new IllegalArgumentException("rawUngapped Sequence can not have gaps");
		}
		//give some wiggle room for inserting gaps
		B builder = rawUngappedSequence.newEmptyBuilder((int)rawUngappedSequence.getLength()+20)
												.turnOffDataCompression(true);
		int gappedReferenceOffset = gappedReference.getGappedOffsetFor(ungappedReferenceStartOffset);
		int currentOffset = gappedReferenceOffset;
		Iterator<R> ungappedBasesIter;
		if(dir == Direction.FORWARD){
			ungappedBasesIter= rawUngappedSequence.iterator();
		}else{
			if(rawUngappedSequence instanceof ReverseComplementable) {
				ungappedBasesIter = (Iterator<R>)(((ReverseComplementable)rawUngappedSequence).reverseComplementIterator());
			}else{
				ungappedBasesIter = rawUngappedSequence.reverseIterator();
			}
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
		
		return new Result<>(gappedReferenceOffset, builder);
	}
	
	private <R extends Residue, S extends ResidueSequence<R, S, B>, B extends ResidueSequenceBuilder<R,S,B>> int appendBases(B builder, Iterator<R> ungappedReadBaseIterator, int refOffset, CigarElement e){
		
		int ret = refOffset;
		for(int i=0; i<e.getLength(); i++){
			
			if(e.getOp() != CigarOperation.INSERTION){
				while(gapOffsets.binarySearch(ret) >=0){
					//insert gap
					builder.appendGap();
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
				builder.appendGap();
				
			}else{
				builder.append(ungappedReadBaseIterator.next());			
				
			}
			ret++;
		}
		return ret;
	}
}
