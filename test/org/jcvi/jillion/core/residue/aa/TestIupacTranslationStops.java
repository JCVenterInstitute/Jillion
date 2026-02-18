/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class TestIupacTranslationStops {


	@Test
	public void TestFindStops(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
							"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
							"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();
		
		Map<Frame,List<Long>> expected = new HashMap<Frame,List<Long>>();
		expected.put(Frame.THREE, List.of(5L));
		expected.put(Frame.TWO, List.of(31L));
		expected.put(Frame.ONE,List.of(87L,120L));
		Map<Frame,List<Long>> actual = table1.findStops(seq);
		assertEquals(expected,actual);	    
	}

	@Test
	public void readThroughStopsAsDefaults(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
				"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
				"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();


		ProteinSequence proteinSequence = table1.translate(seq);

		assertEquals(62, proteinSequence.getLength());

	}

	@Test
	public void readThroughStopsAsDefaultsUsingBuilderDefaultFactoryMethod(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
				"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
				"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();


		ProteinSequence proteinSequence = table1.translate(seq, TranslationOptions.createDefaultOptions());

		assertEquals(62, proteinSequence.getLength());

	}
	@Test
	public void readThroughStopsAsDefaultsUsingBuilder(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
				"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
				"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();


		ProteinSequence proteinSequence = table1.translate(seq, TranslationOptions.builder().build());

		assertEquals(62, proteinSequence.getLength());

	}
	@Test
	public void readThroughStopsUsingBuilder(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
				"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
				"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();


		ProteinSequence proteinSequence = table1.translate(seq, TranslationOptions.builder()
				.readThroughStops(true)
				.build());

		assertEquals(62, proteinSequence.getLength());

	}
	@Test
	public void doNotReadThroughStopsUsingBuilder(){
		IupacTranslationTables table1 = IupacTranslationTables.STANDARD;
		NucleotideSequence seq = new NucleotideSequenceBuilder("AGAATTAGGTCAGAGCCTCTCTGCAACAACGTAAAACCCGCACCCGCCGCAGTATGTTATTCGT" +
				"ACCGGGCGCCAACGCGGCGATGGTGAGCAATTCGTTTATCTACCCGGCCGACGCGCTGA" +
				"TGTTCGACCTGGAAGACTCCGTTGCATTACGCGAAAAAGACGCGGCGCGCCGTCTGGTACAGR").build();


		ProteinSequence proteinSequence = table1.translate(seq, TranslationOptions.builder()
				.readThroughStops(false)
				.build());

		assertEquals(30, proteinSequence.getLength());
		assertEquals(AminoAcid.STOP, proteinSequence.get(29));

	}
	
}
