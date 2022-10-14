package org.jcvi.jillion.core.residue.aa;

import java.util.List;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestIupacTranslationTableUracil {

	private String triplet;
	private String expectedAA;
	
	public TestIupacTranslationTableUracil(String triplet, String expectedAA) {
		this.triplet = triplet;
		this.expectedAA = expectedAA;
	}
	
	@Parameters
	public static List<String[]> data(){
		return List.of(
				new String[] {"UUU", "F"},
				new String[] {"UKU", "X"});
	}

	@Test
	public void assignAmbigiousAAIfNucleotideHasAmbigiousBases() {
		AminoAcid actual = IupacTranslationTables.STANDARD.translate(NucleotideSequence.of(triplet)).get(0);
		assertEquals(expectedAA, actual.toString());
	}
}
