package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.testUtil.SlowTests;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.fasta.FastaCollectors;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.testutils.ProteinSequenceTestUtil;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
@Category(SlowTests.class)
public class TestMultithreadProteinFastaWriter {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private static Map<String, ProteinFastaRecord> map;


    @BeforeClass
    public static void createDataSet(){
        map = new ConcurrentHashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(100_000));
        for(int i=0; i<2_000; i++){
            String id = "seq_" +i;
            map.put(id, new ProteinFastaRecordBuilder(id, ProteinSequenceTestUtil.randomSequence(100)).build());
        }
    }

    @Test
    public void writeSerially() throws IOException {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                                                .build()){
           for(ProteinFastaRecord f: map.values()){
               writer.write(fakeCPUIntensiveOp(f));
           }
        }

        assertFileWrittenCorrectly(fastaFile);
    }
    @Test
    public void writeCollection() throws IOException {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                .build()){
            writer.write(map.values());
        }

        assertFileWrittenCorrectly(fastaFile);
    }

    @Test
    public void executorService() throws Throwable {

        File fastaFile = tmpDir.newFile();

        ExecutorService service = Executors.newFixedThreadPool(5);
        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                .build()){
            for(ProteinFastaRecord r : map.values()){
                service.submit(()-> write(r, writer));
            }
            service.shutdown();
            service.awaitTermination(1, TimeUnit.HOURS);

        }

        assertFileWrittenCorrectly(fastaFile);
    }

    private void write(ProteinFastaRecord r, ProteinFastaWriter writer){
        try {
            writer.write(fakeCPUIntensiveOp(r));
        }catch(Throwable e){
            Sneak.sneakyThrow(e);
        };
    }

    @Test
    public void multiThreadedCollector() throws Throwable {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                .build()){
            map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                    .collect(FastaCollectors.write(writer));

        }

        assertFileWrittenCorrectly(fastaFile);
    }

    private <T> T fakeCPUIntensiveOp(T in){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return in;
    }

    @Test
    public void writeTrimmed() throws Throwable {

        File fastaFile = tmpDir.newFile();


        Range trimRange = Range.of(25,75);
        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
                .collect(FastaCollectors.writeAndClose(new ProteinFastaWriterBuilder(fastaFile).build(),

                        (ThrowingBiConsumer<ProteinFastaWriter, ProteinFastaRecord, Throwable>) (w,r)-> w.write(r.trim(trimRange))
                        ));



        assertFileWrittenAndTrimmedCorrectly(fastaFile, trimRange);
    }

//    @Test
//    public void blockingQueueDefault() throws Throwable {
//
//        File fastaFile = tmpDir.newFile();
//
//
//            map.values().parallelStream().map(this::fakeCPUIntensiveOp)
//                    .collect(FastaCollectors.writeUsingBlockingQueue(new ProteinFastaWriterBuilder(fastaFile)
//                            .build()));
//
//
//
//        assertFileWrittenCorrectly(fastaFile);
//    }
//    @Test
//    public void blockingQueueLarge() throws Throwable {
//
//        File fastaFile = tmpDir.newFile();
//
//
//        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
//                .collect(FastaCollectors.writeUsingBlockingQueue(new ProteinFastaWriterBuilder(fastaFile)
//                        .build(),
//                        1_000));
//
//
//
//        assertFileWrittenCorrectly(fastaFile);
//    }
//    @Test
//    public void blockingQueueSmall() throws Throwable {
//
//        File fastaFile = tmpDir.newFile();
//
//
//        map.values().parallelStream().map(this::fakeCPUIntensiveOp)
//                .collect(FastaCollectors.writeUsingBlockingQueue(new ProteinFastaWriterBuilder(fastaFile)
//                                .build(),
//                        5));
//
//
//
//        assertFileWrittenCorrectly(fastaFile);
//    }

    @Test
    public void toDataStore() throws IOException{
        File fastaFile = tmpDir.newFile();

        ProteinFastaDataStore datastore =  map.values()
                                                .parallelStream()
                                        .collect(FastaCollectors.toDataStore(ProteinFastaDataStore.class));

        datastore.records().collect(FastaCollectors.writeAndClose(new ProteinFastaWriterBuilder(fastaFile)
                .build()));

        assertFileWrittenCorrectly(fastaFile);
    }


//    @Test
//    public void multiThreadedCollectorUnordered() throws Throwable {
//
//        File fastaFile = tmpDir.newFile();
//
//        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
//                .build()){
//            map.values().parallelStream().map(this::fakeCPUIntensiveOp)
//                    .collect(FastaCollectors.writeUnordered(writer));
//
//        }
//
//        assertFileWrittenCorrectly(fastaFile);
//    }



    private void assertFileWrittenCorrectly(File fastaFile) throws IOException{
        ProteinFastaDataStore datastore = ProteinFastaDataStore.fromFile(fastaFile);

        assertEquals(map.size(), datastore.getNumberOfRecords());
        for(ProteinFastaRecord f : map.values()){
            ProteinFastaRecord actual = datastore.get(f.getId());
//            System.out.println(f);
//            System.out.println(actual);
            assertEquals(f, actual);
        }
    }

    private void assertFileWrittenAndTrimmedCorrectly(File fastaFile, Range trimRange) throws IOException{
        ProteinFastaDataStore datastore = ProteinFastaDataStore.fromFile(fastaFile);

        assertEquals(map.size(), datastore.getNumberOfRecords());
        for(ProteinFastaRecord f : map.values()){
            ProteinFastaRecord actual = datastore.get(f.getId());
//            System.out.println(f);
//            System.out.println(actual);
            ProteinFastaRecord expected = new ProteinFastaRecordBuilder(f.getId(), f.getSequence().trim(trimRange))
                        .comment(f.getComment())
                        .build();
            assertEquals(expected, actual);
        }
    }



}
