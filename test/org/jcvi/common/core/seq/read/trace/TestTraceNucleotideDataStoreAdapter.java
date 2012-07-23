package org.jcvi.common.core.seq.read.trace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TestTraceNucleotideDataStoreAdapter extends EasyMockSupport{
	NucleotideSequence expectedSequence = createMock(NucleotideSequence.class);
	String id = "id";
	private DataStore<NucleotideSequence> sut;
	@Before
	public void setup(){
		Trace mockTrace = createMock(Trace.class);
		expect(mockTrace.getNucleotideSequence()).andStubReturn(expectedSequence);
		
		Map<String,Trace> map = new HashMap<String, Trace>();
		map.put(id, mockTrace);
		DataStore<Trace> datastore = MapDataStoreAdapter.adapt(map);
		
		sut = TraceNucleotideDataStoreAdapter.adapt(datastore);
		
		replayAll();
	}
	
	@Test
	public void getReturnsQualities() throws DataStoreException{		
		assertEquals(expectedSequence, sut.get(id));		
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
