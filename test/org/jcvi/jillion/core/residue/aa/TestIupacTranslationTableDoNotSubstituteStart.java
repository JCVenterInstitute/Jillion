package org.jcvi.jillion.core.residue.aa;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestIupacTranslationTableDoNotSubstituteStart {

	@Test
	public void ctgShouldBeL(){
		assertEquals("L", IupacTranslationTables.STANDARD.translate(seq("CTG"),false).get(0).toString());
	}
	
	@Test
	public void ttgShouldBeL(){
		assertEquals("L", IupacTranslationTables.STANDARD.translate(seq("TTG"),false).get(0).toString());
	}
	
	@Test
	public void ctgShouldBeM(){
		assertEquals("M", IupacTranslationTables.STANDARD.translate(seq("CTG"),true).get(0).toString());
	}
	
	@Test
	public void ttgShouldBeM(){
		assertEquals("M", IupacTranslationTables.STANDARD.translate(seq("TTG"),true).get(0).toString());
	}
	
	private NucleotideSequence seq(String s){
		return new NucleotideSequenceBuilder(s).build();
	}
}
