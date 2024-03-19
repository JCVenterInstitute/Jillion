package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.testUtil.SlowTests;
import org.jcvi.jillion.core.util.MapUtil;

import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@Category(SlowTests.class)
public class TestFastqCollector {

        @Rule
        public TemporaryFolder tmpDir = new TemporaryFolder();

        private static Map<String, FastqRecord> map;

        

        @BeforeClass
        public static void createDataSet(){
        	 map = TestFastqDatasetUtil.createRandomFastqDataset(2_000, 100);
        }

		

    private <T> T fakeCPUIntensiveOp(T in){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return in;
    }
    private FastqDataStore assertFileWrittenContainsAllRecordsInMap(File fastqFile) throws IOException {
        return assertFileWrittenContainsAllRecordsInMap(fastqFile, 0);
    }

    private FastqDataStore assertFileWrittenContainsAllRecordsInMap(Map<String, FastqRecord> expectedMap, File fastqFile, int numExtra) throws IOException{
    	 FastqDataStore datastore = FastqFileDataStore.fromFile(fastqFile);

         assertEquals(expectedMap.size() + numExtra, datastore.getNumberOfRecords());
         for(FastqRecord f : expectedMap.values()){
             FastqRecord actual = datastore.get(f.getId());
//             System.out.println(f);
//             System.out.println(actual);
             assertEquals(f, actual);
         }
         return datastore;
    }
    private FastqDataStore assertFileWrittenContainsAllRecordsInMap(File fastqFile, int numExtra) throws IOException{
       return assertFileWrittenContainsAllRecordsInMap(map, fastqFile, numExtra);
    }

        @Test
        public void writeSerially() throws IOException {

            File fastqFile = tmpDir.newFile();

            try(FastqWriter writer = new FastqWriterBuilder(fastqFile)
                    .build()){
                for(FastqRecord f: map.values()){
                    writer.write(fakeCPUIntensiveOp(f));
                }
            }

            assertFileWrittenContainsAllRecordsInMap(fastqFile);
        }

        @Test
        public void summary(){
            FastqCollectors.FastqSummaryStatistics stats = map.values().parallelStream().collect(FastqCollectors.summarizing());
            assertEquals(100, stats.getAvgLength().getAsDouble(), 0D);
            assertEquals(100, stats.getMinLength().getAsInt());

            assertEquals(100, stats.getMaxLength().getAsInt());

            assertEquals(map.size(), stats.getCount());

            IntSummaryStatistics expectedStats = map.values().stream().flatMap(r-> StreamSupport.stream(r.getQualitySequence().spliterator(), false))
                                .collect(Collectors.summarizingInt(PhredQuality::getQualityScore));

            assertEquals(expectedStats.getAverage(), stats.getAvgQuality().getAsDouble(), 0D);
            assertEquals(expectedStats.getMin(), stats.getMinQuality().get().getQualityScore());
            assertEquals(expectedStats.getMax(), stats.getMaxQuality().get().getQualityScore());
        }
        @Test
        public void collectToDataStore() throws IOException{
            FastqDataStore datastore = map.values().parallelStream().collect(FastqCollectors.toDataStore());

            File fastqFile = tmpDir.newFile();
            datastore.records()
                    .peek(f-> {
                        assertEquals(100, f.getQualitySequence().getLength());
                        assertEquals(100, f.getNucleotideSequence().getLength());

                    }).collect(FastqCollectors.writeAndClose(new FastqWriterBuilder(fastqFile)
                    .build()));

            assertFileWrittenContainsAllRecordsInMap(fastqFile);
        }

    @Test
    public void writeSeriallyWithoutDelay() throws IOException {

        File fastqFile = tmpDir.newFile();

        try(FastqWriter writer = new FastqWriterBuilder(fastqFile)
                .build()){
            for(FastqRecord f: map.values()){
                writer.write(f);
            }
        }

        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }
        @Test
        public void writeCollectionWithoutDelay() throws IOException {

            File fastqFile = tmpDir.newFile();

            try(FastqWriter writer = new FastqWriterBuilder(fastqFile)
                    .build()){
                writer.write(map.values());
            }

            assertFileWrittenContainsAllRecordsInMap(fastqFile);
        }
        @Test
        public void writeSmallerCollectionWithoutDelay() throws IOException {

            File fastqFile = tmpDir.newFile();

            //map has 2000 records test with smaller not-round number
            Map<String, FastqRecord> smallerMap = map.entrySet().stream()
            										.limit(1337)
            										.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
           
            try(FastqWriter writer = new FastqWriterBuilder(fastqFile)
                    .build()){
                writer.write(smallerMap.values());
            }

            assertFileWrittenContainsAllRecordsInMap(smallerMap, fastqFile, 0);
        }

        @Test
        public void executorService() throws Throwable {

            File fastqFile = tmpDir.newFile();

            ExecutorService service = Executors.newFixedThreadPool(5);
            try(FastqWriter writer = new FastqWriterBuilder(fastqFile)
                    .build()){
                for(FastqRecord r : map.values()){
                    service.submit(()-> write(r, writer));
                }
                service.shutdown();
                service.awaitTermination(1, TimeUnit.HOURS);

            }

            assertFileWrittenContainsAllRecordsInMap(fastqFile);
        }

    @Test
    public void writeAndKeepOpen() throws Throwable {

        File fastqFile = tmpDir.newFile();

        FastqRecord extra =  FastqRecordBuilder.create("extra",
                                                        NucleotideSequenceTestUtil.createRandom(200),
                                                        TestFastqDatasetUtil.createRandomQualitySequence(200))
                                                        .build();
        try(FastqWriter writer = new FastqWriterBuilder(fastqFile).build()) {
            map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                    .collect(FastqCollectors.write(writer));

            writer.write(extra);
        }



        FastqDataStore datastore = assertFileWrittenContainsAllRecordsInMap(fastqFile, 1);
        assertEquals(extra, datastore.get(extra.getId()));
    }

    @Test
    public void writeFile() throws Throwable {

        File fastqFile = tmpDir.newFile();


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.write(fastqFile));



        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }
    @Test
    public void writeAndClose() throws Throwable {

        File fastqFile = tmpDir.newFile();


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.writeAndClose(new FastqWriterBuilder(fastqFile).build()));



        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }
    @Test
    public void writeWithFunction() throws Throwable {

        File fastqFile = tmpDir.newFile();

        Range trimRange = Range.of(25,75);


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.write(fastqFile,
                        (w, record)-> w.write(record.trim(trimRange))));



        assertFileWrittenAndTrimmedCorrectly(fastqFile, trimRange);
    }

    private void write(FastqRecord r, FastqWriter writer){
        try {
            writer.write(fakeCPUIntensiveOp(r));
        }catch(Throwable e){
            Sneak.sneakyThrow(e);
        };
    }

    //FIXME removing blocking queue implementations don't seem to make any improvment and complicates API
/*
    @Test
    public void blockingQueueDefault() throws Throwable {

        File fastqFile = tmpDir.newFile();


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.writeUsingBlockingQueue(new FastqWriterBuilder(fastqFile)
                        .build()));



        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }


    @Test
    public void blockingQueueLarge() throws Throwable {

        File fastqFile = tmpDir.newFile();


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.writeUsingBlockingQueue(new FastqWriterBuilder(fastqFile)
                                .build(),
                        1_000));



        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }
    @Test
    public void blockingQueueSmall() throws Throwable {

        File fastqFile = tmpDir.newFile();


        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastqCollectors.writeUsingBlockingQueue(new FastqWriterBuilder(fastqFile)
                                .build(),
                        5));



        assertFileWrittenContainsAllRecordsInMap(fastqFile);
    }

 */

    private void assertFileWrittenAndTrimmedCorrectly(File fastqFile, Range trimRange) throws IOException{
        FastqDataStore datastore = FastqFileDataStore.fromFile(fastqFile);

        assertEquals(map.size(), datastore.getNumberOfRecords());
        for(FastqRecord f : map.values()){
            FastqRecord actual = datastore.get(f.getId());
//            System.out.println(f);
//            System.out.println(actual);
            FastqRecord expected = f.trim(trimRange);
            assertEquals(expected, actual);
        }
    }
}
