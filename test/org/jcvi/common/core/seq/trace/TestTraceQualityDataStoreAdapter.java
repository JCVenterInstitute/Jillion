package org.jcvi.common.core.seq.trace;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.seq.trace.Trace;
import org.jcvi.common.core.seq.trace.TraceQualityDataStoreAdapter;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.junit.Before;
import org.junit.Test;
public class TestTraceQualityDataStoreAdapter extends EasyMockSupport{
	QualitySequence expectedQualities = createMock(QualitySequence.class);
	String id = "id";
	private DataStore<QualitySequence> sut;
	@Before
	public void setup(){
		Trace mockTrace = createMock(Trace.class);
		expect(mockTrace.getQualitySequence()).andStubReturn(expectedQualities);
		
		Map<String,Trace> map = new HashMap<String, Trace>();
		map.put(id, mockTrace);
		DataStore<Trace> datastore = DataStoreUtil.adapt(map);
		
		sut = TraceQualityDataStoreAdapter.adapt(datastore);
		
		replayAll();
	}
	
	@Test
	public void getReturnsQualities() throws DataStoreException{		
		assertEquals(expectedQualities, sut.get(id));		
	}
	
	@Test
	public void size() throws DataStoreException{
		assertEquals(1, sut.getNumberOfRecords());
	}
	
	@Test
	public void getIds() throws DataStoreException{
		Iterator<String> ids = sut.idIterator();
		
		assertTrue(ids.hasNext());
		assertEquals(id, ids.next());
		assertFalse(ids.hasNext());
	}
}
