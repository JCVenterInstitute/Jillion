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
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecord;
import org.junit.Test;
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

	private final ResourceHelper resources = new ResourceHelper(TestAbstractMultiLineFastqRecordsInDataStore.class);
	
	protected abstract FastqDataStore createFastqDataStoreFor(File fastq, FastqQualityCodec qualityCodec) throws IOException;
	
	protected abstract FastqDataStore createFastqDataStoreFor(File fastq, FastqQualityCodec qualityCodec, DataStoreFilter filter) throws IOException;
	
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
				assertEquals(single, multi);
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
		DataStoreFilter filter = DataStoreFilters.newIncludeFilter(include);
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
