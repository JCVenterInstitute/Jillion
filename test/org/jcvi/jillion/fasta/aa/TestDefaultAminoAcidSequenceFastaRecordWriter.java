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
package org.jcvi.jillion.fasta.aa;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecord;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecordBuilder;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecordWriter;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecordWriterBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAminoAcidSequenceFastaRecordWriter {
	private final AminoAcidFastaRecord record1 = new AminoAcidFastaRecordBuilder("id_1", "CVGITPSA")
																.comment("a comment")
																.build();
	private final AminoAcidFastaRecord record2 = new AminoAcidFastaRecordBuilder("id_2", "CVGITPSAKASILHEV").build();
	
	@Test(expected = NullPointerException.class)
	public void nullOutputStreamShouldThrowNPE(){
		new AminoAcidFastaRecordWriterBuilder((OutputStream)null);
	}
	@Test(expected = NullPointerException.class)
	public void nullFileShouldThrowNPE() throws FileNotFoundException{
		new AminoAcidFastaRecordWriterBuilder((File)null);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativeBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new AminoAcidFastaRecordWriterBuilder(out)
			.numberPerLine(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroBasesPerLineShouldthrowIllegalArgumentException(){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new AminoAcidFastaRecordWriterBuilder(out)
			.numberPerLine(0);
	}
	@Test
	public void writeFastasWithDefaultOptions() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		AminoAcidFastaRecordWriter sut = new AminoAcidFastaRecordWriterBuilder(out)
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
		AminoAcidFastaRecordWriter sut = new AminoAcidFastaRecordWriterBuilder(out)
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
		AminoAcidFastaRecordWriter sut = new AminoAcidFastaRecordWriterBuilder(out)
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
		AminoAcidFastaRecordWriter sut = new AminoAcidFastaRecordWriterBuilder(out)
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
