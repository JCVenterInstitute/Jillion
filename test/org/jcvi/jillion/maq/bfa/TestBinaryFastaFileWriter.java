/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfa;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecordWriter;
import org.junit.Test;

public class TestBinaryFastaFileWriter extends AbstractTestBinaryFastaFile{

	
	@Test
	public void writeAllRecordsShouldMatchMaqByteForByte() throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		NucleotideFastaRecordWriter writer = new BfaWriterBuilder(out)
													.endian(ByteOrder.LITTLE_ENDIAN)
													.build();
		
		writer.write(forward);
		writer.write(reverse);
		writer.close();
		byte[] actual = out.toByteArray();
		byte[] expected = IOUtil.toByteArray(getHelper().getFile("seqs.bfa"));
		assertArrayEquals(expected, actual);
	}
	
	@Test
	public void encodeBigEndian() throws IOException, DataStoreException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		NucleotideFastaRecordWriter writer = new BfaWriterBuilder(out)
													.endian(ByteOrder.BIG_ENDIAN)
													.build();
		writer.write(forward);
		writer.write(reverse);
		writer.close();
		FastaParser parser = BfaParser.create(new ByteArrayInputStream(out.toByteArray()), ByteOrder.BIG_ENDIAN);
		
		NucleotideFastaDataStore datastore = new NucleotideFastaFileDataStoreBuilder(parser)
													.build();
		assertEquals(2, datastore.getNumberOfRecords());
		assertEquals(forward, datastore.get(forward.getId()));
		assertEquals(reverse, datastore.get(reverse.getId()));
	}
}
