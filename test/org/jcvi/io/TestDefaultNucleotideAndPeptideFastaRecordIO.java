package org.jcvi.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.jcvi.fasta.NucleotideSequenceFastaRecord;
import org.jcvi.fasta.PeptideSequenceFastaRecord;
import org.junit.Test;

public class TestDefaultNucleotideAndPeptideFastaRecordIO {

	URL nucleotideFastaUrl 	= this.getClass().getResource("files/TestAlnFasta.fna");
	URL peptideFastaUrl		= this.getClass().getResource("files/TestPeptideFasta.faa");
	NucleotideFastaRecordIO nucFastaIO = new DefaultNucleotideFastaRecordIO(nucleotideFastaUrl);
	PeptideFastaRecordIO pepFastaIO = new DefaultPeptideFastaRecordIO(peptideFastaUrl);
	
	@Test
	public void testReadFile() {
		String line;
		int i = 0;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(nucleotideFastaUrl.getFile()));
			while ((line = buff.readLine()) != null) {
				if (line.startsWith(">")) {
					i++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals(i, 1044);
	}
	
	//@Test
	public void testNucleotideFastaRecordIO() {
		int i = 0;
		for (NucleotideSequenceFastaRecord f : nucFastaIO) {
			assertFalse(f.getIdentifier() == null);
			assertFalse(f.getComments() == null);
			assertFalse(f.getValues().toString().length() < 100);
			assertEquals(1775, f.getValues().decode().size());
			assertEquals(1775, f.getValues().getLength());
			i++;
		}
		assertEquals(1044, i);
	}
	
	@Test
	public void testPeptideFastaRecordReader() {
		int i = 0;
		for (PeptideSequenceFastaRecord f : pepFastaIO) {
			assertFalse(f.getIdentifier() == null);
			assertFalse(f.getComments() == null);
			assertEquals(565, f.getValues().decode().size());
			assertEquals(565, f.getValues().getLength());
			i++;
		}
		assertEquals(1044, i);
	}

}
