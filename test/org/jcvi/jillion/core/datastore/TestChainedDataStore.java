package org.jcvi.jillion.core.datastore;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.junit.Before;
import org.junit.Test;
@SuppressWarnings("unchecked")
public class TestChainedDataStore{

	
	 DataStore<String> datastore1, datastore2;
	    
	    DataStore<String> sut;
	    String id = "id";
	    DataStoreException datastoreException = new DataStoreException("expected exception");
	    
	    @Before
	    public void setup(){
	        datastore1 = createMock(DataStore.class);
	        datastore2 = createMock(DataStore.class);
	        sut = createSut(DataStore.class,datastore1,datastore2);
	    }

		protected <T, D extends DataStore<T>> D createSut(Class<D> clazz, D...dataStores) {
			return DataStoreUtil.chain(clazz, Arrays.asList(dataStores));
			
		}
	    
	    @Test
	    public void voidMethodShouldGetCalledForAll() throws IOException{
	        datastore1.close();
	        datastore2.close();
	        replay(datastore1, datastore2);
	        sut.close();
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void longMethodShouldSumTotal() throws DataStoreException{
	        expect(datastore1.getNumberOfRecords()).andReturn(100L);
	        expect(datastore2.getNumberOfRecords()).andReturn(50L);
	        replay(datastore1, datastore2);
	        assertEquals(150L, sut.getNumberOfRecords());
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void booleanShouldReturnFirstTrue_lastIsTrue() throws DataStoreException{
	        expect(datastore1.contains(id)).andReturn(false);
	        expect(datastore2.contains(id)).andReturn(true);
	        replay(datastore1, datastore2);
	        assertTrue(sut.contains(id));
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void booleanShouldReturnFirstTrue_FirstIsTrue() throws DataStoreException{
	        expect(datastore1.contains(id)).andReturn(true);
	        replay(datastore1, datastore2);
	        assertTrue(sut.contains(id));
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void booleanShouldReturnFirstTrue_NonAreTrue() throws DataStoreException{
	        expect(datastore1.contains(id)).andReturn(false);
	        expect(datastore2.contains(id)).andReturn(false);
	        replay(datastore1, datastore2);
	        assertFalse(sut.contains(id));
	        verify(datastore1, datastore2);
	    }
	    
	    @Test
	    public void iteratorShouldIterateOverAll() throws DataStoreException{
	        StreamingIterator<String> iter1 = IteratorUtil.createStreamingIterator(Arrays.asList("one","two").iterator());
	        StreamingIterator<String> iter2 = IteratorUtil.createStreamingIterator(Arrays.asList("three","four").iterator());
	        
	        Iterator<String> expectedIterator = Arrays.asList("one","two","three","four").iterator();
	        expect(datastore1.iterator()).andReturn(iter1);
	        expect(datastore2.iterator()).andReturn(iter2);
	        replay(datastore1, datastore2);
	        Iterator<String> actualIterator = sut.iterator();
	        while(expectedIterator.hasNext()){
	            assertEquals(expectedIterator.next(), actualIterator.next());
	        }
	        assertFalse(actualIterator.hasNext());
	        verify(datastore1, datastore2);
	    }
	    
	    @Test
	    public void closingIteratorShouldCloseAllIterators() throws IOException, DataStoreException{
	        StreamingIterator<String> iter1 = createMock(StreamingIterator.class);
	        StreamingIterator<String> iter2 = createMock(StreamingIterator.class);
	        iter1.close();
	        iter2.close();
	        expect(datastore1.iterator()).andReturn(iter1);
	        expect(datastore2.iterator()).andReturn(iter2);
	        replay(datastore1, datastore2,iter1,iter2);
	        StreamingIterator<String> actualIterator = sut.iterator();
	        actualIterator.close();       
	        verify(datastore1, datastore2,iter1,iter2);
	    }
	    @Test
	    public void getShouldGetFirstDoesNotContainIdSecondDoes() throws DataStoreException{
	        expect(datastore1.get(id)).andReturn(null);
	        expect(datastore2.get(id)).andReturn(id);
	        replay(datastore1, datastore2);
	        assertEquals(id, sut.get(id));
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void getShouldGetFirstValidFirstFirstHasIt() throws DataStoreException{
	        expect(datastore1.get(id)).andReturn(id);
	        replay(datastore1, datastore2);
	        assertEquals(id, sut.get(id));
	        verify(datastore1, datastore2);
	    }
	    @Test
	    public void getShouldGetFirstValidAllDoNotContainIdShouldReturnNull() throws DataStoreException{
	        expect(datastore1.get(id)).andReturn(null);
	        expect(datastore2.get(id)).andReturn(null);
	        replay(datastore1, datastore2);
	        assertNull(id, sut.get(id));
	        verify(datastore1, datastore2);
	    }
	    
	    @Test
	    public void getFirstDataStorethatContainsIdThrowsExceptionShouldTossUp() throws DataStoreException{
	        expect(datastore1.get(id)).andThrow(datastoreException);
	        replay(datastore1, datastore2);
	        try{
	            sut.get(id);
	        }catch(DataStoreException e){
	            assertEquals(datastoreException, e);
	        }
	        verify(datastore1, datastore2);
	    }
	    
	    @Test(expected = IllegalArgumentException.class)
	    public void noDelegatesShouldThrowIllegalArgumentException(){
	        createSut(DataStore.class);
	    }
	    
	    @Test(expected = NullPointerException.class)
	    public void nullDelegatesShouldThrowNullPointerException(){
	        createSut(DataStore.class,datastore1,null,datastore2);
	    }

}
