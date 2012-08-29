package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultNucleotideSequenceFastaRecordWriter {
	private final NucleotideSequenceFastaRecord record1 = NucleotideSequenceFastaRecordFactory.create("id_1", 
			new NucleotideSequenceBuilder("ACGTACGT").build(),
			"a comment");
	private final NucleotideSequenceFastaRecord record2 = NucleotideSequenceFastaRecordFactory.create("id_2", 
			new NucleotideSequenceBuilder("AAAACCCCGGGGTTTT").build());
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new DefaultNucleotideSequenceFastaRecordWriter.Builder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws FileNotFoundException{
		new DefaultNucleotideSequenceFastaRecordWriter.Builder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new DefaultNucleotideSequenceFastaRecordWriter.Builder(out)
			.basesPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new DefaultNucleotideSequenceFastaRecordWriter.Builder(out)
			.basesPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideSequenceFastaRecordWriter sut = new DefaultNucleotideSequenceFastaRecordWriter.Builder(out)
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
		NucleotideSequenceFastaRecordWriter sut = new DefaultNucleotideSequenceFastaRecordWriter.Builder(out)
								.basesPerLine(5)											
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
		NucleotideSequenceFastaRecordWriter sut = new DefaultNucleotideSequenceFastaRecordWriter.Builder(out)
								.basesPerLine(4)											
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
}
