package org.jcvi.jillion.trace.sff;

import java.io.IOException;

import org.jcvi.common.core.assembly.util.trim.TrimPointsDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestTrimPointsDataStoreFromSff extends AbstractTestExampleSffFile{

	private final TrimPointsDataStore datastore;
	public TestTrimPointsDataStoreFromSff() throws IOException{
		datastore = SffUtil.createTrimPointsDataStoreFrom(SFF_FILE);
	}
	@Test
	public void isATrimPointsDataStore(){
		assertTrue(datastore instanceof TrimPointsDataStore);
	}
	@Test
	public void correctNumberOfRecords() throws DataStoreException{
		assertEquals(5, datastore.getNumberOfRecords());
	}
	@Test
	public void trimDataStoreIsCorrect() throws DataStoreException{		
		
		assertEquals(FF585OX02HCMO2.getQualityClip(),datastore.get("FF585OX02HCMO2"));
		assertEquals(FF585OX02HCD8G.getQualityClip(),datastore.get("FF585OX02HCD8G"));
		assertEquals(FF585OX02FNE4N.getQualityClip(),datastore.get("FF585OX02FNE4N"));
		assertEquals(FF585OX02GMGGN.getQualityClip(),datastore.get("FF585OX02GMGGN"));
		assertEquals(FF585OX02FHO5X.getQualityClip(),datastore.get("FF585OX02FHO5X"));
	}
}
