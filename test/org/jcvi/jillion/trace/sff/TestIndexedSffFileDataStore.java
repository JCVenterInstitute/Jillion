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
package org.jcvi.jillion.trace.sff;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.junit.Test;
public class TestIndexedSffFileDataStore extends AbstractTestExampleSffFile{

	@Test
	public void returnManifestIndexedInstanceIfManifestIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE)
										.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
										.build();
		assertTrue(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
	@Test
	public void returnManifestIndexedInstanceIfManifestWithNoXMLIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE_NO_XML)
												.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
												.build();
		assertTrue(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
	
	@Test
	public void returnFullyParsedIndexedInstanceIfNoIndexIsPresent() throws IOException{
		SffFileDataStore datastore = new SffFileDataStoreBuilder(SFF_FILE_NO_INDEX)
												.hint(DataStoreProviderHint.OPTIMIZE_LOW_MEMORY_RANDOM_ACCESS)
												.build();
		assertNotNull(datastore);
		assertFalse(datastore instanceof ManifestIndexed454SffFileDataStore);
	}
}
