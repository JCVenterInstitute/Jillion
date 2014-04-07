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
package org.jcvi.jillion.core.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.junit.Before;
import org.junit.Test;
public class TestMapDataStoreAdapterProxy {

	Map<String, NucleotideSequence> map = new HashMap<String, NucleotideSequence>();
	
	NucleotideSequenceDataStore sut;
	public TestMapDataStoreAdapterProxy(){
		map.put("read1", new NucleotideSequenceBuilder("ACGTACGT")
								.build());
		map.put("read2", new NucleotideSequenceBuilder("AAAACCCCGGGGTTT")
							.build());
		
		
	}
	@Before
	public void createSut(){
		sut = DataStoreUtil.adapt(NucleotideSequenceDataStore.class, map);
	}
	@Test
	public void instanceOf(){
		assertTrue(sut instanceof NucleotideSequenceDataStore);
	}
	
	@Test
	public void get() throws DataStoreException{
		assertEquals(map.get("read1"), sut.get("read1"));
	}
	
	@Test
	public void size() throws DataStoreException{
		assertEquals(map.size(), sut.getNumberOfRecords());
	}
	
	@Test
	public void close() throws IOException{
		assertFalse(sut.isClosed());
		sut.close();
		assertTrue(sut.isClosed());
	}
}
