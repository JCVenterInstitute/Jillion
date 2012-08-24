package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import org.jcvi.common.core.datastore.DataStoreClosedException;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public abstract class AbstractTestAceDataStoreStreamingIterators {

	private AceFileContigDataStore datastore;
	private final File aceFile;
	
	public AbstractTestAceDataStoreStreamingIterators(){
		ResourceFileServer resources = new ResourceFileServer(AbstractTestAceDataStoreStreamingIterators.class);
		try {
			aceFile = resources.getFile("files/fluSample.ace");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	protected abstract AceFileContigDataStore createDataStore(File aceFile) throws IOException;
	
	@Before
	public void setupDataStore() throws IOException{
		datastore = createDataStore(aceFile);
	}
	
	@After
	public void closeDataStore(){
		IOUtil.closeAndIgnoreErrors(datastore);
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
