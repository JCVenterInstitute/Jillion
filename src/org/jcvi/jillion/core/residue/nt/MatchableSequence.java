package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Find Sequence matches using the given Patterns.
 */
public interface MatchableSequence {

    /**
     * Get the length of the sequence.
     * @return the length
     */
    long getLength();

    default Stream<Range> findMatches(String regex, Range subSequenceRange){
        return findMatches(Pattern.compile(regex), subSequenceRange);
    }

    default Stream<Range> findMatches(String regex, Range subSequenceRange, boolean nested){
        return findMatches(Pattern.compile(regex),subSequenceRange,nested);
    }


    default Stream<Range> findMatches(Pattern pattern, boolean nested) {

        return findMatches(pattern, Range.ofLength(getLength()), nested);
    }



    default Stream<Range> findMatches(Pattern pattern, Range subSequenceRange, boolean nested) {

        Stream<Range> matches = findMatches(pattern, subSequenceRange);
        if (! nested) {
            return matches;
        }
        List<Range> matchList = matches.collect(Collectors.toList());

        Stream<Range> nestedOutput = matchList.stream();

        long start;
        long end;
        long matchCount = matchList.size();
        for (int i=0,j=1; i < matchCount; i++,j++) {
            start = matchList.get(i).getBegin();
            end = subSequenceRange.getEnd();
            if (j < matchCount) {
                // skip last to avoid getting next match again
                end = matchList.get(j).getEnd() -1;
            }
            if (end - start > 0) {
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start + 1, end) , nested));
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start, end -1 ), nested));
            }
            if (end - start  > 1)
            {
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start + 1, end -1), nested));
            }
        }
        return nestedOutput;
    }

    default Stream<Range> findMatches(String regex){
        return findMatches(Pattern.compile(regex));
    }

    default Stream<Range> findMatches(String regex, boolean nested){
        return findMatches(Pattern.compile(regex),nested);
    }
    /**
     * Find all the Ranges in this sequence that match the given regular expression {@link Pattern}.
     *
     * @param pattern the pattern to look for.  All bases must be in uppercase.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * @since 5.3
     */
    Stream<Range> findMatches(Pattern pattern);

    /**
     * Find the Ranges in this sequence within the specified sub sequence range
     * that match the given regular expression {@link Pattern}.
     * <strong>NOTE</strong> All the Range
     * coordinates returned in the Stream will be relative to the entire sequence.
     *
     * @param pattern          the pattern to look for.  All bases must be in uppercase.
     * @param subSequenceRange the Range in the sequence to look for matches in.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * @apiNote This should return the same result as :
     * <pre>
     *  sut.findMatches(pattern)
     *     .filter(r-> r.isSubRangeOf(subSequenceRange))
     *
     *  </pre>
     * But will be more efficient.
     * @since 5.3
     */
    Stream<Range> findMatches(Pattern pattern, Range subSequenceRange);

}
