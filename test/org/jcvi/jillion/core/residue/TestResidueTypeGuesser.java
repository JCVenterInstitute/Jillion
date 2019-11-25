package org.jcvi.jillion.core.residue;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestResidueTypeGuesser {

    @Test
    public void allUsIsRNA(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.RNA, ResidueTypeGuesser.guessSequenceType("UUUUUUUUU"));

    }

    @Test
    public void acgtIsDNA(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.DNA, ResidueTypeGuesser.guessSequenceType("ACGTACGT"));

    }
    @Test
    public void acguIsRNA(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.RNA, ResidueTypeGuesser.guessSequenceType("ACGUACGU"));

    }

    @Test
    public void acgtAndUIsRNA(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.RNA, ResidueTypeGuesser.guessSequenceType("ACGTUACGUT"));

    }
    @Test
    public void acgtAndWhitespaceIsDNA(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.DNA, ResidueTypeGuesser.guessSequenceType("\tA CGTAC GT "));

    }

    @Test
    public void fluSegmentAminoAcidWithNewLines(){
        assertEquals(ResidueTypeGuesser.ResidueTypeGuessedResult.AMINO_ACID, ResidueTypeGuesser.guessSequenceType("MASQGTKRSYEQMETGGDRQNATEIRASVGRMVGGIGRFYIQMCTELKLSDYEGRLIQNSITIERMVLSA\n" +
                "FDERRNKYLEEHPSAGKDPKKTGGPIYRRRDGKWMRELILYDKEEIRRIWRQANNGEDATAGLTHMMIWH\n" +
                "SNLNDATYQRTRALVRTGMDPRMCSLMQGSTLPRRSGAAGAAVKGVGTMVMELIRMIKRGINDRNFWRGE\n" +
                "NGRRTRIAYERMCNILKGKFQTAAQRAMMDQVRESRNPGNAEIEDLIFLARSALILRGSVAHKSCLPACV\n" +
                "YGLAVASGYDFEREGYSLVGIDPFRLLQNSQVFSLIRPNENPAHKSQLVWMACHSAAFEDLRVSSFIRGT\n" +
                "RVVPRGQLSTRGVQIASNENMETMDSSTLELRSRYWAIRTRSGGNTNQQRASAGQISVQPTFSVQRNLPF\n" +
                "ERATIMAAFTGNTEGRTSDMRTEIIRMMENARPEDVSFQGRGVFELSDEKATNPIVPSFDMSNEGSYFFG\n" +
                "DNAEEYDN"));

    }


}
