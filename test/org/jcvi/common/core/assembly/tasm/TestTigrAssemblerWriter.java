/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.common.core.assembly.tasm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.jcvi.common.core.assembly.tasm.DefaultTigrAssemblerFileContigDataStore;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerContigDataStore;
import org.jcvi.common.core.assembly.tasm.TigrAssemblerWriter;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.io.fileServer.FileServer;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestTigrAssemblerWriter {
	private static final FileServer RESOURCES = new ResourceFileServer(TestTigrAssemblerWriter.class);
	   
	private static final DefaultTigrAssemblerFileContigDataStore tasmDataStore;
	static{	         
        try {
            tasmDataStore= new DefaultTigrAssemblerFileContigDataStore(RESOURCES.getFile("files/giv-15050.tasm"));
        } catch (Exception e) {
            throw new IllegalStateException("could not parse contig file",e);
        } 
    }
	    
	    
    @Test(expected = NullPointerException.class)
    public void writeNullDataStoreShouldThrowNullPointerException() throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	TigrAssemblerWriter.write((DefaultTigrAssemblerFileContigDataStore)null,out);
    }
    @Test(expected = NullPointerException.class)
    public void writeNullOutputStreamShouldThrowNullPointerException() throws IOException{
    	TigrAssemblerWriter.write(tasmDataStore,null);
    }
    @Test
    public void whenDataStoreThrowsExceptionShouldWrapInIOException() throws DataStoreException{
    	TigrAssemblerContigDataStore mockDataStore = createMock(TigrAssemblerContigDataStore.class);
    	DataStoreException expectedException = new DataStoreException("expected");
    	expect(mockDataStore.getIds()).andThrow(expectedException);
    	replay(mockDataStore);
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
			TigrAssemblerWriter.write(mockDataStore,out);
			fail("should wrap DataStoreException in IOException");
		} catch (IOException e) {
			assertEquals("error writing tasm file", e.getMessage());
			assertEquals(expectedException, e.getCause());
		}
    }
    @Test
    public void rewrittenTasmShouldMatchOriginalByteForByte() throws IOException{
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	TigrAssemblerWriter.write(tasmDataStore,out);
    	byte[] expected = IOUtils.toByteArray(RESOURCES.getFileAsStream("files/giv-15050.tasm"));
    	assertArrayEquals(expected, out.toByteArray());
    }
}
