package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLargeAceFileDataStore extends TestAbstractAceParserMatchesAce2ContigMultipleContigs{

	public TestLargeAceFileDataStore() throws IOException, DataStoreException {
		super();
	}

	@Override
	protected List<AceContig> getContigList(File aceFile) throws IOException, DataStoreException {
		AceContigDataStore dataStore= LargeAceFileDataStore.create(aceFile);
		CloseableIterator<AceContig> iter = dataStore.iterator();
		List<AceContig> contigs = new ArrayList<AceContig>();
		try{
			while(iter.hasNext()){
				contigs.add(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}       
		dataStore.close();
         return contigs;
	}

	@Test
	public void size() throws IOException, DataStoreException{
		AceContigDataStore dataStore= LargeAceFileDataStore.create(getAceFile());
		
		long size = dataStore.getNumberOfRecords();
		long expected = 0;
		CloseableIterator<AceContig> iter = dataStore.iterator();
		try{
			while(iter.hasNext()){
				expected++;
				iter.next();
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		dataStore.close();
		assertEquals(expected, size);
	}
	
	@Test
	public void contains() throws DataStoreException, IOException{
		AceContigDataStore dataStore= LargeAceFileDataStore.create(getAceFile());
		CloseableIterator<AceContig> iter = dataStore.iterator();
		try{
			while(iter.hasNext()){
				assertTrue(dataStore.contains(iter.next().getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		dataStore.close();
	}
	
	@Test
	public void containsIdNotInDataStoreShouldReturnFalse() throws DataStoreException, FileNotFoundException, IOException{
		AceContigDataStore dataStore= LargeAceFileDataStore.create(getAceFile());
		assertFalse(dataStore.contains("not in datastore"));
	}
	
	@Test
	public void get() throws DataStoreException, IOException{
		AceContigDataStore dataStore= LargeAceFileDataStore.create(getAceFile());
		CloseableIterator<AceContig> iter = dataStore.iterator();
		try{
			while(iter.hasNext()){
				AceContig fromIter = iter.next();
				AceContig fromGet = dataStore.get(fromIter.getId());
				assertEquals("contigs from iterator vs get() don't match",fromIter, fromGet);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		dataStore.close();
	}

	@Override
	protected AceContigDataStore createDataStoreFor(File aceFile)
			throws IOException {
		return LargeAceFileDataStore.create(getAceFile());
	}
}
