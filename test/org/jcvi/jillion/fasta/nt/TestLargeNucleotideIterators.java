package org.jcvi.jillion.fasta.nt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandler;
import org.jcvi.jillion.core.residue.nt.Nucleotide.InvalidCharacterHandlers;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class TestLargeNucleotideIterators {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void iterator() throws IOException {
		File fasta = tmpDir.newFile();
		List<NucleotideFastaRecord> expected = writeValidDataset(fasta);
		InvalidCharacterHandler handler = null;
		parseAndIterate(fasta, expected, handler);
	}
	private void parseAndIterate(File fasta, List<NucleotideFastaRecord> expected, InvalidCharacterHandler handler)
			throws IOException {
		try(StreamingIterator<NucleotideFastaRecord> iter = NucleotideFastaRecord.createNewIteratorFor(fasta, handler)){
			assertTrue(iter.hasNext());
			Iterator<NucleotideFastaRecord> expectedIter = expected.iterator();
			while(iter.hasNext()) {
				assertEquals(expectedIter.next(), iter.next());
			}
			assertFalse(expectedIter.hasNext());
		}
	}
	@Test
	public void forEach() throws IOException {
		File fasta = tmpDir.newFile();
		InvalidCharacterHandler handler=null;
		List<NucleotideFastaRecord> expected = writeValidDataset(fasta);
		List<NucleotideFastaRecord> actual = parseForEach(fasta, handler);
		assertEquals(expected, actual);
	}
	@Test
	public void iteratorInvalidNs() throws IOException {
		File fasta = tmpDir.newFile();
		InvalidCharacterHandler handler = InvalidCharacterHandlers.REPLACE_WITH_N;
		List<NucleotideFastaRecord> expected = writeInvalidDataset(fasta, handler);
		
		parseAndIterate(fasta, expected, handler);
	}
	@Test
	public void forEachInvalidNs() throws IOException {
		File fasta = tmpDir.newFile();
		InvalidCharacterHandler handler=InvalidCharacterHandlers.REPLACE_WITH_N;
		List<NucleotideFastaRecord> expected = writeInvalidDataset(fasta, handler);
		List<NucleotideFastaRecord> actual = parseForEach(fasta, handler);
		assertEquals(expected, actual);
	}
	@Test
	public void iteratorInvalidSkip() throws IOException {
		File fasta = tmpDir.newFile();
		InvalidCharacterHandler handler = InvalidCharacterHandlers.IGNORE;
		List<NucleotideFastaRecord> expected = writeInvalidDataset(fasta, handler);
		
		parseAndIterate(fasta, expected, handler);
	}
	@Test
	public void forEachInvalidSkip() throws IOException {
		File fasta = tmpDir.newFile();
		InvalidCharacterHandler handler=InvalidCharacterHandlers.IGNORE;
		List<NucleotideFastaRecord> expected = writeInvalidDataset(fasta, handler);
		List<NucleotideFastaRecord> actual = parseForEach(fasta, handler);
		assertEquals(expected, actual);
	}
	private List<NucleotideFastaRecord> parseForEach(File fasta, InvalidCharacterHandler handler) throws IOException {
		List<NucleotideFastaRecord> actual = new ArrayList<>();
		new NucleotideFastaFileDataStoreBuilder(fasta)
				.hint(DataStoreProviderHint.ITERATION_ONLY)
				.invalidCharacterHandler(handler)
				.build()
				.forEach((id, r) -> actual.add(r));
		return actual;
	}

	private List<NucleotideFastaRecord> writeValidDataset(File fasta) throws IOException {
		List<NucleotideFastaRecord> expected = write(fasta, Nucleotide.defaultInvalidCharacterHandler(),
														"foo", "ACGTACGT",
														"bar", "AAAAAAANNNNNN");
		return expected;
	}
	private List<NucleotideFastaRecord> writeInvalidDataset(File fasta, InvalidCharacterHandler handler) throws IOException {
		List<NucleotideFastaRecord> expected = write(fasta, handler,
														"foo", "ACGTACGT",
														"bar", "AAAAAAAZZZZZ");
		return expected;
	}
	
	private List<NucleotideFastaRecord> write(File fastaFile, InvalidCharacterHandler handler, String... idsAndSequences) throws IOException {
		List<NucleotideFastaRecord> list = new ArrayList<NucleotideFastaRecord>(idsAndSequences.length/2);
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(fastaFile))){
			for(int i=0; i< idsAndSequences.length; i+=2) {
				String id = idsAndSequences[i];
				String seq = idsAndSequences[2];
				writer.write(">"+ id);
				writer.newLine();
				writer.write(seq);
				writer.newLine();
				
				list.add(new NucleotideFastaRecordBuilder(id, new NucleotideSequenceBuilder(seq, handler).build()).build());
			}
		}
		return list;
	}
}
