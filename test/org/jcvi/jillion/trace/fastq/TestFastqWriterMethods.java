package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.OutputStreams;
import org.jcvi.jillion.core.util.JillionCollectors;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
public class TestFastqWriterMethods {

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	private final ResourceHelper helper = new ResourceHelper(this);
	
	File in, out;
	@Before
	public void setup() throws IOException{
		in = helper.getFile("files/giv_XX_15050.fastq");
		out = tmpDir.newFile();
	}
	@Test
	public void adapt() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		Range trimRange = Range.of(20,100);
		
		File manualTrim = tmpDir.newFile();
		File actual = tmpDir.newFile();
		
		try(FastqWriter w1 = new FastqWriterBuilder(manualTrim).build();
			FastqWriter w2 = FastqWriter.adapt(new FastqWriterBuilder(actual).build(),
					                           fastq -> fastq.toBuilder().trim(trimRange).build())){
			
			FastqFileReader.forEach(in, (id, fastq)->{
				w1.write(fastq, trimRange);
				w2.write(fastq);
			});
		}
				
		assertSameRecords(manualTrim, actual);
	}
	
	@Test
	public void adaptReturnsNullWillSkipRecord() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
File actual = tmpDir.newFile();
		
		try(FastqWriter w1 = FastqWriter.adapt(new FastqWriterBuilder(actual).build(),
					                           fastq ->  null)){
			FastqFileReader.forEach(in, (id, fastq)-> w1.write(fastq));
		}
			
		assertEquals(0, FastqFileDataStore.fromFile(actual, FastqQualityCodec.SOLEXA).getNumberOfRecords());
		
	}
	
	@Test
	public void writeToFile() throws IOException{
		File out = tmpDir.newFile();
		
		try(FastqFileDataStore datastore = FastqFileDataStore.fromFile(in)){
			FastqWriter.write(datastore, out);
		}
		
		assertSameRecords(in, out);
	}
	
	@Test
	public void copyToOutputStreamPredicate() throws IOException{
		Predicate<String> filter = id -> id.contains("T07");
		FastqWriter.copyById(FastqFileParser.create(in), OutputStreams.buffered(out), filter);
	
		FastqFileDataStore expected = new FastqFileDataStoreBuilder(in)
												.filter(filter)
												.build();
		assertSameRecords(expected, FastqFileDataStore.fromFile(out));
	}
	
	@Test
	public void copyToFilePredicate() throws IOException{
		Predicate<String> stringFilter = id -> id.contains("T07");
		Predicate<FastqRecord> recordFilter = fastq -> stringFilter.test(fastq.getId());
		
		FastqWriter.copy(FastqFileParser.create(in), out, recordFilter);
	
		FastqFileDataStore expected = new FastqFileDataStoreBuilder(in)
												.filter(stringFilter)
												.build();
		assertSameRecords(expected, FastqFileDataStore.fromFile(out));
	}
	
	@Test
	public void copyToFileCodecPredicate() throws IOException{
		Predicate<String> stringFilter = id -> id.contains("T07");
		Predicate<FastqRecord> recordFilter = fastq -> stringFilter.test(fastq.getId());
		
		FastqWriter.copy(FastqFileParser.create(in), FastqQualityCodec.SANGER, out, recordFilter);
	
		FastqFileDataStore expected = new FastqFileDataStoreBuilder(in)
												.filter(stringFilter)
												.build();
		assertSameRecords(expected, FastqFileDataStore.fromFile(out));
		assertEquals(FastqQualityCodec.SANGER, FastqUtil.guessQualityCodecUsed(out));
	}
	
	@Test
	public void copyToFile() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		FastqWriter.copy(FastqFileParser.create(in), out);
		
		assertSameRecords(in, out);
	}
	
	@Test
	public void copyToOutputStream() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		File out = tmpDir.newFile();
		FastqWriter.copy(FastqFileParser.create(in), OutputStreams.buffered(out));
		
		assertSameRecords(in, out);
	}
	
	@Test
	public void copyToFileGivenCodec() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		File out = tmpDir.newFile();
		FastqWriter.copy(FastqFileParser.create(in), FastqQualityCodec.SANGER, out);
		
		assertSameRecords(in, out);
	}
	
	@Test
	public void copyToOutputStreamGivenCodec() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		File out = tmpDir.newFile();
		FastqWriter.copy(FastqFileParser.create(in), FastqQualityCodec.SANGER, OutputStreams.buffered(out));
		
		assertSameRecords(in, out);
	}
	
	@Test
	public void copyToFileWithCodecAndPredicate() throws IOException{
		File in = helper.getFile("files/giv_XX_15050.fastq");
		File out = tmpDir.newFile();
		
		FastqParser parser = new FastqFileParserBuilder(in)
									.hasComments(true)
									.build();
		FastqWriter.copy(parser, FastqQualityCodec.SANGER,
				OutputStreams.buffered(out),
				record -> record.getId().endsWith("F")
				);
		
		FastqFileDataStore expected = FastqFileReader.read(parser)
										.records()
										.filter(record -> record.getId().endsWith("F"))
										.collect(JillionCollectors.toDataStore(FastqFileDataStore.class, FastqRecord::getId));
		
		assertSameRecords(expected, FastqFileDataStore.from(new FastqFileParserBuilder(out)
									.hasComments(true)
									.build()));
	}
	
	private void assertSameRecords(File expected, File actual) throws IOException{
		assertSameRecords(FastqFileDataStore.fromFile(expected),
				FastqFileDataStore.fromFile(actual));
		
	}
	
	private void assertSameRecords(FastqFileDataStore expected, FastqFileDataStore actual) throws IOException{
		assertEquals(expected.getNumberOfRecords(), actual.getNumberOfRecords());
		try(StreamingIterator<FastqRecord> eIter = expected.iterator();
			StreamingIterator<FastqRecord> aIter = actual.iterator();
			){
			while(eIter.hasNext()){
				assertEquals(eIter.next(), aIter.next());
			}
		}
	}
}
