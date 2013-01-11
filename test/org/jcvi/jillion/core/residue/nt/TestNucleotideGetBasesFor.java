package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideGetBasesFor {

	@Test
	public void mirrorsGetAmbiguityFor(){
		for(Nucleotide n : Nucleotide.values()){
			assertSame(n, Nucleotide.getAmbiguityFor(n.getBasesFor()));
		}
	}
}
