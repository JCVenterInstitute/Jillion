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
package org.jcvi.jillion.assembly.consed.ace;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.consed.ace.AceAssembledRead;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestAceDataStoreStreamingIterators {

	private AceFileDataStore datastore;
	private final File aceFile;
	
	public AbstractTestAceDataStoreStreamingIterators(){
		ResourceHelper resources = new ResourceHelper(AbstractTestAceDataStoreStreamingIterators.class);
		try {
			aceFile = resources.getFile("files/fluSample.ace");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected abstract AceFileDataStore createDataStore(File aceFile) throws IOException;
	
	@Before
	public void setupDataStore() throws IOException{
		datastore = createDataStore(aceFile);
	}
	
	@After
	public void closeDataStore(){
		IOUtil.closeAndIgnoreErrors(datastore);
	}
	private Iterator<String> getExpectedReadIdsFor22934_PB1(){
		return Arrays.asList(
				"FCPRO0N01D9RBE.60-238",
				"contig00004",
				"22934-PB1",
				"FCPRO0N01DMHJ6.4-226",
				"FCPRO0N01DVJUW.1-215",
				"FCPRO0N01CS0EZ",
				"FCPRO0N01ED9U7",
				"contig00005",
				"FCPRO0N01BJPU5",
				"FCPRO0N01D9UY9",
				"FCPRO0N01DNY53",
				"FCPRO0N01EL2M4.1-149",
				"FCPRO0N01AQ199.1-213",
				"FCPRO0N01C8KJL.2-223",
				"FCPRO0N01DFY9W.2-222",
				"FCPRO0N01A6AIK",
				"contig00006",
				"FCPRO0N01EE7SR",
				"FCPRO0N01AE1KR",
				"FCPRO0N01EYACJ",
				"FCPRO0N01B4CYC",
				"FCPRO0N01DGTHP",
				"contig00007",
				"FCPRO0N01C9NO1",
				"FCPRO0N01EP3QY",
				"FCPRO0N01CQCIC.11-225",
				"FCPRO0N01C9XY8",
				"FCPRO0N01CBNSU",
				"FCPRO0N01DPTRO",
				"FCPRO0N01DZSSH",
				"FCPRO0N01D261C.2-232",
				"FCPRO0N01EY4MZ",
				"FCPRO0N01AQEM8.2-234",
				"FCPRO0N01BZI5N",
				"FCPRO0N01CQ0BI.3-224",
				"FCPRO0N01D3NPM",
				"FCPRO0N01BG1HF.5-218",
				"FCPRO0N01CYOTF",
				"FCPRO0N01DPDHL",
				"FCPRO0N01CRPPH",
				"FCPRO0N01APU1A",
				"FCPRO0N01B004F.228-1",
				"FCPRO0N01BZ0LI.228-1",
				"FCPRO0N01CRF75"

		).iterator();
	}
	
	
	@Test
	public void readOrderIsTheOrderInTheAceFile() throws DataStoreException{
		
		
		AceContig contig = datastore.get("22934-PB1");
		StreamingIterator<AceAssembledRead> iter = contig.getReadIterator();
		Iterator<String> expectedIterator = getExpectedReadIdsFor22934_PB1();
		try{
			while(expectedIterator.hasNext()){
				assertTrue(iter.hasNext());
				String expected = expectedIterator.next();
				String actual = iter.next().getId();
				assertEquals(expected, actual);
			}
			assertFalse(iter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
	
	private <T> void  callingNextAfterDataStoreClosedShouldThrowException(StreamingIterator<T> iter) throws IOException{
		assertTrue(iter.hasNext());
		datastore.close();
		try{
			iter.next();
			fail("should throw exception after datastore is closed");
		}catch(DataStoreClosedException expected){
			//expected			
		}
	}
	private <T> void  callingNextAfterDataStoreClosedButNoMoreElementsShouldThrowNoSuchElementException(StreamingIterator<T> iter) throws IOException{
		while(iter.hasNext()){
			iter.next();
		}
		datastore.close();
		try{
			iter.next();
			fail("should throw NoSuchElementException even after datastore is closed");
		}catch(NoSuchElementException expected){
			//expected			
		}
	}
	private <T> void  callingHasNextAfterDataStoreClosedButNoMoreElementsShouldReturnFalse(StreamingIterator<T> iter) throws IOException{
		while(iter.hasNext()){
			iter.next();
		}
		datastore.close();
		assertFalse(iter.hasNext());
	}
	private <T> void  callingHasNextAfterDataStoreClosedShouldThrowException(StreamingIterator<T> iter) throws IOException{
		assertTrue(iter.hasNext());
		datastore.close();
		try{
			iter.hasNext();
			fail("should throw exception after datastore is closed");
		}catch(DataStoreClosedException expected){
			//expected			
		}
	}
	@Test
	public void callingNextOnIteratorAfterDataStoreClosedShouldThrowException() throws DataStoreException, IOException{
		StreamingIterator<AceContig> iter =datastore.iterator();
		callingNextAfterDataStoreClosedShouldThrowException(iter);
	}
	@Test
	public void callingHasNextOnIteratorAfterDataStoreClosedShouldThrowException() throws DataStoreException, IOException{
		StreamingIterator<AceContig> iter =datastore.iterator();
		callingHasNextAfterDataStoreClosedShouldThrowException(iter);
	}
	@Test
	public void callingNextOnFinishedIteratorAfterDataStoreClosedShouldThrowNoSuchElementException() throws DataStoreException, IOException{
		StreamingIterator<AceContig> iter =datastore.iterator();
		callingNextAfterDataStoreClosedButNoMoreElementsShouldThrowNoSuchElementException(iter);
	}
	@Test
	public void callingHasNextOnFinishedIteratorAfterDataStoreClosedShouldReturnFalse() throws DataStoreException, IOException{
		StreamingIterator<AceContig> iter =datastore.iterator();
		callingHasNextAfterDataStoreClosedButNoMoreElementsShouldReturnFalse(iter);
	}
	@Test
	public void callingNextOnIdIteratorAfterDataStoreClosedShouldThrowException() throws DataStoreException, IOException{
		StreamingIterator<String> iter =datastore.idIterator();
		callingNextAfterDataStoreClosedShouldThrowException(iter);
	}
	
	
	@Test
	public void callingHasNextOnIdIteratorAfterDataStoreClosedShouldThrowException() throws DataStoreException, IOException{
		StreamingIterator<String> iter =datastore.idIterator();
		callingHasNextAfterDataStoreClosedShouldThrowException(iter);
	}
	
	@Test
	public void callingNextOnFinishedIdIteratorAfterDataStoreClosedShouldThrowNoSuchElementException() throws DataStoreException, IOException{
		StreamingIterator<String> iter =datastore.idIterator();
		callingNextAfterDataStoreClosedButNoMoreElementsShouldThrowNoSuchElementException(iter);
	}
	@Test
	public void callingHasNextOnFinishedIdIteratorAfterDataStoreClosedShouldReturnFalse() throws DataStoreException, IOException{
		StreamingIterator<String> iter =datastore.idIterator();
		callingHasNextAfterDataStoreClosedButNoMoreElementsShouldReturnFalse(iter);
	}
}
