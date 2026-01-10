package org.jcvi.jillion.core.residue;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.io.StreamUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Find Sequence matches using the given Patterns.
 */
public interface MatchableSequence<R extends Residue<R>, S extends ResidueSequence<R, S,B>, B extends ResidueSequenceBuilder<R, S,B>> {

    @Data
    @Setter(AccessLevel.NONE)
    @SuperBuilder
    abstract class SequenceMatcherParameters<R extends Residue<R>, S extends ResidueSequence<R, S,B>, B extends ResidueSequenceBuilder<R, S,B>>{

        Pattern pattern;

        String stringPattern;

        boolean nested;
        Range subSequenceRange;
        boolean explodeAmbiguities;

        boolean explodePatternAmbiguities;

        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        Pattern computedPattern;

        public Pattern getComputedPattern() {
            return getComputedPattern(getRegexPatternFunction());
        }


        private Set<S> computeExplodedSequences(S sequence, Range range){
            if(range ==null){
                return computeExplodedSequences(sequence);
            }
            if(! explodeAmbiguities || !sequence.hasAmbiguities()){
                return Set.of(sequence.trim(range));
            }
            return _computeExplodedSequences(()-> sequence.iterator(range), (int) range.getLength());
        }

        private Set<S> computeExplodedSequences(S sequence){
            if(!explodeAmbiguities || !sequence.hasAmbiguities()){
                return Set.of(sequence);
            }
            return _computeExplodedSequences(sequence::iterator, (int)sequence.getLength());
        }

        protected abstract CharSequence toCharSequence(S sequence);

        public Stream<Range> findMatches(S sequence){

            Pattern pattern = getComputedPattern();
            Range subSequenceRange = this.getSubSequenceRange()==null? Range.ofLength(sequence.getLength()) : this.getSubSequenceRange();

            Stream<Range> matches = _findMatches(sequence, pattern, subSequenceRange);
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
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start + 1, end) , true));
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start, end -1 ), true));
                }
                if (end - start  > 1)
                {
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start + 1, end -1), true));
                }
            }
            return nestedOutput;

        }

        private Stream<Range> _findMatches(S sequence, Pattern pattern){

            if(subSequenceRange !=null){
                return _findMatches(sequence, pattern, subSequenceRange);
            }
            Set<Range> ranges = new TreeSet<>(Range.Comparators.ARRIVAL);

            for(S s : computeExplodedSequences(sequence)) {
                Matcher matcher = pattern.matcher(toCharSequence(s));

                ranges.addAll(StreamUtil.newGeneratedStream(() -> matcher.find()
                                ? Optional.of(Range.of(matcher.start(), matcher.end() - 1))
                                : Optional.empty())
                        .collect(Collectors.toList()));
            }
            return ranges.stream();
        }

        private Stream<Range> _findMatches(S sequence, Pattern pattern, Range subSequenceRange){

            Set<Range> ranges = new TreeSet<>(Range.Comparators.ARRIVAL);

            for(S s : computeExplodedSequences(sequence, subSequenceRange)) {
                Matcher matcher = pattern.matcher(toCharSequence(s));

                ranges.addAll(StreamUtil.newGeneratedStream(() -> matcher.find()
                                ? Optional.of(Range.of(subSequenceRange.getBegin() + matcher.start(), subSequenceRange.getBegin() + matcher.end() - 1))
                                : Optional.empty())
                        .collect(Collectors.toList()));
            }
            return ranges.stream();
        }

        private Stream<Range> _findMatches(S sequence, Pattern pattern, Range subSequenceRange, boolean nested){
            Stream<Range> matches = _findMatches(sequence, pattern, subSequenceRange);
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
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start + 1, end) , true));
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start, end -1 ), true));
                }
                if (end - start  > 1)
                {
                    nestedOutput = Stream.concat(nestedOutput,_findMatches(sequence, pattern,Range.of(start + 1, end -1), true));
                }
            }
            return nestedOutput;
        }


        protected abstract B createNewBuilder(int seqLength);

        private Set<S> _computeExplodedSequences(Supplier<Iterator<R>> iteratorSupplier, int seqLength){


            List<B> builderList = new ArrayList<>();
            builderList.add(createNewBuilder(seqLength));
            Iterator<R> iter = iteratorSupplier.get();
            while(iter.hasNext()){
                R n = iter.next();
                if(n.isAmbiguity()){
                    Set<R> nonAmbiguousBases = n.getNonAmbiguousBases();
                    List<B> newList = new ArrayList<>(nonAmbiguousBases.size() * builderList.size());
                    for(R nonAmbiguious : nonAmbiguousBases){
                        for(B b : builderList){
                            newList.add( b.copy()
                                    .append(nonAmbiguious));
                        }
                    }
                    builderList = newList;

                }else{
                    for(B b : builderList){
                        b.append(n);
                    }
                }
            }

            return builderList.stream()
                    .map(B::build)
                    .collect(Collectors.toSet());
        }

        protected abstract BiFunction<String, Boolean, Pattern> getRegexPatternFunction();

        private Pattern getComputedPattern(BiFunction<String, Boolean, Pattern> patternFunction) {
            if(pattern !=null){
                return pattern;
            }
            if(computedPattern ==null){
                computedPattern = patternFunction.apply(stringPattern, explodePatternAmbiguities);
            }

            return computedPattern;
        }


    }

    /**
     * Get the length of the sequence.
     * @return the length
     */
    long getLength();

    /**
     * Find the Ranges in this sequence within the specified sub sequence range
     *  that match the given regular expression.
     *
     * @param regex the pattern to look for.  All bases must be in uppercase.
     * @param subSequenceRange the Range in the sequence to look for matches in.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     *
     * @apiNote this is the same as {@code  findMatches(Pattern.compile(regex), subSequenceRange); }
     *
     * @since 5.3
     *
     * @see #findMatches(Pattern, Range)
     */
    default Stream<Range> findMatches(String regex, Range subSequenceRange){
        return findMatches(regex, subSequenceRange, false);
    }

    Stream<Range> findMatches(String regex, Range subSequenceRange, boolean nested);


    default Stream<Range> findMatches(Pattern pattern, boolean nested){
        return findMatches(pattern, null, nested);
    }



    Stream<Range> findMatches(SequenceMatcherParameters<R,S, B> sequenceMatcherParameters);

    Stream<Range> findMatches(Pattern pattern, Range subSequenceRange, boolean nested) ;

    /**
     * Find all the Ranges in this sequence that match the given regular expression {@link Pattern}.
     * @param regex the regular expression pattern to look for.  All bases must be in uppercase.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     *
     * @apiNote this is the same as {@code  findMatches(Pattern.compile(regex)); }
     *
     * @since 5.3
     *
     * @see #findMatches(Pattern)
     */
    default Stream<Range> findMatches(String regex){
        return findMatches(regex, null);
    }

    default Stream<Range> findMatches(String regex, boolean nested){
        return  findMatches(regex, null, nested);
    }
    /**
     * Find all the Ranges in this sequence that match the given regular expression {@link Pattern}.
     *
     * @param pattern the pattern to look for.  All bases must be in uppercase.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * @since 5.3
     */
    default Stream<Range> findMatches(Pattern pattern){
        return findMatches(pattern, null, false);
    }

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
    default Stream<Range> findMatches(Pattern pattern, Range subSequenceRange){
        return findMatches(pattern, subSequenceRange, false);
    }

}
