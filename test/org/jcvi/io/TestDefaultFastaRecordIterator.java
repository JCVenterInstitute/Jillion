package org.jcvi.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.jcvi.fasta.FastaRecord;
import org.junit.Test;


public class TestDefaultFastaRecordIterator {

	InputStream fastaStream = this.getClass().getResourceAsStream("files/TestFasta.fna");
	private DefaultFastaRecordIterator reader = new DefaultFastaRecordIterator(fastaStream);
	
	@Test
	public void testReadFile() {
		
		BufferedReader buff = new BufferedReader(new InputStreamReader(fastaStream));
		String line;
		int i = 0;
		try {
			while ((line = buff.readLine()) != null) {
				if (line.startsWith(">")) {
					i++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("Number of records:" + i);
		assertEquals(i, 1044);
	}
	
	@Test
	public void testReader() {
		int i = 0;
		for (FastaRecord f : reader) {
			assertFalse(f.getIdentifier() == null);
			assertFalse(f.getComments() == null);
			assertFalse(f.getValues().toString().length() < 100);
			i++;
		}
		assertEquals(1044, i);
	}
	
}
