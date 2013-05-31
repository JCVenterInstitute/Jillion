package org.jcvi.jillion.fasta.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.fasta.qual.IndexedQualityFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;
import org.junit.Test;

public class TestQualityFastaFileDataStoreBuilder {

	String path = "files/19150.qual";
	ResourceHelper helper = new ResourceHelper(TestQualityFastaFileDataStoreBuilder.class);
	
	private final File fasta;
	
	public TestQualityFastaFileDataStoreBuilder() throws IOException{
		fasta = helper.getFile(path);
	}
	
	@Test
	public void streamEqualsDefault() throws IOException, DataStoreException{
		QualityFastaDataStore fromFile =new QualityFastaFileDataStoreBuilder(fasta)
												.build();
		QualityFastaDataStore fromStream = createDataStoreFromStream();
		
		assertContainSameData(fromFile, fromStream);
	}

	public QualityFastaDataStore createDataStoreFromStream()
			throws FileNotFoundException, IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		return new QualityFastaFileDataStoreBuilder(in)
											.build();
	}
	
	public QualityFastaDataStore createDataStoreFromStream(DataStoreProviderHint hint)
			throws FileNotFoundException, IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(fasta));
		return new QualityFastaFileDataStoreBuilder(in)
											.hint(hint)
											.build();
	}
	
	@Test
	public void streamImplementationSameNoMatterWhatHintProvided() throws FileNotFoundException, IOException{
		QualityFastaDataStore defaultDataStore = createDataStoreFromStream();
		for(DataStoreProviderHint hint : DataStoreProviderHint.values()){
			assertEquals(createDataStoreFromStream(hint).getClass(), defaultDataStore.getClass());
		}
		
	}
	
	@Test
	public void optimizedMemoryUsesIndexedImpl() throws IOException, DataStoreException{
		QualityFastaDataStore indexed =new QualityFastaFileDataStoreBuilder(fasta)
												.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY)
												.build();
		assertTrue(indexed instanceof IndexedQualityFastaFileDataStore.Impl	);
	}
	
	@Test
	public void iterationOnlyImpl() throws IOException, DataStoreException{
		QualityFastaDataStore indexed =new QualityFastaFileDataStoreBuilder(fasta)
												.hint(DataStoreProviderHint.ITERATION_ONLY)
												.build();
		assertTrue(indexed instanceof LargeQualityFastaFileDataStore	);
	}

	private void assertContainSameData(QualityFastaDataStore fromFile,
			QualityFastaDataStore fromStream) throws DataStoreException {
		assertEquals(fromFile.getNumberOfRecords(), fromStream.getNumberOfRecords());
		StreamingIterator<QualityFastaRecord> iter=null;
		try{
			iter = fromFile.iterator();
			while(iter.hasNext()){
				QualityFastaRecord expected =iter.next();
				QualityFastaRecord actual = fromStream.get(expected.getId());
				assertEquals(expected, actual);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}
	}
}
