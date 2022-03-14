package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence.Polymorphism;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence.PolymorphismType;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.stream.Collectors;


public class PolymorphismTest {

	@Test
	public void noSnpsEmptyPolymorpisms() {
		NucleotideSequence ref = NucleotideSequence.of("ACGTACGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder(ref)
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		
		assertEquals(0, read.computePolymorphisms().count());
		
	}
	
	@Test
	public void oneSNP() {
		NucleotideSequence ref = NucleotideSequence.of("ACGTACGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGTTCGT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(4, PolymorphismType.POLYMORPHISM,
																		 NucleotideSequence.of("A"),  NucleotideSequence.of("T")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneInsertion() {
		NucleotideSequence ref = NucleotideSequence.of("ACGT-CGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGTTCGT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(4, PolymorphismType.INSERTION,
																		 NucleotideSequence.of("-"),  NucleotideSequence.of("T")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneDeletion() {
		NucleotideSequence ref = NucleotideSequence.of("ACGTTCGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGT-CGT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(4, PolymorphismType.DELETION,
																		 NucleotideSequence.of("T"),  NucleotideSequence.of("-")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void multibaseDeletion() {
		NucleotideSequence ref = NucleotideSequence.of("ACGTTCGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGT--GT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(4, PolymorphismType.DELETION,
																		 NucleotideSequence.of("TC"),  NucleotideSequence.of("--")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void twoSeparateSNPs() {
		NucleotideSequence ref = NucleotideSequence.of("ACGTACGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGTTCGG")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(
				new Polymorphism(4, PolymorphismType.POLYMORPHISM,
									NucleotideSequence.of("A"),  NucleotideSequence.of("T")),
				new Polymorphism(7, PolymorphismType.POLYMORPHISM,
						 			NucleotideSequence.of("T"),  NucleotideSequence.of("G")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneMultibasePolymorphism() {
		NucleotideSequence ref = NucleotideSequence.of(                        "ACGTACGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ACGTGGGT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(4, PolymorphismType.POLYMORPHISM,
																		 NucleotideSequence.of("AC"),  NucleotideSequence.of("GG")));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void twoMultibasePolymorphisms() {
		NucleotideSequence ref = NucleotideSequence.of(                        "ACGTACGT");
		ReferenceMappedNucleotideSequence read = new NucleotideSequenceBuilder("ATGTGGGT")
										.setReferenceHint(ref, 0)
										.buildReferenceEncodedNucleotideSequence();
		
		List<Polymorphism> actual= read.computePolymorphisms().collect(Collectors.toList());
		 
		List<Polymorphism> expected = List.of(new Polymorphism(1, PolymorphismType.POLYMORPHISM,
				 									NucleotideSequence.of("C"),  NucleotideSequence.of("T")),
				
												new Polymorphism(4, PolymorphismType.POLYMORPHISM,
																		 NucleotideSequence.of("AC"),  NucleotideSequence.of("GG")));
		
		assertEquals(expected, actual);
		
	}
}
