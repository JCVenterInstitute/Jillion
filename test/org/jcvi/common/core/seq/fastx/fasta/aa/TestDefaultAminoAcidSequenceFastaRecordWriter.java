package org.jcvi.common.core.seq.fastx.fasta.aa;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAminoAcidSequenceFastaRecordWriter {
	private final AminoAcidSequenceFastaRecord record1 = AminoAcidSequenceFastaRecordFactory.create("id_1", 
			new AminoAcidSequenceBuilder("CVGITPSA").build(),
			"a comment");
	private final AminoAcidSequenceFastaRecord record2 = AminoAcidSequenceFastaRecordFactory.create("id_2", 
			new AminoAcidSequenceBuilder("CVGITPSAKASILHEV").build());
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new AminoAcidSequenceFastaRecordWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws FileNotFoundException{
		new AminoAcidSequenceFastaRecordWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new AminoAcidSequenceFastaRecordWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new AminoAcidSequenceFastaRecordWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AminoAcidSequenceFastaRecordWriter sut = new AminoAcidSequenceFastaRecordWriterBuilder(out)
													.build();
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGITPSA\n"+
							">id_2\n"+
							"CVGITPSAKASILHEV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void multiLineFastas() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AminoAcidSequenceFastaRecordWriter sut = new AminoAcidSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGIT\nPSA\n"+
							">id_2\n"+
							"CVGIT\nPSAKA\nSILHE\nV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AminoAcidSequenceFastaRecordWriter sut = new AminoAcidSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(4)											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"CVGI\nTPSA\n"+
							">id_2\n"+
							"CVGI\nTPSA\nKASI\nLHEV\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void differentCharSet() throws IOException{
		Charset charSet = Charset.forName("UTF-16");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AminoAcidSequenceFastaRecordWriter sut = new AminoAcidSequenceFastaRecordWriterBuilder(out)
								.numberPerLine(5)	
								.charset(charSet)
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\n"+
						"CVGIT\nPSA\n"+
						">id_2\n"+
						"CVGIT\nPSAKA\nSILHE\nV\n";
		byte[] expectedBytes = expected.getBytes(charSet);
		assertArrayEquals(expectedBytes, out.toByteArray());
	}
}
