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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.PrimitiveIterator.OfInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.internal.core.GlyphCodec;
import org.jcvi.jillion.internal.core.io.StreamUtil;

/**
 * @author dkatzel
 *
 *
 */
interface NucleotideCodec extends GlyphCodec<Nucleotide>{

	
    byte[] encode(int numberOfNucleotides,int[] gapOffsets, Iterator<Nucleotide> nucleotides);

    /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by getNumberOfGaps(byte[]).
     * 
     * @return a List of gap offsets as Integers.
     */
    List<Integer> getGapOffsets(byte[] encodedData);
    /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by getNumberOfGaps(byte[]).
     * 
     * @return an {@link java.util.stream.IntStream} gap offsets.
     * 
     * @since 6.0
     */
   IntStream getGapOffsetsAsStream(byte[] encodedData); 
    /**
     * 
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps(byte[] encodedData);
   
    /**
     * Is the {@link Nucleotide} at the given gapped index a gap?
     * @param gappedOffset the gappedOffset to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(byte[] encodedData,int gappedOffset);
    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength(byte[] encodedData);

    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence}.
     * @return the number of  bases as a long.
     * @since 5.3
     */
    long getLength(byte[] encodedData);

    /**
     * Compute the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     * @param gappedOffset the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     */
    int getNumberOfGapsUntil(byte[] encodedData,int gappedOffset);
    /**
     * Get the ungapped offset equivalent of the 
     * given gapped offset.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param gappedOffset the gapped offset to use 
     * to compute the ungapped offset.
     * @return an int representing the ungapped
     * offset; will always be >=0.
     */
    int getUngappedOffsetFor(byte[] encodedData,int gappedOffset);
    
    /**
     * Get the gapped offset equivalent of the 
     * given ungapped offset.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param ungappedOffset the ungapped offset to use 
     * to compute the gapped offset.
     * @return an int representing the gapped
     * offset; will always be >=0.
     */
    int getGappedOffsetFor(byte[] encodedData,int ungappedOffset);
    
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link Range}.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param gappedRange the Range of gapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * @throws IndexOutOfBoundsException if the given Range goes beyond
     * the gapped sequence.
     * 
     * @since 5.2
     */
    default Range toUngappedRange(byte[] encodedData, Range gappedRange){
       
        if(gappedRange ==null){
            throw new NullPointerException("gappedRange can not be null");
        }
        return Range.of(
                getUngappedOffsetFor(encodedData,(int)gappedRange.getBegin()),
                getUngappedOffsetFor(encodedData, (int)gappedRange.getEnd())
                );
    }
    
    /**
     * Get the corresponding gapped Range (where the start and end values
     * of the range are in gapped coordinate space) for the given
     * ungapped {@link Range}.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param ungappedRange the Range of ungapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * 
     * @since 5.2
     */
    default Range toGappedRange(byte[] encodedData, Range ungappedRange){
        if(ungappedRange ==null){
            throw new NullPointerException("ungappedRange can not be null");
        }
        return Range.of(
                getGappedOffsetFor(encodedData, (int)ungappedRange.getBegin()),
                getGappedOffsetFor(encodedData, (int)ungappedRange.getEnd())
                );
    }
    
    /**
     * Convenience method to encode a single nucleotide.
     * @param nucleotide The single base to encode.
     *
     * @return the byte array which encodes the single given nucleotide.
     */
    byte[] encode(Nucleotide nucleotide);
    /**
     * Creates a new {@link Iterator}
     * in an efficient manner.
     * @return an {@link Iterator} of all the {@link Nucleotide}s.
     */
    Iterator<Nucleotide> iterator(byte[] encodedData);
    
    /**
     * Create a new {@link Iterator}
     * which only iterates over the specified
     * Range of elements in this sequence.
     * @param range the range to iterate over.
     * @return a new {@link Iterator}; will never
     * be null.
     * @throws NullPointerException if range is null.
     * @throws IndexOutOfBoundsException if Range contains
     * values outside of the possible sequence offsets.
     */
    Iterator<Nucleotide> iterator(byte[] encodedData, Range range);
    /**
     * Convert the encoded bytes into a String
     * of Nucleotides.
     * @param encodedData the byte array of encoded
     * nucleotide sequence data.
     * @return a new String that represents the 
     * same Nucleotide sequence; will never be null.
     */
    String toString(byte[] encodedData);
    
    default String toString(byte[] encodedData, Range subRange){
        StringBuilder builder = new StringBuilder((int) subRange.getLength());
        Iterator<Nucleotide> iter = iterator(encodedData, subRange);
        while(iter.hasNext()){
            builder.append(iter.next());
        }
        return builder.toString();
    }
    
    List<Range> getNRanges(byte[] encodedData);
    
    default double getPercentN(byte[] encodedData) {
    	long ungappedLength = getUngappedLength(encodedData);
    	if(ungappedLength ==0L) {
    		return 0D;
    	}
    	long numN = getNRanges(encodedData).stream().mapToLong(r-> r.getLength()).sum();
    	return numN / (double)ungappedLength;
    }
    
    default List<Range> getGapRanges(byte[] encodedData){
    	return Ranges.asRanges(getGapOffsetsAsStream(encodedData).toArray());
    }
    
    default boolean hasGaps(byte[] encodedData) {
    	return getNumberOfGaps(encodedData) >0;
    }
    
    default Stream<Range> matches(byte[] encodedData, String regex){
      //override if something better!
        return matches(encodedData, Pattern.compile(regex));
    }
    
    default Stream<Range> matches(byte[] encodedData, Pattern pattern){
        //override if something better!
        Matcher matcher = pattern.matcher(toString(encodedData));
        
        return StreamUtil.newGeneratedStream(() -> matcher.find()
                ? Optional.of(Range.of(matcher.start(), matcher.end() - 1))
                : Optional.empty());
       
    }
    
    default Stream<Range> matches(byte[] encodedData, Pattern pattern, Range range){
        //override if something better!
        Matcher matcher = pattern.matcher(toString(encodedData, range));
        
        return StreamUtil.newGeneratedStream(() -> matcher.find()
                ? Optional.of(new Range.Builder(matcher.start(), matcher.end() - 1).shift(range.getBegin()).build())
                : Optional.empty());
       
    }
    
    
    default Stream<Range> matches(byte[] encodedData, String regex,boolean nested){
      //override if something better!
        return matches(encodedData, Pattern.compile(regex),nested);
    }
        
    
    default Stream<Range> matches(byte[] encodedData, Pattern pattern, boolean nested){

        return matches(encodedData, pattern, Range.ofLength(getLength(encodedData)), nested);

    }
    
    default Stream<Range> matches(byte[] encodedData, Pattern pattern, Range range, boolean nested){
        //override if something better!
        Stream<Range> matches = matches(encodedData, pattern, range);
        if (! nested) {
            return matches;
        }
        List<Range> matchesList = matches.collect(Collectors.toList());
 
        Stream<Range> nestedMatches = matchesList.stream();

        if (matchesList.isEmpty()) {
            return Stream.empty();
        }
        long end;
        long start;
        int matchCount = matchesList.size();
        for (int i=0, j=1; i < matchCount; i++,j++){
            start = matchesList.get(i).getBegin();
            end = range.getEnd();
            if (j < matchCount) {
                // skip last to avoid returning next match again
                end = matchesList.get(j).getEnd() -1;
            }
            if (end - start > 0)
            {
                nestedMatches = Stream.concat(nestedMatches, matches(encodedData, pattern, Range.of(start, end-1), true));
                nestedMatches = Stream.concat(nestedMatches, matches(encodedData, pattern, Range.of(start+1, end), true));
            }
            if (end - start > 1)
            {
                nestedMatches = Stream.concat(nestedMatches, matches(encodedData, pattern, Range.of(start+1, end-1), true));
            }
        }
        return nestedMatches;
    }

    int getLeftFlankingNonGapOffsetFor(byte[] encodedData, int gappedOffset);
    int getRightFlankingNonGapOffsetFor(byte[] encodedData, int gappedOffset);
    
   
	Range getExpandingFlankingNonGapRangeFor(byte[] encodedData, Rangeable gappedRange);

	Range getContractingFlankingNonGapRangeFor(byte[] encodedData, Rangeable gappedRange);
	
	Range getExpandingFlankingNonGapRangeFor(byte[] encodedData, int gappedBegin, int gappedEnd );

	Range getContractingFlankingNonGapRangeFor(byte[] encodedData, int gappedBegin, int gappedEnd);
	
	OfInt createLeftFlankingNonGapIterator(byte[] encodedGlyphs, int startingGapOffset);
	
	OfInt createRightFlankingNonGapIterator(byte[] encodedGlyphs, int startingGapOffset);
}
