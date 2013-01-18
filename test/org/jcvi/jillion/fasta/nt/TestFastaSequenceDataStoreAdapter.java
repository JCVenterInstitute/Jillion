package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
public class TestFastaSequenceDataStoreAdapter {

	private final File fastaFile;
	private NucleotideSequenceDataStore sut;
	public TestFastaSequenceDataStoreAdapter() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestFastaSequenceDataStoreAdapter.class);
		fastaFile = resources.getFile("files/19150.fasta");
	}
	
	@Before
	public void createDataStore() throws IOException{
		sut = FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile));
	}
	@Test
	public void numberOfRecords() throws DataStoreException{
		assertEquals(9,sut.getNumberOfRecords());
	}
	
	@Test
	public void get() throws DataStoreException{
		NucleotideSequence expectedSequence = sequenceForThirdRecord();
		
		assertEquals(expectedSequence, sut.get("3"));
	}

	private NucleotideSequence sequenceForThirdRecord() {
		NucleotideSequence expectedSequence = new NucleotideSequenceBuilder(

				"GATAATCACTCACCGAGTGACATCCACATCATGGCGTCTCAAGGCACCAAACGATCTTAT" +
				"GAGCAGATGGAAACTGGAGGGGAACGCCAGAATGCCACTGAGATCAGAGCATCTGTTGGG" +
				"AGAATGGTTGGTGGAATCGGGAGATTCTACATACAGATGTGTACTGAACTCAAACTCAGT" +
				"GACTATGAAGGAAGACTGATCCAAAACAGCATAACAATAGAGAGAATGGTTCTCTCTGCA" +
				"TTTGATGAGAGAAGAAATAGATATCTGGAAGAACATCCCAGTGCTGGGAAAGACCCTAAG" +
				"AAAACTGGAGGCCCAATCTACAGGAGGAGAGATGGGAAATGGGTGAGAGAATTGATCCTG" +
				"TATGACAAGGAGGAGATCAGGAGGATTTGGCGTCAAGCGAATAATGGAGAAGATGCGACT" +
				"GCTGGTCTCACCCATTTGATGATCTGGCATTCCAATCTGAATGATGCCACATATCAGAGA" +
				"ACAAGGGCACTTGTGCGCAGTGGGATGGACCCCAGAATGTGCTCTCTGATGCAAGGCTCG" +
				"ACTCTTCCGAGGAGATCTGGAGCGGCCGGAGCAGCAGTAAAAGGAGTTGGTACAATGGTG" +
				"ATGGAATTGGTCCGGATGATCAAGCGGGGAATCAATGATCGGAATTTCTGGAGAGGCGAA" +
				"AATGGACGGAGAACAAGAACTGCTTACGAAAGAATGTGCAACATTCTCAAGGGGAAATTC" +
				"CAAACAGCAGCACAACGAGCAATGATGGACCAGGTGAGGGAAAGCCGAAATCCTGGGAAT" +
				"GCTGAAATTGAAGATCTCATCTTCCTGGCACGATCCGCTCTCATTCTGAGAGGATCAGTG" +
				"GCTCACAAATCCTGTCTGCCTGCTTGTGTATATGGACTTGCTGTGGCCAGTGGATAYGAT" +
				"TTTGAAAGAGAGGGATACTCACTAGTCGGAATTGATCCTTTCCGCCTGCTTCAAAACAGT" +
				"CAAGTCTTCAGTCTTATAAGGCCGAACGAAAATCCAGCTCATAAAAGCCAACTGGTATGG" +
				"ATGGCATGTCACTCTGCAGCATTTGAGGATCTAAGAGTGTCAAGCTTCATCAGAGGAACA" +
				"AAAGTGGTTCCAAGAGGACAACTGTCCACCAGAGGAGTCCAAGTCGCTTCAAATGAGAAT" +
				"ATGGAGAYGATGGATTCCAGTACTCTTGAATTGAGAAGTAGATATTGGGCCATAAGAACC" +
				"AGAAGTGGAGGAAACACAAATCAGCAGAGAGCATCCGCTGGGCAAATCAGTGTACAGCCA" +
				"ACATTCTCTGTTCAGAGAAACCTCCCATTCGAGAGAGCAACCATTATGGCAGCATTTACA" +
				"GGAAACACTGAAGGCAGAACTTCAGACATGAGAACTGAGATCATAAGGATGATGGAAAAT" +
				"GCCAAACCTGAAGATGTGTCTTTCCAAGGGCGGGGAGTATTCGAGCTCTCGGACGAAAAG" +
				"GCAACGAACCCGATCGTGCCTTCCTTTGACATGAGTAACGAAGGATCTTATTTCTTCGGA" +
				"GACAATGCAGAGGAGTATGACAATTGAAGAAAA").build();
		return expectedSequence;
	}
	
	@Test
	public void idIterator() throws DataStoreException{
		StreamingIterator<String> iter = sut.idIterator();
		for(int i=1; i<=9; i++){
			assertEquals(Integer.toString(i), iter.next());
		}
	}
	@Test
	public void iterator() throws DataStoreException{
		StreamingIterator<NucleotideSequence> iter = sut.iterator();
		//skip first 2 records
		iter.next();
		iter.next();
		assertEquals(sequenceForThirdRecord(), iter.next());
		for(int i=4; i<=9; i++){
			iter.next();
		}
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void close() throws IOException, DataStoreException{
		sut.close();
		assertTrue(sut.isClosed());
		try{
			sut.get("3");
		}catch(IllegalStateException expected){
			//pass
		}
	}
}
