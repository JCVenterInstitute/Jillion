package org.jcvi.common.core.assembly.util.trim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.datastore.DataStoreException;
import org.junit.Test;
public class TestEmptyTrimDataStore {

	TrimPointsDataStore sut = TrimDataStoreUtil.createEmptyTrimPointsDataStore();
	
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
		TrimPointsDataStore emptyDataStore = TrimDataStoreUtil.createEmptyTrimPointsDataStore();
		assertFalse(emptyDataStore.isClosed());
		emptyDataStore.close();
		assertTrue(emptyDataStore.isClosed());
	}
}
