package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.residue.aa.AminoAcid;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class NucleotideSequencePattern {

    private NucleotideSequencePattern(){
        //can not instantiate
    }

    public static Pattern compile(String pattern){

        StringBuilder patternBuilder = new StringBuilder(pattern.length()*2);
        for (char c : pattern.toCharArray()) {

            Nucleotide n = Nucleotide.safeParse(c);
            if (n == null) {
                patternBuilder.append(c);
            } else {
                if (n.isAmbiguity()) {
                    patternBuilder.append(
                            n.getBasesFor().stream()
                                    .map(Objects::toString)
                                    .collect(Collectors.joining("", "["+n.getCharacter(), "]")));
                } else {
                    patternBuilder.append(n.getCharacter());
                }
            }

        }
        return Pattern.compile(patternBuilder.toString());

    }
}
