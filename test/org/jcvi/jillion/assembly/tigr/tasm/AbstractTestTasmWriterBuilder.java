/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
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
package org.jcvi.jillion.assembly.tigr.tasm;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public abstract class  AbstractTestTasmWriterBuilder {
	private ResourceHelper helper = new ResourceHelper(AbstractTestTasmWriterBuilder.class);
	
	protected abstract TasmWriter createTasmWriterFor(File inputTasm);
	
	protected abstract byte[] getWrittenBytes();
	
	@Test
	public void writtenTasmShouldMatchInputTasm() throws IOException, DataStoreException{
		File tasmFile = helper.getFile("files/giv-15050.tasm");
		File readFile = helper.getFile("files/giv-15050.fasta");
		NucleotideFastaDataStore reads = new NucleotideFastaFileDataStoreBuilder(readFile)
												.build();
		TasmContigDataStore datastore = new TasmContigFileDataStoreBuilder(tasmFile, reads)
											.build();

		TasmWriter writer= createTasmWriterFor(tasmFile);
		
		StreamingIterator<TasmContig> iter=datastore.iterator();
		try{
			while(iter.hasNext()){
				TasmContig contig = iter.next();
				writer.write(contig);
			}
			writer.close();
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		byte[] actualBytes = getWrittenBytes();
		assertArrayEquals(IOUtil.toByteArray(tasmFile), actualBytes);
		
	}
	
	@Test
	public void writeAnnotationTasms() throws IOException, DataStoreException{
		File tasmFile = helper.getFile("files/annotation.tasm");
		//we don't have any reads so we can just mock it
		NucleotideFastaDataStore reads = createMock(NucleotideFastaDataStore.class);
		replay(reads);
		TasmContigDataStore datastore = new TasmContigFileDataStoreBuilder(tasmFile, reads)
											.build();
		
		ByteArrayOutputStream out = new ByteArrayOutputStream((int)tasmFile.length());
		
		TasmWriter writer = new TasmFileWriterBuilder(out)
								.writeAnnotationContigs()
								.build();
		
		StreamingIterator<TasmContig> iter=datastore.iterator();
		try{
			while(iter.hasNext()){
				TasmContig contig = iter.next();
				writer.write(contig);
			}
			writer.close();
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		byte[] actualBytes = out.toByteArray();
		
		
		assertArrayEquals(IOUtil.toByteArray(tasmFile), actualBytes);
	}
}
