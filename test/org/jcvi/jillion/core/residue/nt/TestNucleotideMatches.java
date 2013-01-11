package org.jcvi.jillion.core.residue.nt;

import java.util.EnumSet;
import java.util.Set;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestNucleotideMatches {

	@Test(expected = NullPointerException.class)
	public void nullShouldThrowNPE(){
		Nucleotide.Adenine.matches(null);
	}
	
	@Test
	public void equalNucleotidesAlsoMatch(){
		for(Nucleotide n : Nucleotide.values()){
			assertTrue(n.matches(n));
		}
	}
	
	
	
	
	@Test
	public void ambiguityMatchBasesThatMakeItUpAndViceVersa(){
		Set<Nucleotide> ambiguities = EnumSet.complementOf(EnumSet.of(
				Nucleotide.Adenine, Nucleotide.Cytosine, Nucleotide.Guanine, Nucleotide.Thymine));
	
		for(Nucleotide ambiguity : ambiguities){
			for(Nucleotide nt :ambiguity.getBasesFor()){
				assertTrue(nt.matches(ambiguity));
				assertTrue(ambiguity.matches(nt));
			}
		}
		
	}
	
	@Test
	public void nonAmbiguityMatchesItsAmbiguity(){
		Set<Nucleotide> ambiguities = EnumSet.complementOf(EnumSet.of(
				Nucleotide.Adenine, Nucleotide.Cytosine, Nucleotide.Guanine, Nucleotide.Thymine));
	
		for(Nucleotide ambiguity : ambiguities){
			for(Nucleotide nt :ambiguity.getBasesFor()){
				assertTrue(ambiguity.matches(nt));
			}
		}
	}
	
	@Test
	public void M(){
		//M= A,C
		Nucleotide m = Nucleotide.parse("M");
		assertTrue(m.matches(Nucleotide.Adenine));
		assertTrue(m.matches(Nucleotide.Cytosine));
		
		assertFalse(m.matches(Nucleotide.Guanine));
		assertFalse(m.matches(Nucleotide.Thymine));
		//V = A,C,G
		assertTrue(m.matches(Nucleotide.parse("V")));
		//H = A,C,T
		assertTrue(m.matches(Nucleotide.parse("H")));
		assertTrue(m.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("RWSYKDB")){
			assertFalse(nonMatch.toString(), m.matches(nonMatch));
		}
		
	}
	
	@Test
	public void R(){
		//R= A,G
		Nucleotide r = Nucleotide.parse("R");
		assertTrue(r.matches(Nucleotide.Adenine));
		assertTrue(r.matches(Nucleotide.Guanine));
		
		assertFalse(r.matches(Nucleotide.Cytosine));
		assertFalse(r.matches(Nucleotide.Thymine));
		//V = A,C,G
		assertTrue(r.matches(Nucleotide.parse("V")));
		//D = A,G,T
		assertTrue(r.matches(Nucleotide.parse("D")));
		assertTrue(r.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MWSYKHB")){
			assertFalse(nonMatch.toString(), r.matches(nonMatch));
		}
		
	}
	
	@Test
	public void W(){
		//W = A,T
		Nucleotide w = Nucleotide.parse("W");
		assertTrue(w.matches(Nucleotide.Adenine));
		assertTrue(w.matches(Nucleotide.Thymine));
		
		assertFalse(w.matches(Nucleotide.Cytosine));
		assertFalse(w.matches(Nucleotide.Guanine));
		//H = A,C,T
		assertTrue(w.matches(Nucleotide.parse("H")));
		//D = A,G,T
		assertTrue(w.matches(Nucleotide.parse("D")));
		assertTrue(w.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MRSYKVB")){
			assertFalse(nonMatch.toString(), w.matches(nonMatch));
		}
		
	}
	
	@Test
	public void S(){
		//S = C, G
		Nucleotide s = Nucleotide.parse("S");
		assertTrue(s.matches(Nucleotide.Cytosine));
		assertTrue(s.matches(Nucleotide.Guanine));
		
		assertFalse(s.matches(Nucleotide.Adenine));
		assertFalse(s.matches(Nucleotide.Thymine));
		//V = A,C,G
		assertTrue(s.matches(Nucleotide.parse("V")));
		//B = C,G,T
		assertTrue(s.matches(Nucleotide.parse("B")));
		assertTrue(s.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MRYKHD")){
			assertFalse(nonMatch.toString(), s.matches(nonMatch));
		}
		
	}
	
	@Test
	public void Y(){
		//Y = C, T
		Nucleotide y = Nucleotide.parse("Y");
		assertTrue(y.matches(Nucleotide.Cytosine));
		assertTrue(y.matches(Nucleotide.Thymine));
		
		assertFalse(y.matches(Nucleotide.Adenine));
		assertFalse(y.matches(Nucleotide.Guanine));
		//H = A,C,T
		assertTrue(y.matches(Nucleotide.parse("H")));
		//B = C,G,T
		assertTrue(y.matches(Nucleotide.parse("B")));
		assertTrue(y.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MRWSKVD")){
			assertFalse(nonMatch.toString(), y.matches(nonMatch));
		}
		
	}
	
	@Test
	public void K(){
		//K = G,T
		Nucleotide k = Nucleotide.parse("K");
		assertTrue(k.matches(Nucleotide.Guanine));
		assertTrue(k.matches(Nucleotide.Thymine));
		
		assertFalse(k.matches(Nucleotide.Adenine));
		assertFalse(k.matches(Nucleotide.Cytosine));
		//D = A,G,T
		assertTrue(k.matches(Nucleotide.parse("D")));
		//B = C,G,T
		assertTrue(k.matches(Nucleotide.parse("B")));
		assertTrue(k.matches(Nucleotide.parse("N")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MRWSYHV")){
			assertFalse(nonMatch.toString(), k.matches(nonMatch));
		}
		
	}
	
	@Test
	public void V(){
		//V = A,C,G
		Nucleotide v = Nucleotide.parse("V");
		
		assertTrue(v.matches(Nucleotide.Adenine));
		assertTrue(v.matches(Nucleotide.Cytosine));
		assertTrue(v.matches(Nucleotide.Guanine));
		
		assertFalse(v.matches(Nucleotide.Thymine));
		
		assertTrue(v.matches(Nucleotide.parse("N")));
		
		assertTrue(v.matches(Nucleotide.parse("M")));
		assertTrue(v.matches(Nucleotide.parse("R")));
		assertTrue(v.matches(Nucleotide.parse("S")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("WYKHDB")){
			assertFalse(nonMatch.toString(), v.matches(nonMatch));
		}
		
	}
	
	@Test
	public void H(){
		//H = A,C,T
		Nucleotide h = Nucleotide.parse("H");
		
		assertTrue(h.matches(Nucleotide.Adenine));
		assertTrue(h.matches(Nucleotide.Cytosine));
		assertTrue(h.matches(Nucleotide.Thymine));
		
		assertFalse(h.matches(Nucleotide.Guanine));
		
		assertTrue(h.matches(Nucleotide.parse("N")));
		
		assertTrue(h.matches(Nucleotide.parse("M")));
		assertTrue(h.matches(Nucleotide.parse("W")));
		assertTrue(h.matches(Nucleotide.parse("Y")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("RSKVDB")){
			assertFalse(nonMatch.toString(), h.matches(nonMatch));
		}
		
	}
	@Test
	public void D(){
		//D = A,G,T
		Nucleotide d = Nucleotide.parse("D");
		
		assertTrue(d.matches(Nucleotide.Adenine));
		assertTrue(d.matches(Nucleotide.Guanine));
		assertTrue(d.matches(Nucleotide.Thymine));
		
		assertFalse(d.matches(Nucleotide.Cytosine));
		
		assertTrue(d.matches(Nucleotide.parse("N")));
		
		assertTrue(d.matches(Nucleotide.parse("R")));
		assertTrue(d.matches(Nucleotide.parse("W")));
		assertTrue(d.matches(Nucleotide.parse("K")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MSYVHB")){
			assertFalse(nonMatch.toString(), d.matches(nonMatch));
		}
		
	}
	
	@Test
	public void B(){
		////B = C,G,T
		Nucleotide b = Nucleotide.parse("B");
		
		assertTrue(b.matches(Nucleotide.Cytosine));
		assertTrue(b.matches(Nucleotide.Guanine));
		assertTrue(b.matches(Nucleotide.Thymine));
		
		assertFalse(b.matches(Nucleotide.Adenine));
		
		assertTrue(b.matches(Nucleotide.parse("N")));
		
		assertTrue(b.matches(Nucleotide.parse("S")));
		assertTrue(b.matches(Nucleotide.parse("Y")));
		assertTrue(b.matches(Nucleotide.parse("K")));
		
		for(Nucleotide nonMatch :Nucleotides.parse("MRWVHD")){
			assertFalse(nonMatch.toString(), b.matches(nonMatch));
		}
		
	}
	
	@Test
	public void nMatchesAllNonGap(){
		for(Nucleotide nt : EnumSet.complementOf(EnumSet.of(Nucleotide.Gap))){
			Nucleotide n = Nucleotide.Unknown;
			assertTrue(nt.toString(), nt.matches(n));
			assertTrue(nt.toString(), n.matches(nt));
		}
	}
}
