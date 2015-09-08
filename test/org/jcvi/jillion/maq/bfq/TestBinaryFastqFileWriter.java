/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfq;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.jcvi.jillion.trace.fastq.FastqRecordBuilder;
import org.jcvi.jillion.trace.fastq.FastqWriter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TestBinaryFastqFileWriter {

	private FastqRecord record1 = new FastqRecordBuilder("id",
										new NucleotideSequenceBuilder("ACGT").build(), 
										new QualitySequenceBuilder(new byte[]{20,30,40,45}).build())
									.build();
	@Rule 
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void writeOneRecordNativeByteOrder() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		
		writer.write(record1);
		writer.close();
		
		assertContains(record1, out);
		
	}
	
	@Test
	public void capMaxQualityTo63() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		FastqRecord input = new FastqRecordBuilder("id",
									new NucleotideSequenceBuilder("ACGTT").build(), 
									new QualitySequenceBuilder(new byte[]{40,50,60,70,80}).build())
								.build();
		
		FastqRecord capped = new FastqRecordBuilder("id",
				new NucleotideSequenceBuilder("ACGTT").build(), 
				new QualitySequenceBuilder(new byte[]{40,50,60,63,63}).build())
			.build();
		
		writer.write(input);
		writer.close();
		
		assertContains(capped, out);
		
	}
	
	@Test
	public void nsGetZeroQuality() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		FastqRecord input = new FastqRecordBuilder("id",
									new NucleotideSequenceBuilder("ACNT").build(), 
									new QualitySequenceBuilder(new byte[]{20,20,20,20}).build())
								.build();
		
		FastqRecord expected = new FastqRecordBuilder("id",
				new NucleotideSequenceBuilder("ACNT").build(), 
				new QualitySequenceBuilder(new byte[]{20,20,0,20}).build())
			.build();
		
		writer.write(input);
		writer.close();
		
		assertContains(expected, out);
		
	}
	@Test
	public void gapGetsConvertedIntoNWithZeroQuality() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		FastqRecord input = new FastqRecordBuilder("id",
									new NucleotideSequenceBuilder("AC-T").build(), 
									new QualitySequenceBuilder(new byte[]{20,20,20,20}).build())
								.build();
		
		FastqRecord expected = new FastqRecordBuilder("id",
				new NucleotideSequenceBuilder("ACNT").build(), 
				new QualitySequenceBuilder(new byte[]{20,20,0,20}).build())
			.build();
		
		writer.write(input);
		writer.close();
		
		assertContains(expected, out);
		
	}
	@Test
	public void ambiguityGetsConvertedIntoNWithZeroQuality() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		FastqRecord input = new FastqRecordBuilder("id",
									new NucleotideSequenceBuilder("ACRT").build(), 
									new QualitySequenceBuilder(new byte[]{20,20,20,20}).build())
								.build();
		
		FastqRecord expected = new FastqRecordBuilder("id",
				new NucleotideSequenceBuilder("ACNT").build(), 
				new QualitySequenceBuilder(new byte[]{20,20,0,20}).build())
			.build();
		
		writer.write(input);
		writer.close();
		
		assertContains(expected, out);
		
	}
	
	@Test
	public void writeMultipleRecords() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.build();
		
		
		FastqRecord capped = new FastqRecordBuilder("id2",
									new NucleotideSequenceBuilder("ACGTT").build(), 
									new QualitySequenceBuilder(new byte[]{40,50,60,63,63}).build())
								.build();
		writer.write(record1);
		writer.write(capped);
		writer.close();
		
		assertContains(Arrays.asList(record1,capped), out, ByteOrder.nativeOrder());
	}
	
	@Test
	public void writeOneRecordBigEndian() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.endian(ByteOrder.BIG_ENDIAN)
											.build();
		
		
		writer.write(record1);
		writer.close();
		
		assertContains(record1, out, ByteOrder.BIG_ENDIAN);
		
	}
	
	@Test
	public void writeOneRecordLittleEndian() throws IOException, DataStoreException{
		File out =tmpDir.newFile();
		
		FastqWriter writer = new BfqFileWriterBuilder(out)
											.endian(ByteOrder.LITTLE_ENDIAN)
											.build();
		
		
		writer.write(record1);
		writer.close();
		
		assertContains(record1, out, ByteOrder.LITTLE_ENDIAN);
		
	}

	private void assertContains(FastqRecord expected, File out) throws IOException, DataStoreException {
		assertContains(Collections.singleton(expected),out, ByteOrder.nativeOrder());
		
	}
	private void assertContains(FastqRecord expected, File out, ByteOrder endian) throws IOException, DataStoreException {
		assertContains(Collections.singleton(expected),out, endian);
		
	}
	private void assertContains(Collection<FastqRecord> expected, File out, ByteOrder endian) throws IOException, DataStoreException {
		FastqDataStore datastore = new BfqFileDataStoreBuilder(out, endian)
										.build();
		assertEquals(expected.size(), datastore.getNumberOfRecords());
		for(FastqRecord r : expected){
			assertEquals(r, datastore.get(r.getId()));
		}
		
	}
	
}
