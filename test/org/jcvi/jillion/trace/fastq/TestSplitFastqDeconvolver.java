package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
public class TestSplitFastqDeconvolver {

	private static FastqFileDataStore datastore;
	
	private static Pattern ELVIRA_SEQNAME_PATTERN = Pattern.compile("^.{4}(.)(\\d{2})([A-Z]\\d{2})([A-Z])(\\d{2})(\\S+?)([F|R])[M1-9]?([A-Z]?)$");
    
	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@BeforeClass
	public static void setup() throws IOException{
		ResourceHelper resources = new ResourceHelper(TestSplitFastqDeconvolver.class);
		
		datastore = new FastqFileDataStoreBuilder(resources.getFile("files/giv_XX_15050.fastq"))
						.build();
	}
	
	private static String getDirection(FastqRecord record){
		Matcher matcher = ELVIRA_SEQNAME_PATTERN.matcher(record.getId());
		if(matcher.find()){
			return matcher.group(7);
		}
		return null;
	}
	
	private static String getSegment(FastqRecord record){
		Matcher matcher = ELVIRA_SEQNAME_PATTERN.matcher(record.getId());
		if(matcher.find()){
			return matcher.group(2);
		}
		return null;
	}
	
	@AfterClass
	public static void closeDataStore() throws IOException{
		datastore.close();
		datastore =null;
	}
	
	@Test
	public void writingNoRecordsShouldNotCreateAnyFiles() throws IOException{
		FastqWriter writer = createWriter(TestSplitFastqDeconvolver::getDirection);
		writer.close();
		assertTrue(getOutputFiles().isEmpty());
	}
	
	@Test
	public void callingCloseMultipleTimesIsOK() throws IOException{
		FastqWriter writer = createWriter(TestSplitFastqDeconvolver::getDirection);
		writer.close();
		writer.close();
	}
	
	@Test
	public void writingRecordAfterClosingShouldThrowException() throws IOException{
		FastqWriter writer = createWriter(TestSplitFastqDeconvolver::getDirection);
		writer.close();
		
		expectedException.expect(IOException.class);
		expectedException.expectMessage("closed");
		
		writer.write("foo", NucleotideSequenceTestUtil.create("ACGT"), new QualitySequenceBuilder(new byte[]{20,20,20,20}).build());
	}
	
	@Test
	public void splitAllPerFileShouldMakeIdenticalOutputFile() throws IOException, DataStoreException{
		assertRolloverWorksFor(record-> "keep");
	}

	@Test
	public void splitbyDirectionFiles() throws IOException, DataStoreException{
		assertRolloverWorksFor(TestSplitFastqDeconvolver::getDirection);
	}
	@Test
	public void splitBySegmentFiles() throws IOException, DataStoreException{
		assertRolloverWorksFor(TestSplitFastqDeconvolver::getSegment);
	}


	private  <K> void assertRolloverWorksFor(Function<FastqRecord, K> deconvolver) throws IOException,
			DataStoreException {
		Map<String, List<FastqRecord>> deconvolveMap = new HashMap<>();
		
		try(FastqWriter writer = createWriter(deconvolver);
				
			StreamingIterator<FastqRecord> iter = datastore.iterator();
				
				){
			while(iter.hasNext()){
				FastqRecord next = iter.next();
				writer.write(next);
				K key = deconvolver.apply(next);
				
				String fileName = key +".fastq";
				deconvolveMap.computeIfAbsent(fileName, k-> new ArrayList<>())
									.add(next);
			}
		}
		
		List<File> actualFiles = getOutputFiles();
		assertEquals(deconvolveMap.keySet().stream().collect(Collectors.toCollection(()-> new TreeSet<>())),
				actualFiles.stream().map(File::getName).collect(Collectors.toCollection(()-> new TreeSet<>()))
				);
		
	
		
		outputMatchesExpected(deconvolveMap, actualFiles);
	}
	
	private <K> FastqWriter createWriter(Function<FastqRecord, K> deconvolverFunction){		
		//which makes sorting easy
		return SplitFastqWriter.deconvolve(
				deconvolverFunction,
				key-> new FastqWriterBuilder(new File(tmpDir.getRoot(), key +".fastq"))
									.qualityCodec(datastore.getQualityCodec())
									.build());
	}
	
	
	
	
	private void outputMatchesExpected(Map<String, List<FastqRecord>> deconvolveMap, List<File> sortedFiles) throws IOException, DataStoreException{
		for(File actualFile : sortedFiles){
			try(FastqFileDataStore actual = new FastqFileDataStoreBuilder(actualFile).qualityCodec(datastore.getQualityCodec()).build();
					
					StreamingIterator<FastqRecord> actualIter = actual.iterator();
					){
				List<FastqRecord> expected = deconvolveMap.get(actualFile.getName());
				assertEquals(expected.size(), actual.getNumberOfRecords());
				
				try(StreamingIterator<FastqRecord> expectedIter = IteratorUtil.createStreamingIterator(expected.iterator())){
					while(expectedIter.hasNext()){
						assertTrue(actualIter.hasNext());
						assertEquals(expectedIter.next(),actualIter.next());
					}
					assertFalse(actualIter.hasNext());
				}
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
