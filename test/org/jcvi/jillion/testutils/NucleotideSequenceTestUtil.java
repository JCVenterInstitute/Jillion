package org.jcvi.jillion.testutils;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public final class NucleotideSequenceTestUtil {

	private NucleotideSequenceTestUtil(){
		//can not instantiate
	}
	
	public static NucleotideSequence create(String seq){
		return new NucleotideSequenceBuilder(seq).build();
	}
}
