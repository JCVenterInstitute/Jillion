package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public abstract class AbstractTestDownsampler {

	 @Rule
     public TemporaryFolder tmpDir = new TemporaryFolder();

     private static Map<String, FastqRecord> map;

     protected abstract FastqDownsampler createSut(int reserviorSize);

     @BeforeClass
     public static void createDataSet(){
     	 map = TestFastqDatasetUtil.createRandomFastqDataset(10_000, 100);
     }
     
     @Test
     public void lessThanResevorSizeShouldNotDownsample() throws IOException {
    	 File fastq = tmpDir.newFile();
    	 FastqQualityCodec codec = FastqQualityCodec.SANGER;
    	 try(FastqWriter writer = new FastqWriterBuilder(fastq)
                 .qualityCodec(codec)
                 .build();

		ThrowingStream<FastqRecord> stream = ThrowingStream.asThrowingStream(map.values().stream().limit(75));
		){
    		 stream.throwingForEach(writer::write);
    		 
    	 }
    	 
    	 File downsampledFastq = tmpDir.newFile();
    	 
    	 FastqDownsampler sut = createSut(1_000);
    	 try(FastqWriter writer = new FastqWriterBuilder(downsampledFastq).qualityCodec(codec).build()){
    			
 			sut.downsample(fastq, codec, writer);
     	 }
    	 
    	 Set<FastqRecord> expected = FastqFileReader.read(fastq).records().collect(Collectors.toSet());
    	 Set<FastqRecord> actual = FastqFileReader.read(downsampledFastq).records().collect(Collectors.toSet());
    	 
    	 assertEquals(expected, actual);
     }
     
     @Test
     public void downSampleLarge() throws IOException {
    	 File fastq = tmpDir.newFile();
    	 FastqQualityCodec codec = FastqQualityCodec.SANGER;
    	 FastqWriter.write(DataStoreUtil.adapt(FastqDataStore.class, map),fastq, codec);
    	 
    	 File downsampledFastq = tmpDir.newFile();
    	 
    	 FastqDownsampler sut = createSut(1_000);
    	 
    	 try(FastqWriter writer = new FastqWriterBuilder(downsampledFastq).qualityCodec(codec).build()){
		
			sut.downsample(fastq, codec, writer);
    	 }
    	 
    	 
    	 FastqFileDataStore actual = new FastqFileDataStoreBuilder(downsampledFastq).build();
    	 
    	 assertEquals(codec, actual.getQualityCodec());
    	 assertEquals(1_000, actual.getNumberOfRecords());
    	 Set<String> idsSeen = new HashSet<>();
    	 actual.forEach((id,record) -> {
    		 idsSeen.add(id);
    		 assertEquals(map.get(id), record);
    	 });
    	 
    	 assertEquals(1_000, idsSeen.size());
     }
     
     @Test
     public void downSampleHuge() throws IOException {
    	 File fastq = tmpDir.newFile();
    	 FastqQualityCodec codec = FastqQualityCodec.SANGER;
    	 Map<String, FastqRecord> map= TestFastqDatasetUtil.createRandomFastqDataset(1_000_000, 100);
    	 FastqWriter.write(DataStoreUtil.adapt(FastqDataStore.class, map),fastq, codec);
    	 
    	 File downsampledFastq = tmpDir.newFile();
    	 
    	 FastqDownsampler sut = createSut(50_000);
    	 
    	 try(FastqWriter writer = new FastqWriterBuilder(downsampledFastq).qualityCodec(codec).build()){
		
			sut.downsample(fastq, codec, writer);
    	 }
    	 
    	 
    	 FastqFileDataStore actual = new FastqFileDataStoreBuilder(downsampledFastq).build();
    	 
    	 assertEquals(codec, actual.getQualityCodec());
    	 assertEquals(50_000, actual.getNumberOfRecords());
    	 Set<String> idsSeen = new HashSet<>();
    	 actual.forEach((id,record) -> {
    		 idsSeen.add(id);
    		 assertEquals(map.get(id), record);
    	 });
    	 
    	 assertEquals(50_000, idsSeen.size());
     }
     
     @Test
     public void downSamplingTwiceProducesDifferentResults() throws IOException {
    	 File fastq = tmpDir.newFile();
    	 FastqQualityCodec codec = FastqQualityCodec.SANGER;
    	 FastqWriter.write(DataStoreUtil.adapt(FastqDataStore.class, map),fastq, codec);
    	 
    	 File downsampledFastq = tmpDir.newFile();
    	 File downsampledFastq2 = tmpDir.newFile();
    	 FastqDownsampler sut = createSut(1_000);
    	 
    	 try(FastqWriter writer = new FastqWriterBuilder(downsampledFastq).qualityCodec(codec).build();
    			 FastqWriter writer2 = new FastqWriterBuilder(downsampledFastq2).qualityCodec(codec).build()){
		
			sut.downsample(fastq, codec, writer);
			sut.downsample(fastq, codec, writer2);
    	 }
    	 
    	 
    	 Set<FastqRecord> set1 = FastqFileReader.read(downsampledFastq, codec).records().collect(Collectors.toSet());
    	 Set<FastqRecord> set2 = FastqFileReader.read(downsampledFastq2, codec).records().collect(Collectors.toSet());
    	 
    	
    	 
    	 assertEquals(1_000, set1.size());
    	 assertEquals(1_000, set2.size());
    	 
    	 assertNotEquals(set1, set2);
     }
}
