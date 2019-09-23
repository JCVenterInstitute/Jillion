package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.fasta.FastaCollectors;
import org.jcvi.jillion.fasta.FastaRecord;
import org.jcvi.jillion.fasta.FastaWriter;
import org.jcvi.jillion.internal.core.io.BlockingAccumulator;
import org.jcvi.jillion.internal.core.util.Sneak;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Implementations of {@link Collector} that implement various useful reduction
 * operations, such as accumulating {@link FastqRecord}s into {@link FastqDataStore}s,
 * writing them to files and summarizing
 * elements according to various criteria, etc.
 *  *
 *  * <p>The following are examples of using the predefined collectors to perform
 *  * common mutable reduction tasks:
 *  <pre>{@code
 *   FastqSummaryStatistics stats = fastqs.parallelStream().collect(FastqCollectors.summarizing());
 *
 *  //collect to an in memory DataStore
 *  FastqDataStore datastore = fastqs.parallelStream().collect(FastqCollectors.toDataStore());
 *
 *  //write to the given file
 *  fastqs.parallelStream().collect(FastqCollectors.write(fastaFile));
 *
 *  //write to the given file
 *  fastqs.parallelStream().collect(FastqCollectors.writeAndClose(new FastqWriterBuilder(...).build());
 *
 * //stream through elements and perform operation to modify fastqrecord to write
 * fastqs.parallelStream().collect(FastqCollectors.write(fastaFile,
 *                         (w, record)-> w.write(record.trim(trimRange))));
 *
 *   //write records to given FastqWriter keeping writer open
 *   try(FastqWriter writer = ...){
 *      fastqs.parallelStream().collect(FastqCollectors.write(writer));
 *
 *      //writer still open
 *      writer.write(anotherRecord);
 *
 *   }//writer closed
 *
 *  }</pre>
 *
 * @since 5.3.2
 */
public final class FastqCollectors {

    private FastqCollectors(){
        //can not instantiate
    }

    /**
     * Write all the {@link FastqRecord}s in the Stream to a new File (overwriting if already exists)
     * using the default FastqWriter implementation.
     * @param fastqFileToWriteTo the file to write to.  Can not be {@code null},
     *                           if the file does not exist then the file and any parent directories
     *                           will be created.
     * @return a new Collector that will write the accumulated records to the fastq writer.
     * @throws IOException if there is a problem creating a writer to write to the given file.
     */
    public static  Collector<FastqRecord, ?, Void> write(File fastqFileToWriteTo) throws IOException{
        return writeAndClose(new FastqWriterBuilder(fastqFileToWriteTo).build());
    }

    /**
     * Write all the {@link FastqRecord}s in the Stream to a new File (overwriting if already exists)
     * using the default FastqWriter implementation.
     * @param fastqFileToWriteTo the file to write to.  Can not be {@code null},
     *                           if the file does not exist then the file and any parent directories
     *                           will be created.
     *
     * @param writeFunction the function to take the input record and (possiblly)
     *                     write it to the given writer.
     * @return a new Collector that will write the accumulated records to the fastq writer.
     * @throws IOException if there is a problem creating a writer to write to the given file.
     */
    public static <T, E extends Throwable> Collector<T, ?, Void> write(File fastqFileToWriteTo, ThrowingBiConsumer<FastqWriter, T, E> writeFunction) throws IOException, E{
        return writeAndClose(new FastqWriterBuilder(fastqFileToWriteTo).build(), writeFunction);
    }
    /**
     * Write all the {@link FastqRecord}s in the Stream to a the given {@link FastqWriter}
     * AND CLOSE the WRITER WHEN COMPLETE.
     * @param writer the {@link FastqWriter} to write to.  Can not be {@code null}.
     * @return a new Collector that will write the accumulated records to the fastq writer.
     */
    public static  Collector<FastqRecord, ?, Void> writeAndClose(FastqWriter writer){
        return Collector.of(() -> writer,
                (w, record) -> {
                    try{
                        w.write(record);
                    }catch(Throwable t){
                        Sneak.sneakyThrow(t);
                    }
                },
                (w1, w2) -> w1,
                w->{
                    {
                        try {
                            w.close();
                        } catch (IOException e) {
                           throw new UncheckedIOException(e);
                        }
                        return null;
                    }
                } ,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }

    /**
     * Write all the {@link FastqRecord}s in the Stream to a the given {@link FastqWriter}
     * AND CLOSE the WRITER WHEN COMPLETE.
     * @param writer the {@link FastqWriter} to write to.  Can not be {@code null}.
     * @param writeFunction the function to take the input record and (possiblly)
     *                     write it to the given writer.
     * @return a new Collector that will write the accumulated records to the fastq writer.
     */
    public static  <T, E extends Throwable> Collector<T, ?, Void> writeAndClose(FastqWriter writer, ThrowingBiConsumer<FastqWriter, T, E> writeFunction) throws E{
        Objects.requireNonNull(writeFunction);
        return Collector.of(() -> writer,
                (w, record) -> {
                    try{
                        writeFunction.accept(w, record);
                    }catch(Throwable t){
                        Sneak.sneakyThrow(t);
                    }
                },
                (w1, w2) -> w1,
                w->{
                    {
                        try {
                            w.close();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        return null;
                    }
                } ,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }

    /**
     * Write all the {@link FastqRecord}s in the Stream to a the given {@link FastqWriter}
     * but do not close the writer.  The writer must be closed by the owner, this allows further writes to be made
     * after the collector has finished.
     * @param writer the {@link FastqWriter} to write to.  Can not be {@code null}.
     * @return a new Collector that will write the accumulated records to the fastq writer.
     */
    public static  Collector<FastqRecord, ?, Void> write(FastqWriter writer){
        return Collector.of(() -> writer,
                (w, record) -> {
                    try{
                        w.write(record);
                    }catch(Throwable t){
                        Sneak.sneakyThrow(t);
                    }
                },
                (w1, w2) -> w1,
                w->null,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }


//    public static <W extends FastqWriter, E extends Throwable> Collector<FastqRecord, ?, Void> writeUsingBlockingQueue(W writer, int queueSize, ThrowingBiConsumer<W,FastqRecord, E> writerConsumer  ) throws E{
//        return new BlockingQueueFastqWriterCollector<>(writer,  writerConsumer, queueSize);
//    }
//
//    public static <W extends FastqWriter, E extends Throwable> Collector<FastqRecord, ?, Void> writeUsingBlockingQueue(W writer, int queueSize) {
//        return new BlockingQueueFastqWriterCollector<>(writer, (w, r)-> w.write(r), queueSize);
//    }
//    public static <W extends FastqWriter, E extends Throwable> Collector<FastqRecord, ?, Void> writeUsingBlockingQueue(W writer) {
//        return writeUsingBlockingQueue(writer, 100);
//    }


    public static Collector<FastqRecord, ?, FastqDataStore> toDataStore(){
        return Collectors.collectingAndThen(Collectors.toConcurrentMap(FastqRecord::getId, Function.identity()),
                                map-> DataStoreUtil.adapt(FastqDataStore.class, map));
    }
    public static Collector<FastqRecord, FastqSummaryStatistics, FastqSummaryStatistics> summarizing(){
        return Collector.of(FastqSummaryStatistics::new,
                FastqSummaryStatistics::accept,
                FastqSummaryStatistics::combine,
                Function.identity(),
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED
        );
    }
    private static class BlockingQueueFastqWriterCollector < W extends FastqWriter, E extends Throwable> implements Collector<FastqRecord, BlockingAccumulator, Void>{
        final BlockingQueue<Object> queue;

        Object POISON = new Object();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Future future;
        BiConsumer<W, FastqRecord> fastqFunction;
        BlockingQueueFastqWriterCollector(W writer, ThrowingBiConsumer<W, FastqRecord, E> fastqFunction, int queueSize){
            queue = new ArrayBlockingQueue<>(queueSize);
            future = singleThreadExecutor.submit(()->{
                try{
                    Object current;
                    while( (current = queue.take()) != POISON){
                        fastqFunction.accept(writer, (FastqRecord) current);
                    }

                }catch(Throwable e){
                    Sneak.sneakyThrow(e);

                }finally{
                    IOUtil.closeAndIgnoreErrors(writer);
                }
            });
            singleThreadExecutor.shutdown();
        }
        @Override
        public Supplier<BlockingAccumulator> supplier() {
            return ()-> new BlockingAccumulator(queue) ;
        }

        @Override
        public BiConsumer<BlockingAccumulator, FastqRecord> accumulator() {
            return (acc, t) ->{

                if(t !=null){
                    acc.put(t);
                }
            };
        }

        @Override
        public BinaryOperator<BlockingAccumulator> combiner() {
            return (a1, a2)-> a1;
        }

        @Override
        public Function<BlockingAccumulator, Void> finisher() {
            return (a)->{
                a.put(POISON);
                try {
                    future.get(); //will block
                } catch (Throwable t) {
                    Sneak.sneakyThrow(t);
                }
                return null;
            };
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Collector.Characteristics.CONCURRENT,

                    Collector.Characteristics.UNORDERED);
        }




    }

    /**
     * Computes statistics of various FastqRecord fields. Like its counterpart
     * XSummaryStatistics classes in the JDK.  This class is not threadsafe
     * but can be used in Collectors because of the way
     * the Collector partitions and combbines accumulators in different threads.
     *
     * @5.3.2
     */
    public static class FastqSummaryStatistics{
        private final LongSummaryStatistics qualStats =  new LongSummaryStatistics();
        private final LongSummaryStatistics lengthStats = new  LongSummaryStatistics();

        /**
         * Adds the information from the given FastqRecord to the running
         * stat counts.
         *
         * @param r the {@link FastqRecord} to include; can not be null.
         *
         * @throws NullPointerException if parameter is null.
         */
        public void accept(FastqRecord r){
            byte[] a = r.getQualitySequence().toArray();
            int len = a.length;
            for(int i=0; i<len; i++){
                qualStats.accept(a[i]);
            }
            lengthStats.accept(len);

        }

        /**
         * Combine another {@link FastqSummaryStatistics} object with this one.
         * @param other the other {@link FastqSummaryStatistics} to add; can not be null.
         * @return this
         *
         * @throws NullPointerException if parameter is null.
         */
        public FastqSummaryStatistics combine(FastqSummaryStatistics other){
            qualStats.combine(other.qualStats);
            lengthStats.combine(other.lengthStats);
            return this;
        }

        /**
         * Get the length of the shortest record accepted so far.
         * @return an OptionalInt wrapping the shortest value;
         * or an empty OptionalInt if no records have been accepted yet.
         */
        public OptionalInt getMinLength(){
            long min = lengthStats.getMin();
            if(min == Long.MAX_VALUE){
                return OptionalInt.empty();
            }
            return OptionalInt.of((int) min);
        }
        /**
         * Get the length of the longest record accepted so far.
         * @return an OptionalInt wrapping the longest value;
         * or an empty OptionalInt if no records have been accepted yet.
         */
        public OptionalInt getMaxLength(){
            long min = lengthStats.getMax();
            if(min == Long.MIN_VALUE){
                return OptionalInt.empty();
            }
            return OptionalInt.of((int) min);
        }

        /**
         * Get the total count of records accepted so far.
         * @return the count of records accepted which may be 0.
         */
        public long getCount(){
            return lengthStats.getCount();
        }
        /**
         * Get the avgerage length the records accepted so far.
         * @return an OptionalDouble wrapping the average value;
         * or an empty OptionalDouble if no records have been accepted yet.
         */
        public OptionalDouble getAvgLength(){
            if(lengthStats.getCount() ==0){
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(lengthStats.getAverage());

        }
        /**
         * Get the {@link PhredQuality} value that was the lowest quality
         * contained in all records accepted so far.
         * @return an Optional wrapping the lowest value;
         * or an empty Optional if no records have been accepted yet.
         */
        public Optional<PhredQuality> getMinQuality(){
            if(lengthStats.getCount() ==0){
                return Optional.empty();
            }
            return Optional.of(PhredQuality.valueOf((byte)qualStats.getMin()));
        }
        /**
         * Get the {@link PhredQuality} value that was the highest quality
         * contained in all records accepted so far.
         * @return an Optional wrapping the highest value;
         * or an empty Optional if no records have been accepted yet.
         */
        public Optional<PhredQuality> getMaxQuality(){
            if(lengthStats.getCount() ==0){
                return Optional.empty();
            }
            return Optional.of(PhredQuality.valueOf((byte)qualStats.getMax()));
        }
        /**
         * Get the avgerage quality value the records accepted so far.
         * @return an OptionalDouble wrapping the average value;
         * or an empty OptionalDouble if no records have been accepted yet.
         */
        public OptionalDouble getAvgQuality(){
            double avg=  qualStats.getAverage();
            if(avg == 0D){
                return OptionalDouble.empty();
            }
            return OptionalDouble.of(avg);
        }

    }
}
