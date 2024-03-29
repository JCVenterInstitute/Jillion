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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestSplitFastqRollover {

	private static FastqFileDataStore datastore;
	
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestSplitFastqRollover.class);
		
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
		SplitFastqWriter.rollover(0, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), String.format("%04d.fastq", i)))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeNumberOfFilesShouldThrowIllegalArgumentException(){
		SplitFastqWriter.rollover(-1, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), String.format("%04d.fastq", i)))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	@Test
	public void splitAllPerFileShouldMakeIdenticalOutputFile() throws IOException, DataStoreException{
		assertRolloverWorksFor(datastore.getNumberOfRecords());
	}

	@Test
	public void splitInto2Files() throws IOException, DataStoreException{
		//there are 282 reads in file
		assertRolloverWorksFor(200);
	}
	@Test
	public void splitInto10Files() throws IOException, DataStoreException{
		assertRolloverWorksFor(30);
	}
	@Test
        public void multiThreadedWrite() throws IOException, DataStoreException{
                assertMultiThreadedRolloverWorksFor(30);
        }

	private void assertRolloverWorksFor(long readsPerFile) throws IOException,
			DataStoreException {
		try(FastqWriter writer = createWriter((int)readsPerFile);
				
			StreamingIterator<FastqRecord> iter = datastore.iterator();
				
				){
			while(iter.hasNext()){
				writer.write(iter.next());
			}
		}
		
		List<File> actualFiles = getOutputFiles();
		
		double tmp = datastore.getNumberOfRecords()/ (double)readsPerFile;
		int remainder = datastore.getNumberOfRecords() % readsPerFile ==0?0 :1;
		int expectedNumberPerFile = (int)(tmp + remainder);
		assertEquals(JoinedStringBuilder.create(actualFiles)
								.transform(File::getName)
								.glue(",")
								.build(), 
								
								expectedNumberPerFile, actualFiles.size());
		
		outputMatchesExpected(actualFiles);
	}
	
	private void assertMultiThreadedRolloverWorksFor(long readsPerFile) throws IOException{
	    try(FastqWriter writer = createWriter((int)readsPerFile);
	        ThrowingStream<FastqRecord> stream = datastore.records().parallel();
	    ){
	        stream.throwingForEach(fastq -> writer.write(fastq));
	    }


	    List<File> actualFiles = getOutputFiles();

	    double tmp = datastore.getNumberOfRecords()/ (double)readsPerFile;
	    int remainder = datastore.getNumberOfRecords() % readsPerFile ==0?0 :1;
	    int expectedNumberPerFile = (int)(tmp + remainder);
	    assertEquals(JoinedStringBuilder.create(actualFiles)
	            .transform(File::getName)
	            .glue(",")
	            .build(), 

	            expectedNumberPerFile, actualFiles.size());

	    multiThreadOutputMatchesExpected(actualFiles);
	}
	
	private FastqWriter createWriter(int numberOfFiles){		
		//which makes sorting easy
		return SplitFastqWriter.rollover(numberOfFiles, 
				i-> new FastqWriterBuilder(new File(tmpDir.getRoot(), String.format("%04d.fastq", i)))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	
	private void multiThreadOutputMatchesExpected(List<File> sortedFiles) throws IOException{
	    Set<String> expectedIds = datastore.ids().collect(Collectors.toSet());
	    
	    for(File f : sortedFiles){
	    	FastqFileReader.forEach(f, (id, fastq)-> assertTrue(id, expectedIds.remove(fastq.getId())));

	    }
	    
	    assertEquals(Collections.emptySet(), expectedIds);
	}
	
	private void outputMatchesExpected(List<File> sortedFiles) throws IOException{
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
			
			try(StreamingIterator<FastqRecord> actualIter = IteratorUtil.createChainedStreamingIterator(actualIters);
					
					StreamingIterator<FastqRecord> expectedIter = datastore.iterator()){
				while(expectedIter.hasNext()){
					
					assertTrue(actualIter.hasNext());
					assertEquals(expectedIter.next(), actualIter.next());
					
				}
				assertFalse(actualIter.hasNext());
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
