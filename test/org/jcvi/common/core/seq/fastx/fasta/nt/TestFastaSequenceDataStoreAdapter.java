package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestFastaSequenceDataStoreAdapter {

	private final File fastaFile;
	private NucleotideDataStore sut;
	public TestFastaSequenceDataStoreAdapter() throws IOException{
		ResourceFileServer resources = new ResourceFileServer(TestFastaSequenceDataStoreAdapter.class);
		fastaFile = resources.getFile("files/19150.fasta");
	}
	
	@Before
	public void createDataStore() throws FileNotFoundException{
		sut = FastaRecordDataStoreAdapter.adapt(NucleotideDataStore.class, DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile));
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
