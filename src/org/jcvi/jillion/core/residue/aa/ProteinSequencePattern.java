package org.jcvi.jillion.core.residue.aa;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ProteinSequencePattern {

    private ProteinSequencePattern(){
        //can not instantiate
    }

    public static Pattern compile(String pattern){

        StringBuilder patternBuilder = new StringBuilder(pattern.length()*2);
        for (char c : pattern.toCharArray()) {
            //special case for stop codon *
            if (c == '*') {
                patternBuilder.append(c);
            } else {
                AminoAcid aa = AminoAcid.safeParse(Character.toString(c));
                if (aa == null) {
                    patternBuilder.append(c);
                } else {
                    if (aa.isAmbiguity()) {
                        patternBuilder.append(
                                aa.getNonAmbiguousBases().stream()
                                        .map(Objects::toString)
                                        .collect(Collectors.joining("", "["+aa.asChar(), "]")));
                    } else {
                        patternBuilder.append(aa.getCharacter());
                    }
                }
            }
        }
        return Pattern.compile(patternBuilder.toString());

    }
}
