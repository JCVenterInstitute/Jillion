package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;

import java.util.*;
import java.util.PrimitiveIterator.OfInt;
import java.util.stream.IntStream;

/**
 * A simple NucleotideSequence implementation that doesn't
 * waste resources compressing the nucleotide data
 * but only contains ACGT and N bases so methods querying for gaps
 * are trivial to compute.
 * 
 * @author dkatzel
 * @since 6.0
 */
class ACGTNOnlySimpleNucleotideSequence extends AbstractSimpleNucleotideSequence{

   

    private static final long serialVersionUID = 7577069593805742567L;

	public ACGTNOnlySimpleNucleotideSequence(GrowableByteArray data) {
       super(data);
    }
    public ACGTNOnlySimpleNucleotideSequence(Nucleotide[] data) {
    	super(data);
        
    }
    
    
    private static class NoGapsLeftIterator implements OfInt{

    	private int current;
    	
    	public NoGapsLeftIterator(int gappedOffset) {
    		this.current = gappedOffset;
    	}
		@Override
		public boolean hasNext() {
			return current>=0;
		}

		@Override
		public int nextInt() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			return current--;
		}
    	
    }

    @Override
	public NucleotideSequence computeUngappedSequence() {
		return this;
	}
	@Override
	public int getLeftFlankingNonGapOffsetFor(int gappedOffset) {
		return gappedOffset;
	}
	@Override
	public OfInt createLeftFlankingNonGapIterator(int startingGapOffset) {
		return new NoGapsLeftIterator(startingGapOffset);
	}
	@Override
	public int getRightFlankingNonGapOffsetFor(int gappedOffset) {
		return gappedOffset;
	}
	@Override
	public Range getExpandingFlankingNonGapRangeFor(Rangeable gappedRange) {
		return gappedRange.asRange();
	}
	@Override
	public Range getContractingFlankingNonGapRangeFor(Rangeable gappedRange) {
		return gappedRange.asRange();
	}
	@Override
	public Range getExpandingFlankingNonGapRangeFor(int gappedBeginOffset, int gappedEndOffset) {
		return Range.of(gappedBeginOffset, gappedEndOffset);
	}
	@Override
	public Range getContractingFlankingNonGapRangeFor(int gappedBeginOffset, int gappedEndOffset) {
		return Range.of(gappedBeginOffset, gappedEndOffset);
	}
	@Override
	public int getUngappedOffsetForSafe(int gappedOffset) {
		return gappedOffset;
	}
    
	@Override
	public Range toUngappedRange(Range gappedRange) {
		return gappedRange;
	}
	@Override
	public Range toGappedRange(Range ungappedRange) {
		return ungappedRange;
	}
	@Override
	public boolean isAllGapsOrBlank() {
		return getLength()==0L;
	}
	@Override
	public boolean hasGaps() {
		return false;
	}
	
	@Override
	public long getUngappedLength() {
		return getLength();
	}
	@Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return 0;
	}
	@Override
	public int getUngappedOffsetFor(int gappedOffset) {
		return gappedOffset;
	}
	@Override
	public int getGappedOffsetFor(int ungappedOffset) {
		return ungappedOffset;
	}
	
	@Override
	public boolean isDna() {
		return true;
	}
	@Override
	public boolean isRna() {
		return false;
	}
	@Override
	public List<Integer> getGapOffsets() {
		return Collections.emptyList();
	}
	@Override
	public IntStream gaps() {
		return IntStream.empty();
	}
	@Override
	public List<Range> getRangesOfGaps() {
		return Collections.emptyList();
	}
	@Override
	public int getNumberOfGaps() {
		return 0;
	}
	@Override
	public boolean isGap(int gappedOffset) {
		return false;
	}
	
	@Override
    public NucleotideSequence createNewInstance(Nucleotide[] trimRange) {
        return new ACGTNOnlySimpleNucleotideSequence(trimRange);
    }
}
