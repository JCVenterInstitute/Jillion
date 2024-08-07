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
/*
 * Created on Nov 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code AssemblyUtil} is a utility class for working
 * with {@link AssembledRead}s and gapped {@link NucleotideSequence}.
 * @author dkatzel
 */
public final class AssemblyUtil {

    private AssemblyUtil(){}
    /**
     * Create a {@link NucleotideSequence} that corresponds to the gapped full range
     * (untrimmed, uncomplemented, gapped) version of the given {@link AssembledRead}.
     * Generally, the returned sequence will be the trimmed off portions of the read
     * (from primer/barcode or bad quality) flanking the gapped sequence
     * from the assembled read (the good quality trimmed sequence returned by
     * {@link AssembledRead#getNucleotideSequence()} ).
     * @param assembledRead the read to work on.
     * @param ungappedUncomplementedFullRangeBases the ungapped uncomplemented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @return a {@link NucleotideSequence} of the gapped, untrimmed uncomplemented
     * basecalls of the given read. 
     */
    public static NucleotideSequence buildGappedComplementedFullRangeBases(AssembledRead assembledRead, NucleotideSequence ungappedUncomplementedFullRangeBases){
    	return buildGappedComplementedFullRangeBases(assembledRead, ungappedUncomplementedFullRangeBases, false);
    }
    /**
     * Create a {@link NucleotideSequence} that corresponds to the gapped full range
     * (untrimmed, uncomplemented, gapped) version of the given {@link AssembledRead}.
     * Generally, the returned sequence will be the trimmed off portions of the read
     * (from primer/barcode or bad quality) flanking the gapped sequence
     * from the assembled read (the good quality trimmed sequence returned by
     * {@link AssembledRead#getNucleotideSequence()} ).
     * @param assembledRead the read to work on.
     * @param ungappedUncomplementedFullRangeBases the ungapped uncomplemented
     * full (raw) version of the basecalls as originally called from the sequencer.
     * @param turnOffCompression Turn off more extreme data compression when making the resulting
     * {@link NucleotideSequence} which will improve cpu performance 
     * at the cost of the built NucleotideSequence taking up more memory.
     * 
     * @return a {@link NucleotideSequence} of the gapped, untrimmed uncomplemented
     * basecalls of the given read. 
     */
    public static NucleotideSequence buildGappedComplementedFullRangeBases(AssembledRead assembledRead, 
    													NucleotideSequence ungappedUncomplementedFullRangeBases,
    												boolean turnOffCompression){
        Direction dir =assembledRead.getDirection();
        Range validRange = assembledRead.getReadInfo().getValidRange();
        NucleotideSequenceBuilder ungappedFullRangeComplimentedBuilder = new NucleotideSequenceBuilder(ungappedUncomplementedFullRangeBases)
        																		.turnOffDataCompression(turnOffCompression);
        if(dir==Direction.REVERSE){
            validRange = AssemblyUtil.reverseComplementValidRange(
                    validRange,
                    ungappedUncomplementedFullRangeBases.getLength());
            ungappedFullRangeComplimentedBuilder.reverseComplement();
            
        }
        //set initial size to 2x ungapped full length 
        //just incase assembly is really gappy.
        //if the sequence is still larger, the builder will dynamically grow
        //but this should cover 99+% of use case without expensive re-sizing.
        NucleotideSequenceBuilder builder = new NucleotideSequenceBuilder((int)( 2* ungappedFullRangeComplimentedBuilder.getLength()));
        builder.append(ungappedFullRangeComplimentedBuilder.copy(new Range.Builder(validRange.getBegin()).build()));
        //need to use read's sequence since that might be gapped
        builder.append(assembledRead.getNucleotideSequence());
        builder.append(ungappedFullRangeComplimentedBuilder.copy(Range.of(validRange.getEnd()+1, ungappedFullRangeComplimentedBuilder.getLength() -1)));
     
        return builder.build();
    }
    /**
     * Get the {@link QualitySequence} for the given
     * read's VALID RANGE taking direction into account.
     * @param read the read; can not be null.
     * @param fullRangeRawQualities the "raw" quality values
     * returned by the sequencer.  These qualities are 
     * <strong>un-complemented</strong> even if the 
     * read is reverse.Can not be null.
     * @return a new {@link QualitySequence} instance
     * which will be trimmed complemented qualities;
     * will never be null.
     * @throws NullPointerException if either parameters is null.
     */
    public static QualitySequence getUngappedComplementedValidRangeQualities(AssembledRead read,QualitySequence fullRangeRawQualities){
    	QualitySequenceBuilder builder = new QualitySequenceBuilder(fullRangeRawQualities);
    	//we need to trim the read before
    	//we consider reverse-ness
    	//otherwise we will trim incorrectly
    	//if we reverse THEN trim
    	builder.trim(read.getReadInfo().getValidRange());
    	if(read.getDirection()==Direction.REVERSE){
    		builder.reverse();
    	}
    	return builder.build();
    }
    
    /**
     * Reverse Complement the given validRange with regards to its fullLength.
     * @param validRange the valid Range to reverseCompliment.
     * @param fullLength the full length of the untrimmed basecalls.
     * @return a new Range that corresponds to the reverse complemented valid range.
     * @throws IllegalArgumentException if valid range is larger than fullLength
     * @throws NullPointerException if validRange is null.
     */
    public static Range reverseComplementValidRange(Range validRange, long fullLength){
        if(validRange ==null){
            throw new NullPointerException("valid range can not be null");
        }
        if(fullLength < validRange.getLength()){
            throw new IllegalArgumentException(
                    String.format("valid range  %s is larger than fullLength %d", validRange, fullLength));
        }
        long newStart = fullLength - validRange.getEnd()-1;
        long newEnd = fullLength - validRange.getBegin()-1;
        return Range.of(newStart, newEnd);
    }

    /**
     * Convert the given gapped valid range offset of a given read into its
     * corresponding ungapped full length (untrimmed) equivalent.
     * 
     * @param placedRead the read to use in the computation.
     * 
     * @param gappedOffset the gapped offset to convert into an ungapped full range offset.
     * 
     * @return the ungapped full range offset as a positive int.
     */
    public static  int convertToUngappedFullRangeOffset(AssembledRead placedRead, int gappedOffset) {
        Range validRange = placedRead.getReadInfo().getValidRange();
        return convertToUngappedFullRangeOffset(placedRead, placedRead.getReadInfo().getUngappedFullLength(),
                gappedOffset, validRange);
    }
    
    private static int convertToUngappedFullRangeOffset(AssembledRead placedRead,
            int fullLength, int gappedOffset, Range validRange) {
       
        
        NucleotideSequence nucleotideSequence = placedRead.getNucleotideSequence();
        final int ungappedOffset;
        if(gappedOffset ==-1){
        	ungappedOffset =-1;
        }else if(gappedOffset == nucleotideSequence.getLength()){
        	ungappedOffset = (int)(nucleotideSequence.getUngappedLength()+1);
        }else{
        	ungappedOffset=nucleotideSequence.getUngappedOffsetFor(gappedOffset);
        }
        if(placedRead.getDirection() == Direction.REVERSE){            
            return (int)(validRange.getEnd() - ungappedOffset);
        }
        return ungappedOffset + (int)validRange.getBegin();
    }
   
    /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the left side of the given
     * gappedReadIndex on the given {@link NucleotideSequence}.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code <= gappedReadIndex};
     * may be negative if the sequence starts with gaps.
     */
    public static int getLeftFlankingNonGapIndex(NucleotideSequence gappedNucleotides, int gappedReadIndex) {
        if(gappedReadIndex< 0){
            return gappedReadIndex;
        }
        if(gappedNucleotides.isGap(gappedReadIndex)){
            return getLeftFlankingNonGapIndex(gappedNucleotides,gappedReadIndex-1);
        }
        
        return gappedReadIndex;
    }
    
    
    /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the right side of the given
     * gappedOffset on the given {@link NucleotideSequence}.  If the given base is not a gap, 
     * then that is the value returned.
     * @param sequence the gapped {@link NucleotideSequence} to search 
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the placedRead that is {@code >= gappedReadIndex}
     */
    public static int getRightFlankingNonGapIndex(NucleotideSequence sequence, int gappedOffset) {
        if(gappedOffset > sequence.getLength() -1 || gappedOffset < 0){
            return gappedOffset;
        }

        if(sequence.isGap(gappedOffset)){
            return getRightFlankingNonGapIndex(sequence,gappedOffset+1);
        }
        return gappedOffset;
    }
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link NucleotideSequence} and gapped {@link Range}.
     * @param gappedSequence the gapped {@link NucleotideSequence} needed
     * to compute the ungapped coordinates from.  
     * @param gappedRange the Range of gapped coordinates.
     * @return a new Range never null.
     * @throws NullPointerException if either argument is null.
     * @throws IndexOutOfBoundsException if the given Range goes beyond
     * the gapped sequence.
     */
    public static Range toUngappedRange(final NucleotideSequence gappedSequence,
            Range gappedRange) {
       return gappedSequence.toUngappedRange(gappedRange);
        
    }
}
