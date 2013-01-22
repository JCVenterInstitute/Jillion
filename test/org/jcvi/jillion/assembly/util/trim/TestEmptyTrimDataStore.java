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
package org.jcvi.jillion.assembly.util.trim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.jillion.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.jillion.assembly.util.trim.TrimPointsDataStoreUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.junit.Test;
public class TestEmptyTrimDataStore {

	TrimPointsDataStore sut = TrimPointsDataStoreUtil.createEmptyTrimPointsDataStore();
	
	@Test
	public void shouldContain0Records() throws DataStoreException{
		assertEquals(0, sut.getNumberOfRecords());
	}
	@Test
	public void shouldNeverContainAnything() throws DataStoreException{
		assertFalse(sut.contains("something"));
	}
	@Test
	public void iteratorShouldAlwaysBeEmpty() throws DataStoreException{
		Iterator<Range> iter= sut.iterator();
		assertFalse(iter.hasNext());
	}
	@Test
	public void idIteratorShouldAlwaysBeEmpty() throws DataStoreException{
		Iterator<String> iter= sut.idIterator();
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void getShouldAlwaysReturnNull() throws DataStoreException{
		assertNull(sut.get("something"));
	}
	
	@Test
	public void close() throws IOException{
		TrimPointsDataStore emptyDataStore = TrimPointsDataStoreUtil.createEmptyTrimPointsDataStore();
		assertFalse(emptyDataStore.isClosed());
		emptyDataStore.close();
		assertTrue(emptyDataStore.isClosed());
	}
}
