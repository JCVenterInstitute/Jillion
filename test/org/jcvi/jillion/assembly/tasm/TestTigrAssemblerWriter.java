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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tasm;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestTigrAssemblerWriter {
	private static final ResourceHelper RESOURCES = new ResourceHelper(TestTigrAssemblerWriter.class);
	   
	private static final TasmContigDataStore tasmDataStore;
	static{	         
        try {
        	NucleotideSequenceFastaDataStore fullLengthFastas = new NucleotideSequenceFastaFileDataStoreBuilder(RESOURCES.getFile("files/giv-15050.fasta"))
														.hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_MEMORY)
														.build();
            tasmDataStore= DefaultTasmFileContigDataStore.create(RESOURCES.getFile("files/giv-15050.tasm"),fullLengthFastas);
        } catch (Exception e) {
            throw new IllegalStateException("could not parse contig file",e);
        } 
    }
	    
	    
    @Test(expected = NullPointerException.class)
    public void writeNullDataStoreShouldThrowNullPointerException() throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	TasmFileWriter.write((TasmContigDataStore)null,out);
    }
    @Test(expected = NullPointerException.class)
    public void writeNullOutputStreamShouldThrowNullPointerException() throws IOException{
    	TasmFileWriter.write(tasmDataStore,null);
    }
    @Test
    public void whenDataStoreThrowsExceptionShouldWrapInIOException() throws DataStoreException{
    	TasmContigDataStore mockDataStore = createMock(TasmContigDataStore.class);
    	DataStoreException expectedException = new DataStoreException("expected");
    	expect(mockDataStore.idIterator()).andThrow(expectedException);
    	replay(mockDataStore);
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
			TasmFileWriter.write(mockDataStore,out);
			fail("should wrap DataStoreException in IOException");
		} catch (IOException e) {
			assertEquals("error writing tasm file", e.getMessage());
			assertEquals(expectedException, e.getCause());
		}
    }
    @Test
    public void rewrittenTasmShouldMatchOriginalByteForByte() throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	TasmFileWriter.write(tasmDataStore,out);
    	byte[] expected = IOUtil.toByteArray(RESOURCES.getFileAsStream("files/giv-15050.tasm"));
    	assertArrayEquals(expected, out.toByteArray());
    }
}
