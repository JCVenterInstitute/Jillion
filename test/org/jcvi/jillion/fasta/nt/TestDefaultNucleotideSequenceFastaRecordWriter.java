/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.nt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestDefaultNucleotideSequenceFastaRecordWriter {
	private final NucleotideFastaRecord record1 = 
			new NucleotideFastaRecordBuilder("id_1", "ACGTACGT")
						.comment("a comment")
						.build();
		
	private final NucleotideFastaRecord record2 = 
			new NucleotideFastaRecordBuilder("id_2","AAAACCCCGGGGTTTT").build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new NucleotideFastaRecordWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws IOException{
		new NucleotideFastaRecordWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaRecordWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaRecordWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test(expected = IllegalArgumentException.class)
	public void emptyEOLShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new NucleotideFastaRecordWriterBuilder(out)
			.lineSeparator("");
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
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
	public void nullEOLShouldUseDefault() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
													.lineSeparator(null)
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
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
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
	public void differentEOL() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
								.numberPerLine(5)
								.lineSeparator("\r\n")
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		sut.close();
		
		String expected = ">id_1 a comment\r\n"+
							"ACGTA\r\nCGT\r\n"+
							">id_2\r\n"+
							"AAAAC\r\nCCCGG\r\nGGTTT\r\nT\r\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	
	@Test
	public void allOnOneLine() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
								.allBasesOnOneLine()											
								.build();
		
		
		
		sut.write(record1);		
		sut.write(record2);
		char[] seq = new char[1000];
		Arrays.fill(seq, 'G');
		
		sut.write("long", new NucleotideSequenceBuilder(new String(seq)).build());
		sut.close();
		
		String expected = ">id_1 a comment\n"+
							"ACGTACGT\n"+
							">id_2\n"+
							"AAAACCCCGGGGTTTT\n" +
							">long\n"+
							new String(seq)+"\n";
		assertEquals(expected, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void sequenceEndsAtEndOfLineExactly() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
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
		NucleotideFastaRecordWriter sut = new NucleotideFastaRecordWriterBuilder(out)
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
