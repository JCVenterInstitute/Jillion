package org.jcvi.jillion.examples;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class TrimSequences {

    public static void main(String[] args) {
        NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTTTTGGACCGTACGTAGGGTTTT").build();
        
        
        Range trimRange = Range.of(CoordinateSystem.RESIDUE_BASED, 1, 10);
        
        //trimmedSeq = ACGTTTTGGA
        NucleotideSequence trimmedSeq = seq.toBuilder()
                                                .trim(trimRange)
                                                .build();
        
        NucleotideSequence alsoWorks = seq.toBuilder(trimRange)
                                                .build();
        

    }

}
