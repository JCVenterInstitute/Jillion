package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * The fastq spec actually allows
 * multi-line sequences and qualities but most
 * writes only use one line for each.
 * This made some users and clients lazy
 * and assume each fastq record is only 4 lines long.
 * However some newer next-gen (3rd ?) sequencers can
 * produce very long reads that can span multiple lines
 * if written out as fastq.
 * Test that we can handle those.
 * @author dkatzel
 *
 */
public abstract class TestAbstractMultiLineFastqRecordsInDataStore {

	private final ResourceFileServer resources = new ResourceFileServer(TestAbstractMultiLineFastqRecordsInDataStore.class);
	
	protected abstract FastqDataStore createFastqDataStoreFor(File fastq, FastqQualityCodec qualityCodec) throws IOException;
	
	protected abstract FastqDataStore createFastqDataStoreFor(File fastq, FastqQualityCodec qualityCodec, FastXFilter filter) throws IOException;
	
	@Test
	public void multiLineMatchesSingleline() throws IOException, DataStoreException{
		FastqDataStore singleDataStore = createFastqDataStoreFor(
				resources.getFile("files/sanger.fastq"),
				FastqQualityCodec.SANGER);
		
		FastqDataStore multilineDataStore = createFastqDataStoreFor(
				resources.getFile("files/multiline.fastq"),
				FastqQualityCodec.SANGER);
		
		assertDataStoresEqual(singleDataStore, multilineDataStore);
		
	}


	private void assertDataStoresEqual(FastqDataStore singleDataStore,
			FastqDataStore multilineDataStore) throws DataStoreException {
		assertEquals(singleDataStore.getNumberOfRecords(), 
				multilineDataStore.getNumberOfRecords());
		
		StreamingIterator<FastqRecord> singleIter=null;
		StreamingIterator<FastqRecord> multiIter=null;
		try{
			singleIter = singleDataStore.iterator();
			multiIter = multilineDataStore.iterator();
			while(singleIter.hasNext()){
				FastqRecord single = singleIter.next();
				FastqRecord multi = multiIter.next();
				assertEquals(single.toFormattedString(), multi.toFormattedString());
			}
			assertFalse(multiIter.hasNext());
		}finally{
			IOUtil.closeAndIgnoreErrors(singleIter, multiIter);
		}
	}
	/**
	 * Should force parser to skip some records
	 * @throws IOException 
	 * @throws DataStoreException 
	 */
	@Test
	public void filteredDataStore() throws IOException, DataStoreException{
		List<String> include = Arrays.asList("SOLEXA1_0007:2:13:163:254#GATCAG/2");
		FastXFilter filter = new IncludeFastXIdFilter(include);
		FastqDataStore singleDataStore = createFastqDataStoreFor(
				resources.getFile("files/sanger.fastq"),
				FastqQualityCodec.SANGER,
				filter);
		
		FastqDataStore multilineDataStore = createFastqDataStoreFor(
				resources.getFile("files/multiline.fastq"),
				FastqQualityCodec.SANGER,
				filter);
		
		assertDataStoresEqual(singleDataStore, multilineDataStore);
	}
}
