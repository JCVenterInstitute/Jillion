package org.jcvi.jillion.maq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Collections;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractTestBinaryFastqDataStore {

	//native order is little endian
	private static FastqDataStore expected;
	private static ResourceHelper resourceHelper = new ResourceHelper(AbstractTestBinaryFastqDataStore.class) ;
	
	@BeforeClass
	public static void parseFastqFile() throws IOException{
		expected = new FastqFileDataStoreBuilder(resourceHelper.getFile("sanger.capped.fastq"))
						.build();
	}
	
	@AfterClass
	public static void freeDataStore(){
		expected = null;
	}
	
	protected abstract DataStoreProviderHint getProviderHint();
	
	@Test
	public void allRecordsPresent() throws IOException, DataStoreException{
		DataStoreFilter filter = DataStoreFilters.alwaysAccept();
		FastqDataStore datastore = createDataStore(filter);
		assertEquals(2, datastore.getNumberOfRecords());
		StreamingIterator<FastqRecord> iter =null;
		try{
			iter =expected.iterator();
			while(iter.hasNext()){
				FastqRecord r = iter.next();
				assertEquals(r, datastore.get(r.getId()));
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		assertIteratorMatches(datastore, filter);
	}
	private void assertIteratorMatches(FastqDataStore actual, DataStoreFilter filter) throws DataStoreException{
		StreamingIterator<FastqRecord> actualIter =null;
		StreamingIterator<FastqRecord> expectedIter =null;
		
		try{
			actualIter = actual.iterator();
			expectedIter =expected.iterator();
			while(expectedIter.hasNext()){
				FastqRecord expected = expectedIter.next();
				if(filter.accept(expected.getId())){
					assertEquals(expected, actualIter.next());
				}
				
			}
			assertFalse(actualIter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(actualIter, expectedIter);
		}
	}
	@Test
	public void filterAllReads() throws IOException, DataStoreException{
		FastqDataStore datastore = createDataStore(DataStoreFilters.neverAccept());
		assertEquals(0, datastore.getNumberOfRecords());
		
	}
	@Test
	public void onlyIncludeSomeIds() throws IOException, DataStoreException{
		String id = "SOLEXA1_0007:2:13:163:254#GATCAG/2";
		FastqRecord fastqRecord = expected.get(id);
		DataStoreFilter filter = DataStoreFilters.newIncludeFilter(Collections.singleton(id));
		FastqDataStore datastore = createDataStore(filter);
		assertEquals(1, datastore.getNumberOfRecords());
		
		assertEquals(fastqRecord, datastore.get(id));
		
		StreamingIterator<FastqRecord> iter =null;
		try{
			iter =datastore.iterator();
			assertTrue(iter.hasNext());
			
			assertEquals(fastqRecord, iter.next());
			assertFalse(iter.hasNext());
			
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
		
		assertIteratorMatches(datastore, filter);
	}
	
	
	private FastqDataStore createDataStore(DataStoreFilter filter) throws IOException{
		File bfq = resourceHelper.getFile("sanger.capped.bfq");
		
		return new BinaryFastqFileDataStoreBuilder(bfq)
						.endian(ByteOrder.LITTLE_ENDIAN)
						.hint(getProviderHint())
						.filter(filter)
						.build();
	}
}
