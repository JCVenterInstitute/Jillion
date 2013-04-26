package org.jcvi.jillion_experimental.primer;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public final class PrimerUtil {

	 public static NucleotideSequence M13_FORWARD_PRIMER = new NucleotideSequenceBuilder("TGTAAAACGACGGCCAGT").build();
	 
	 
	 public static NucleotideSequence M13_REVERSE_PRIMER = new NucleotideSequenceBuilder("CAGGAAACAGCTATGACC").build();


	 private PrimerUtil(){
		 //can not instantiate
	 }
}
