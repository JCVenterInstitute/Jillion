package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestSplitFastqRoundRobin {

	private static FastqFileDataStore datastore;
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestSplitFastqRoundRobin.class);
		
		datastore = new FastqFileDataStoreBuilder(resources.getFile("files/giv_XX_15050.fastq"))
						.build();
	}
	
	@AfterClass
	public static void closeDataStore() throws IOException{
		datastore.close();
		datastore =null;
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void zeroNumberOfFilesShouldThrowIllegalArgumentException(){
		SplitFastqWriter.roundRobin(0, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), (char)(i+'@') + ".fastq"))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeNumberOfFilesShouldThrowIllegalArgumentException(){
		SplitFastqWriter.roundRobin(-1, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), (char)(i+'@') + ".fastq"))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	@Test
	public void splitInto1FileShouldMakeIdenticalOutputFile() throws IOException, DataStoreException{
		assertRoundRobinWorksFor(1);
	}

	@Test
	public void splitInto2Files() throws IOException, DataStoreException{
		assertRoundRobinWorksFor(2);
	}
	@Test
	public void splitInto10Files() throws IOException, DataStoreException{
		assertRoundRobinWorksFor(10);
	}


	private void assertRoundRobinWorksFor(int numFiles) throws IOException,
			DataStoreException {
		try(FastqWriter writer = createWriter(numFiles);
				
			StreamingIterator<FastqRecord> iter = datastore.iterator();
				
				){
			while(iter.hasNext()){
				writer.write(iter.next());
			}
		}
		
		List<File> actualFiles = getOutputFiles();
		assertEquals(JoinedStringBuilder.create(actualFiles)
								.transform(File::getName)
								.glue(",")
								.build(), 
								
								numFiles, actualFiles.size());
		
		outputMatchesExpected(actualFiles);
	}
	
	private FastqWriter createWriter(int numberOfFiles){
		//since i starts at 1 1 + '@' is A so files named A.fastq, B.fastq etc
		//which makes sorting easy
		return SplitFastqWriter.roundRobin(numberOfFiles, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), (char)(i+'@') + ".fastq"))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	
	
	
	private void outputMatchesExpected(List<File> sortedFiles) throws IOException, DataStoreException{
		int numFiles = sortedFiles.size();
		List<StreamingIterator<FastqRecord>> actualIters = new ArrayList<>(numFiles);
		
		try{
			for(File f : sortedFiles){
				actualIters.add( new FastqFileDataStoreBuilder(f)
													.qualityCodec(datastore.getQualityCodec())
													.hint(DataStoreProviderHint.ITERATION_ONLY)
													.build()
													.iterator());
			}
			int i=0;
			try(StreamingIterator<FastqRecord> expectedIter = datastore.iterator()){
				while(expectedIter.hasNext()){
					StreamingIterator<FastqRecord> actualIter = actualIters.get(i);
					assertTrue(actualIter.hasNext());
					assertEquals(expectedIter.next(), actualIter.next());
					
					i++;
					i %= numFiles;
				}
			}
			for(StreamingIterator<FastqRecord> iter : actualIters){
				assertFalse(iter.hasNext());
			}
			
		}finally{
			for(StreamingIterator<FastqRecord> iter: actualIters){
				IOUtil.closeAndIgnoreErrors(iter);
			}
		}
	}
	
	private List<File> getOutputFiles(){
		ArrayList<File> list = new ArrayList<File>();
		for(File f : tmpDir.getRoot().listFiles()){
			list.add(f);
		}
		Collections.sort(list, (a,b)-> a.getName().compareTo(b.getName()));
		return list;
	}
}
