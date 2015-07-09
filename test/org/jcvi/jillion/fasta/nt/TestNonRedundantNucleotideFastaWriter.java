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
		
		try(NucleotideFastaRecordWriter writer = new NucleotideFastaRecordWriterBuilder(out)
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
