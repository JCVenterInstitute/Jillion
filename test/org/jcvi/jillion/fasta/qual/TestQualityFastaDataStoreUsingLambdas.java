package org.jcvi.jillion.fasta.qual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestQualityFastaDataStoreUsingLambdas {

	private static File qualFile;
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestQualityFastaDataStoreUsingLambdas.class);
		qualFile = helper.getFile("files/19150.qual");
	}
	
	@Parameters
	public static List<Object[]> data(){
		Consumer<QualityFastaFileDataStoreBuilder> inMem = builder -> builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED);
		Consumer<QualityFastaFileDataStoreBuilder> memento = builder -> builder.hint(DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_MEMORY);
		Consumer<QualityFastaFileDataStoreBuilder> iterOnly = builder -> builder.hint(DataStoreProviderHint.ITERATION_ONLY);
		
		return Arrays.asList(
				new Object[]{inMem },
				new Object[]{memento},
				new Object[]{iterOnly}
				);
	}
	
	private final Consumer<QualityFastaFileDataStoreBuilder> hinter;
	
	
	
	
	public TestQualityFastaDataStoreUsingLambdas(Consumer<QualityFastaFileDataStoreBuilder> hinter) {
		this.hinter = hinter;
	}

	@Test
	public void noFiltering() throws DataStoreException, IOException{
		QualityFastaFileDataStoreBuilder builder = new QualityFastaFileDataStoreBuilder(qualFile);
		hinter.accept(builder);
		
		try(QualityFastaDataStore sut = builder.build()){
			assertEquals(321, sut.getNumberOfRecords());
		}
	}
	
	@Test
	public void withRecordFilter() throws IOException, DataStoreException{
		QualityFastaFileDataStoreBuilder builder = new QualityFastaFileDataStoreBuilder(qualFile);
		hinter.accept(builder);
		builder.filterRecords(record-> record.getSequence().getAvgQuality() > 25);
		
		try(QualityFastaDataStore sut = builder.build();
				
			StreamingIterator<QualityFastaRecord> iter = sut.iterator();	
				){
			//some records are filtered out
			assertEquals(293, sut.getNumberOfRecords());
			while(iter.hasNext()){
				assertTrue(iter.next().getSequence().getAvgQuality() > 25);
			}
		}
	}
	
	
}
