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
package org.jcvi.jillion.fasta.pos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.pos.DefaultPositionFastaFileDataStore;
import org.jcvi.jillion.fasta.pos.PositionFastaRecord;
import org.jcvi.jillion.fasta.pos.PositionFastaRecordWriter;
import org.jcvi.jillion.fasta.pos.PositionFastaRecordWriterBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultPositionSequenceFastaRecordWriter {

	private final File peakFile;
	private final PositionFastaRecord fasta;
	public TestDefaultPositionSequenceFastaRecordWriter() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestDefaultPositionSequenceFastaRecordWriter.class);
		peakFile = resources.getFile("1119369023656.peak");
		
		fasta = DefaultPositionFastaFileDataStore.create(peakFile).get("1119369023656");
	}
	
	@Test
	public void rewriteShouldMatchExactly() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PositionFastaRecordWriter sut = new PositionFastaRecordWriterBuilder(out)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), IOUtil.UTF_8));
	}
	@Test
	public void differentCharSet() throws IOException{
		
		String asString = asString(peakFile);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Charset charset = Charset.forName("UTF-16");
		PositionFastaRecordWriter sut = new PositionFastaRecordWriterBuilder(out)
												.charset(charset)
												.build();
		
		sut.write(fasta);
		sut.close();
		assertEquals(asString, new String(out.toByteArray(), charset));
	}
	private String asString(File fastaFile) throws IOException{
		InputStream in = new FileInputStream(fastaFile);
		try{
			return IOUtil.toString(in);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
		
	}
}
