package org.jcvi.jillion.maq;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.junit.Test;

public class TestBinaryFastaFileWriter extends AbstractTestBinaryFastaFile{

	
	@Test
	public void writeAllRecordsShouldMatchMaqByteForByte() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		NucleotideFastaRecordWriter writer = new BinaryFastaFileWriterBuilder(out)
													.endian(ByteOrder.LITTLE_ENDIAN)
													.build();
		
		writer.write(forward);
		writer.write(reverse);
		writer.close();
		byte[] actual = out.toByteArray();
		byte[] expected = IOUtil.toByteArray(getHelper().getFile("seqs.bfa"));
		assertArrayEquals(expected, actual);
	}
}
