package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.nt.INucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGappedReferenceBuilder {

    protected GappedReferenceBuilder createSut(String seqPrefix){
        return new GappedReferenceBuilder(NucleotideSequence.of(seqPrefix));
    }
    private static void seqStartsWith(String prefix, ResidueSequence<?,?,?> seq){
        assertEquals(prefix, seq.trim(Range.ofLength(prefix.length())).toString());
    }
    @Test
    public void noGaps(){

        GappedReferenceBuilder sut = createSut("ACGTACGT");
        seqStartsWith("ACGTACGT", sut.build());
    }
    @Test
    public void oneGap(){

        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadInsertion(4, 1);
        seqStartsWith("ACGT-ACGT",sut.build());
    }

    @Test
    public void oneGapBeyondReference(){

        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadInsertion(8, 1);
        seqStartsWith("ACGTACGT-",sut.build());
    }
    @Test
    public void twoGapsBeyondReference(){

        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadInsertion(8, 2);
        seqStartsWith("ACGTACGT--",sut.build());
    }

    @Test
    public void twoGaps(){

        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadInsertion(4, 1);
        sut.addReadInsertion(2, 2);
        seqStartsWith("AC--GT-ACGT", sut.build());
    }
    @Test
    public void oneGapCIGAR(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadByCigar(0, Cigar.parse("4M1I4M"));
        seqStartsWith("ACGT-ACGT", sut.build());
    }
    @Test
    public void sameGapMultipleTimes(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");
        sut.addReadInsertion(4, 1);
        sut.addReadInsertion(4, 1);
        sut.addReadInsertion(4, 1);
        seqStartsWith("ACGT-ACGT", sut.build());
    }
    @Test
    public void sameGapMultipleTimesCIGAR(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");

        sut.addReadByCigar(0, Cigar.parse("4M1I4M"));
        sut.addReadByCigar(0, Cigar.parse("4M1I4M"));
        sut.addReadByCigar(0, Cigar.parse("4M1I4M"));
        seqStartsWith("ACGT-ACGT", sut.build());
    }
    @Test
    public void oneLargeInsert(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");

        sut.addReadInsertion(4, 2);
        seqStartsWith("ACGT--ACGT", sut.build());
    }
    @Test
    public void multipleInsertionsOfDifferentLengthsTakesLargest(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");

        sut.addReadInsertion(4, 1);
        sut.addReadInsertion(4, 2);
        sut.addReadInsertion(4, 1);
        seqStartsWith("ACGT--ACGT", sut.build());
    }
    @Test
    public void oneLargeInsertCIGAR(){
        GappedReferenceBuilder sut = createSut("ACGTACGT");

        sut.addReadByCigar(0, Cigar.parse("4M2I4M"));
        seqStartsWith("ACGT--ACGT", sut.build());
    }
}
