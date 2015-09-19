/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.trace.Trace;
import org.jcvi.jillion.trace.TraceNucleotideDataStoreAdapter;
import org.junit.Before;
import org.junit.Test;

public class TestTraceNucleotideDataStoreAdapter extends EasyMockSupport{
	NucleotideSequence expectedSequence = createMock(NucleotideSequence.class);
	String id = "id";
	private NucleotideSequenceDataStore sut;
	@Before
	public void setup(){
		Trace mockTrace = createMock(Trace.class);
		expect(mockTrace.getNucleotideSequence()).andStubReturn(expectedSequence);
		
		Map<String,Trace> map = new HashMap<String, Trace>();
		map.put(id, mockTrace);
		DataStore<Trace> datastore = DataStoreUtil.adapt(map);
		
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
