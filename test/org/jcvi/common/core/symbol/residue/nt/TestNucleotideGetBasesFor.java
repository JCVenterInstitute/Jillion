package org.jcvi.common.core.symbol.residue.nt;

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
