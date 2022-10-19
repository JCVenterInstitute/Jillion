package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.internal.core.io.BlockingAccumulator;
import org.jcvi.jillion.internal.core.util.Sneak;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
/**
 * Implementations of {@link Collector} that implement various useful reduction
 * operations, such as accumulating {@link FastaRecord}s into {@link FastaDataStore}s,
 * writing them to files etc.
 *  *
 *  * <p>The following are examples of using the predefined collectors to perform
 *  * common mutable reduction tasks:
 *  <pre>{@code
 *
 *  //collect to an in memory DataStore
 *  ProteinFastaDataStore datastore = fastas.parallelStream().collect(FastaCollectors.toDataStore(ProteinFastaDataStore.class));
 *
 *
 *  //write to the given file
 *  fastas.parallelStream().collect(FastaCollectors.writeAndClose(new ProteinFastaWriterBuilder(...).build());
 *
 *   //write records to given FastaWriter keeping writer open
 *   try(FastaWriter writer = ...){
 *      fastas.parallelStream().collect(FastaCollectors.write(writer));
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
public class FastaCollectors {


    private FastaCollectors(){
        //can not instantiate
    }
    /**
     * Write all the {@link FastaRecord}s in the Stream to a the given {@link FastaWriter}
     * AND CLOSE the WRITER WHEN COMPLETE.
     * @param writer the {@link FastaWriter} to write to.  Can not be {@code null}.
     * @return a new Collector that will write the accumulated records to the fasta writer.
     */
    public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>> Collector<F, ?, Void> writeAndClose(FastaWriter<R, S,F> writer){
        Objects.requireNonNull(writer);
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
     * Write all the {@link FastaRecord}s in the Stream to a the given {@link FastaWriter}
     * but do not close the writer.  The writer must be closed by the owner, this allows further writes to be made
     * after the collector has finished.
     * @param writer the {@link FastaWriter} to write to.  Can not be {@code null}.
     * @return a new Collector that will write the accumulated records to the fasta writer.
     */
    public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>> Collector<F, ?, Void> write(FastaWriter<R, S,F> writer){
        return Collector.of(() -> writer,
                (w, record) -> {
                    try{
                        w.write(record);
                    }catch(Throwable t){
                        Sneak.sneakyThrow(t);
                    }
                },
                (w1, w2) -> w1,
                w->null ,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }
    /**
     * Write all the {@link FastaRecord}s in the Stream to a the given {@link FastaWriter}
     * but do not close the writer.  The writer must be closed by the owner, this allows further writes to be made
     * after the collector has finished.
     * @param writer the {@link FastaWriter} to write to.  Can not be {@code null}.
     * @param fastaFunction function to convert the elements of the Stream to FastaRecords to write.
     * @return a new Collector that will write the accumulated records to the fasta writer.
     * @throws NullPointerException if any parameter is null.
     */
    public static <T, R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>> Collector<T, ?, Void> write(W writer, Function<T, F> fastaFunction){
        Objects.requireNonNull(writer);
        Objects.requireNonNull(fastaFunction);
        
    	return Collector.of(() -> writer,
                (w, t) -> {
                    try{
                        F fasta = fastaFunction.apply(t);
                        if(fasta !=null) {
                            w.write(fasta);
                        }
                    }catch(Throwable e){
                        Sneak.sneakyThrow(e);
                    }
                },
                (w1, w2) -> w1,
                w-> null,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED
        );
    }
    /**
     * Write all the {@link FastaRecord}s in the Stream to a the given {@link FastaWriter}
     * AND CLOSE the WRITER WHEN COMPLETE.
     * @param writer the {@link FastaWriter} to write to.  Can not be {@code null}.
     * @param consumer function to convert the elements of the Stream to FastaRecords to write.
     * @return a new Collector that will write the accumulated records to the fasta writer.
     */
    public static <T, R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>, E extends Throwable> Collector<T, ?, Void> writeAndClose(W writer, ThrowingBiConsumer<W, T, E> consumer){
        return Collector.of(() -> writer,
                (w, t) -> {
                    try{
                    consumer.accept(w,t);
                    }catch(Throwable e){
                        Sneak.sneakyThrow(e);
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

    public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>, T extends DataStore<S>, D extends FastaDataStore<R,S,F, T>> Collector<F, ?, D> toDataStore(Class<D> datastoreClass){
        return Collectors.collectingAndThen(Collectors.toConcurrentMap(f -> f.getId(), Function.identity()),
                map-> (D) DataStoreUtil.adapt(datastoreClass, map));
    }
    /*
    public static <T, R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>, E extends Throwable> Collector<T, ?, Void> writeUsingBlockingQueue(W writer, int queueSize, ThrowingBiConsumer<W,T, E> writerConsumer  ) throws E{
        return new BlockingQueueFastaWriterCollector<>(writer,  writerConsumer, queueSize);
    }

    public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>> Collector<F, ?, Void> writeUsingBlockingQueue(W writer, int queueSize) {
        return new BlockingQueueFastaWriterCollector<>(writer, W::write, queueSize);
    }
    public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>> Collector<F, ?, Void> writeUsingBlockingQueue(W writer) {
        return writeUsingBlockingQueue(writer, 100);
    }

        public static <R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>> Collector<F, ?, Void> writeUnordered(W writer){
        return Collector.of(() -> writer,
                (w, record) -> {
                    try{
                        w.write(record);
                    }catch(Throwable t){
                        Sneak.sneakyThrow(t);
                    }
                },
                (w1, w2) -> w1,
                w-> {
                        IOUtil.closeAndIgnoreErrors(w);
                        return null;
                },
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED
        );
    }

     */


    private static class BlockingQueueFastaWriterCollector <T, R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>, E extends Throwable> implements Collector<T, BlockingAccumulator, Void>{
        final BlockingQueue<Object> queue;

        Object POISON = new Object();
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Future future;
        BiConsumer<W, T> fastaFunction;
        BlockingQueueFastaWriterCollector(W writer, ThrowingBiConsumer<W, T, E> fastaFunction, int queueSize){
            queue = new ArrayBlockingQueue<>(queueSize);
            future = singleThreadExecutor.submit(()->{
                try{
                    Object current;
                    while( (current = queue.take()) != POISON){
                        fastaFunction.accept(writer, (T) current);
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
        public BiConsumer<BlockingAccumulator, T> accumulator() {
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




}
