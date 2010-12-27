package org.jcvi.seqmodel.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.jcvi.seqmodel.AminoAcidSequence;
import org.junit.Test;

public class TestFastaPeptideSequenceIO {

	URL peptideFastaUrl		= this.getClass().getResource("../../io/files/TestPeptideFasta.faa");
	private SequenceIO<AminoAcidSequence> seqioPep = new FastaPeptideSequenceIO(peptideFastaUrl);
	
	@Test
	public void testReadFile() {
		String line;
		int i = 0;
		try {
			BufferedReader buff = new BufferedReader(new FileReader(peptideFastaUrl.getFile()));
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
	public void testPeptideFastaSequenceIO() {
		int i = 0;
		for (AminoAcidSequence s : seqioPep) {
			assertTrue(s instanceof AminoAcidSequence);
			assertFalse(s.getId() == null);
			assertEquals(565, s.getSequence().length());
			i++;
		}
		assertEquals(1044, i);
	}
	
}
