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
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;

public class TestNonRedundantNucleotideFastaWriter {

	private ResourceHelper resources = new ResourceHelper(TestNonRedundantNucleotideFastaWriter.class);
	
	
	@Test
	public void mergeMultipleRecordsWithSameSequence() throws IOException{
		NucleotideSequence seq = NucleotideSequenceTestUtil.create("atgaacaacgattttctggtgctgaaaaacattaccaaaagctttggcaaagcgaccgtg" +
				"attgataacctggatctggtgattaaacgcggcaccatggtgaccctgctgggcccgagc" +
				"ggctgcggcaaaaccaccgtgctgcgcctggtggcgggcctggaaaacccgaccagcggc" +
				"cagatttttattgatggcgaagatgtgaccaaaagcagcattcagaaccgcgatatttgc" +
				"attgtgtttcagagctatgcgctgtttccgcatatgagcattggcgataacgtgggctat" +
				"ggcctgcgcatgcagggcgtgagcaacgaagaacgcaaacagcgcgtgaaagaagcgctg" +
				"gaactggtggatctggcgggctttgcggatcgctttgtggatcagattagcggcggccag" +
				"cagcagcgcgtggcgctggcgcgcgcgctggtgctgaaaccgaaagtgctgattctggat" +
				"gaaccgctgagcaacctggatgcgaacctgcgccgcagcatgcgcgaaaaaattcgcgaa" +
				"ctgcagcagcgcctgggcattaccagcctgtatgtgacccatgatcagaccgaagcgttt" +
				"gcggtgagcgatgaagtgattgtgatgaacaaaggcaccattatgcagaaagcgcgccag" +
				"aaaatttttatttatgatcgcattctgtatagcctgcgcaactttatgggcgaaagcacc" +
				"atttgcgatggcaacctgaaccagggcaccgtgagcattggcgattatcgctttccgctg" +
				"cataacgcggcggattttagcgtggcggatggcgcgtgcctggtgggcgtgcgcccggaa" +
				"gcgattcgcctgaccgcgaccggcgaaaccagccagcgctgccagattaaaagcgcggtg" +
				"tatatgggcaaccattgggaaattgtggcgaactggaacggcaaagatgtgctgattaac" +
				"gcgaacccggatcagtttgatccggatgcgaccaaagcgtttattcattttaccgaacag" +
				"ggcatttttctgctgaacaaagaa");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try(NucleotideFastaWriter writer = new NucleotideFastaWriterBuilder(out)
														.makeNonRedundant()
														.build()){
			writer.write("gi|3023276|sp|Q57293|AFUC_ACTPL", seq,"Ferric transport ATP-binding protein afuC");
			writer.write("gi|1469284|gb|AAB05030.1|", seq, "afuC gene product");
			writer.write("gi|1477453|gb|AAB17216.1|", seq, "afuC [Actinobacillus pleuropneumoniae]");
			writer.write("Blah", NucleotideSequenceTestUtil.create("ACGTACGTACGTACGT"));
		}
		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		try(InputStream in = resources.getFileAsStream("files/nonRedundantNucleotide.fasta.nr")){
			IOUtil.copy(in, expected);
		}
		assertArrayEquals(expected.toByteArray(), out.toByteArray());
		
	}
	
}
