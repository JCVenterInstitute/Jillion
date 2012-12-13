package org.jcvi.common.core.seq.fasta.nt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordBuilder;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaRecordWriterBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultNucleotideSequenceFastaRecordWriter {
	private final NucleotideSequenceFastaRecord record1 = 
			new NucleotideSequenceFastaRecordBuilder("id_1", "ACGTACGT")
						.comment("a comment")
						.build();
		
	private final NucleotideSequenceFastaRecord record2 = 
			new NucleotideSequenceFastaRecordBuilder("id_2","AAAACCCCGGGGTTTT").build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new NucleotideSequenceFastaRecordWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws FileNotFoundException{
		new NucleotideSequenceFastaRecordWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideSequenceFastaRecordWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideSequenceFastaRecordWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideSequenceFastaRecordWriter sut = new NucleotideSequenceFastaRecordWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTACGT\n"+
							">id_2\n"+
							"AAAACCCCGGGGTTTT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideSequenceFastaRecordWriter sut = new NucleotideSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTA\nCGT\n"+
							">id_2\n"+
							"AAAAC\nCCCGG\nGGTTT\nT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideSequenceFastaRecordWriter sut = new NucleotideSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGT\nACGT\n"+
							">id_2\n"+
							"AAAA\nCCCC\nGGGG\nTTTT\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideSequenceFastaRecordWriter sut = new NucleotideSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTA\nCGT\n"+
							">id_2\n"+
							"AAAAC\nCCCGG\nGGTTT\nT\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
}
