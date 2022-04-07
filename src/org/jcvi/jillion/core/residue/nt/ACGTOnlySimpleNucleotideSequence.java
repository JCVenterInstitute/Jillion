package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;

import org.jcvi.jillion.internal.core.util.GrowableByteArray;

import java.util.*;

/**
 * A simple NucleotideSequence implementation that doesn't
 * waste resources compressing the nucleotide data
 * but only contains ACG and T bases so methods querying for gaps or Ns
 * are trivial to compute.
 * 
 * @author dkatzel
 * @since 6.0
 */
class ACGTOnlySimpleNucleotideSequence extends AbstractSimpleNucleotideSequence{

   

    public ACGTOnlySimpleNucleotideSequence(GrowableByteArray data) {
       super(data);
    }
    public ACGTOnlySimpleNucleotideSequence(Nucleotide[] data) {
    	super(data);
        
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
	public long getUngappedLength() {
		return getLength();
	}
	@Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return gappedValidRangeIndex;
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
	public List<Range> getRangesOfNs() {
		return Collections.emptyList();
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
	public boolean isAllNs() {
		return false;
	}
	@Override
	public double getPercentN() {
		return 0D;
	}
	@Override
    public NucleotideSequence createNewInstance(Nucleotide[] trimRange) {
        return new ACGTOnlySimpleNucleotideSequence(trimRange);
    }
}
