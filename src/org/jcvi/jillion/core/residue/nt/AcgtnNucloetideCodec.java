/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;

/**
 * {@code AcgtnNucloetideCodec} is a special version
 * of {@link TwoBitEncodedNucleotideCodec} that
 * does not have any gaps.  This makes
 * the computations of gap locations and number of gaps etc
 * trivial to compute (and can be hard coded).
 * @author dkatzel
 *
 *
 */
final class AcgtnNucloetideCodec extends AbstractTwoBitEncodedNucleotideCodec{

    public static final AcgtnNucloetideCodec INSTANCE = new AcgtnNucloetideCodec();
    /**
     * @param sententialBase
     */
    private AcgtnNucloetideCodec() {
        super(Nucleotide.Unknown);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<Integer> getGapOffsets(byte[] encodedGlyphs) {
        return Collections.emptyList();
    }
    
    


	@Override
    public Range toUngappedRange(byte[] encodedData, Range gappedRange) {
	ensureWithinSequence(encodedData, gappedRange);
        return gappedRange;
    }

    private void ensureWithinSequence(byte[] encodedData, Range gappedRange) {
        int length = decodedLengthOf(encodedData);
	    if(gappedRange.getBegin() < 0 || gappedRange.getEnd() < 0 || length < gappedRange.getEnd()){
	        throw new IndexOutOfBoundsException("range "+ gappedRange + " is out of beyond the boundaries of seq with length "+ length);
	    }
    }

    @Override
    public Range toGappedRange(byte[] encodedData, Range ungappedRange) {
        ensureWithinSequence(encodedData, ungappedRange);
        return ungappedRange;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGaps(byte[] encodedGlyphs) {
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isGap(byte[] encodedGlyphs, int gappedOffset) {
        return false;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public long getUngappedLength(byte[] encodedGlyphs) {
        return decodedLengthOf(encodedGlyphs);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getNumberOfGapsUntil(byte[] encodedGlyphs, int gappedOffset) {
        return 0;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getUngappedOffsetFor(byte[] encodedGlyphs, int gappedOffset) {
        return gappedOffset;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getGappedOffsetFor(byte[] encodedGlyphs, int ungappedOffset) {
        return ungappedOffset;
    }

	@Override
	public List<Range> getNRanges(byte[] encodedData) {
		//the sentinel is N
		return Ranges.asRanges(getSentinelOffsets(encodedData).toArray());
		
	}

	@Override
	public List<Range> getGapRanges(byte[] encodedData) {
		//no gaps
		return Collections.emptyList();
	}

	@Override
	public IntStream getGapOffsetsAsStream(byte[] encodedData) {
		return IntStream.empty();
	}
	

}
