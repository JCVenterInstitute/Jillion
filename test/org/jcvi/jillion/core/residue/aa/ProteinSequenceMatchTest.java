package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.Range;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ProteinSequenceMatchTest {

    @Test
    public void simpleSubstring(){
        ProteinSequence sut = ProteinSequence.of("IKFTW");

        List<Range> actual = sut.findMatches("KFT")
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3)), actual);
    }

    @Test
    public void simpleSubstringWithSeqAmbiguity(){
        ProteinSequence sut = ProteinSequence.of("IKXTW");

        List<Range> actual = sut.findMatches(
                ProteinSequence.ProteinSequenceMatcherParameters.builder()
                        .stringPattern("KFT")
                        .explodeAmbiguities(true)
                        .build())
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3)), actual);
    }

    @Test
    public void simpleSubstringWithMultipleSeqAmbiguityMatches(){
        ProteinSequence sut = ProteinSequence.of("IKITWKLTW");

        List<Range> actual = sut.findMatches(
                ProteinSequence.ProteinSequenceMatcherParameters.builder()
                        .stringPattern("KJT")
                        .explodePatternAmbiguities(true)
                        .build())
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3), Range.of(5,7)), actual);
    }
    @Test
    public void plusMatch(){
        ProteinSequence sut = ProteinSequence.of("IIIIIIKTW");

        List<Range> actual = sut.findMatches("I+")
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(0,5)), actual);
    }
    @Test
    public void plusMatchGreedy(){
        ProteinSequence sut = ProteinSequence.of("IIIIIIKTW");

        List<Range> actual = sut.findMatches("I+?")
                .collect(Collectors.toList());

        assertEquals(List.of(
                Range.of(0),
                Range.of(1),
                Range.of(2),
                Range.of(3),
                Range.of(4),
                Range.of(5)
        ), actual);
    }
    @Test
    public void ambiguity(){
        ProteinSequence sut = ProteinSequence.of("IKFTW");

        List<Range> actual = sut.findMatches(ProteinSequence.ProteinSequenceMatcherParameters.builder()
                        .stringPattern("X")
                        .explodePatternAmbiguities(true)
                        .build())
                .collect(Collectors.toList());

        assertEquals(List.of(
                Range.of(0),
                Range.of(1),
                Range.of(2),
                Range.of(3),
                Range.of(4)
                ), actual);
    }

    @Test
    public void simpleSubstring2Matches(){
        ProteinSequence sut = ProteinSequence.of("IKFTWKFT");

        List<Range> actual = sut.findMatches("KFT")
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3),
                Range.of(5,7)), actual);
    }
    @Test
    public void simpleSubstring2MatchesWithAmbigutiy(){
        ProteinSequence sut = ProteinSequence.of("IKFTWKFT");

        List<Range> actual = sut.findMatches(ProteinSequence.ProteinSequenceMatcherParameters.builder()
                .stringPattern("KXT")
                .explodePatternAmbiguities(true)
                .build())
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3),
                Range.of(5,7)), actual);
    }
    @Test
    public void simpleSubstring2MatchesWithOr(){
        ProteinSequence sut = ProteinSequence.of("IKFTWKQT");

        List<Range> actual = sut.findMatches("K[FQ]T")
                .collect(Collectors.toList());

        assertEquals(List.of(Range.of(1,3),
                Range.of(5,7)), actual);
    }
}
