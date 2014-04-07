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
package org.jcvi.jillion.assembly.tigr.tasm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContig;
import org.jcvi.jillion.assembly.tigr.contig.TigrContigDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Test;

public abstract class AbstractTestTasmDataStore { 
	protected static TigrContigDataStore contigDataStore;
	protected static TasmContigDataStore tasmDataStore;
	
	
	private final Map<String,String> contigToTasmIdMap;
	
	AbstractTestTasmDataStore(){
		contigToTasmIdMap = new HashMap<String, String>();
		contigToTasmIdMap.put("15044", "1122071329926");
		contigToTasmIdMap.put("15045", "1122071329927");
		contigToTasmIdMap.put("15046", "1122071329928");
		contigToTasmIdMap.put("15047", "1122071329929");
		contigToTasmIdMap.put("15048", "1122071329930");
		contigToTasmIdMap.put("15057", "1122071329931");
		contigToTasmIdMap.put("26303", "1122071329932");
		contigToTasmIdMap.put("27233", "1122071329933");
		contigToTasmIdMap.put("27235", "1122071329934");
	}
	
	
	
	@Test
	public void isClosed() throws DataStoreException{
		assertFalse(tasmDataStore.isClosed());
	}
	@Test
	public void numberOfRecords() throws DataStoreException{
		assertEquals(contigDataStore.getNumberOfRecords(), tasmDataStore.getNumberOfRecords());
	}
	
	@Test
	public void idIterator() throws DataStoreException{
		StreamingIterator<String> contigIter = null;
		StreamingIterator<String> tasmIter = null;
		try{
			contigIter = contigDataStore.idIterator();
			tasmIter = tasmDataStore.idIterator();
			while(contigIter.hasNext()){
				assertTrue(tasmIter.hasNext());
				String contigId = contigIter.next();
				assertEquals(contigToTasmIdMap.get(contigId), tasmIter.next());
			}
			assertFalse(tasmIter.hasNext());
		
		}finally{
			IOUtil.closeAndIgnoreErrors(contigIter, tasmIter);
		}
	}
	
	@Test
	public void iterator() throws DataStoreException{
		StreamingIterator<TigrContig> contigIter = null;
		StreamingIterator<TasmContig> tasmIter = null;
		try{
			contigIter = contigDataStore.iterator();
			tasmIter = tasmDataStore.iterator();
			while(contigIter.hasNext()){
				assertTrue(tasmIter.hasNext());
				TigrContig contig = contigIter.next();
				TasmContig tasm = tasmIter.next();
				assertContigDataMatches(contig,tasm);
			}
			assertFalse(tasmIter.hasNext());
		
		}finally{
			IOUtil.closeAndIgnoreErrors(contigIter, tasmIter);
		}
	}
	
	@Test
	public void PB2() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15044"),
				tasmDataStore.get("1122071329926"));
	}

	@Test
	public void PB1() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15045"),
				tasmDataStore.get("1122071329927"));
	}

	@Test
	public void PA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15046"),
				tasmDataStore.get("1122071329928"));
	}

	@Test
	public void NP() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15047"),
				tasmDataStore.get("1122071329929"));
	}

	@Test
	public void MP() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15048"),
				tasmDataStore.get("1122071329930"));
	}

	@Test
	public void NS() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("15057"),
				tasmDataStore.get("1122071329931"));
	}

	@Test
	public void HA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("26303"),
				tasmDataStore.get("1122071329932"));
	}

	@Test
	public void NA() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("27233"),
				tasmDataStore.get("1122071329933"));
	}

	@Test
	public void lastContig() throws DataStoreException {
		assertContigDataMatches(contigDataStore.get("27235"),
				tasmDataStore.get("1122071329934"));
	}

	private void assertContigDataMatches(
			Contig<? extends AssembledRead> contig, TasmContig tasm) {
		assertEquals("consensus", contig.getConsensusSequence(),
				tasm.getConsensusSequence());
		assertEquals("#reads", contig.getNumberOfReads(),
				tasm.getNumberOfReads());
		assertFalse(tasm.isAnnotationContig());
		TigrAssemblerTestUtil.assertAllReadsCorrectlyPlaced(contig, tasm);

	}

}
