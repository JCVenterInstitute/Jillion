package org.jcvi.seqmodel.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.jcvi.seqmodel.NucleotideSequence;
import org.junit.Test;

public class TestFastaNucleotideSequenceIO {


	URL nucleotideFastaUrl 	= this.getClass().getResource("../../io/files/TestAlnFasta.fna");
	private SequenceIO<NucleotideSequence> seqioNuc = new FastaNucleotideSequenceIO(nucleotideFastaUrl);
	
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
	
	@Test
	public void testNucleotideFastaSequenceIO() {
		int i = 0;
		for (NucleotideSequence s : seqioNuc) {
			assertFalse(s.getSequence().contains(","));
			assertFalse(s.getSequence().contains("\n"));
			assertFalse(s.getSequence().contains(" "));
			assertFalse(s.getId() == null);
			assertFalse(s.getAccession() == null);
			assertFalse(s.getGiNumber() == null);
			assertEquals(1775, s.getSequence().length());
			i++;
		}
		assertEquals(1044, i);
	}

}
