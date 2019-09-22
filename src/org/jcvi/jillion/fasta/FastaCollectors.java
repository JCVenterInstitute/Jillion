package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.core.util.streams.ThrowingFunction;
import org.jcvi.jillion.internal.core.util.Sneak;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class FastaCollectors {

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
                w-> null,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

    public static <T, R, S extends Sequence<R>, F extends FastaRecord<R,S>, W extends FastaWriter<R, S,F>> Collector<T, ?, Void> write(W writer, Function<T, F> fastaFunction){
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
                Collector.Characteristics.IDENTITY_FINISH
        );
    }

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

    private static class BlockingAccumulator{

        private  final BlockingQueue<Object> queue;

        public BlockingAccumulator(BlockingQueue<Object> queue) {
            this.queue = queue;
        }

        public void put(Object fasta) {
            try {
                queue.put(fasta);
            } catch (InterruptedException e) {
                Sneak.sneakyThrow(e);
            }
        }
    }

}
