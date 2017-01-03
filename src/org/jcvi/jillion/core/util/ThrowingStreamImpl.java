package org.jcvi.jillion.core.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class ThrowingStreamImpl<T> implements ThrowingStream<T> {

    private final Stream<T> delegate;
    
    public ThrowingStreamImpl(Stream<T> delegate){
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public ThrowingStream<T> filter(Predicate<? super T> predicate) {
        return handleNewStream(() -> delegate.filter(predicate));
    }

    @Override
    public <R> ThrowingStream<R> map(Function<? super T, ? extends R> mapper) {
        return ThrowingStream.asThrowingStream(delegate.map(mapper));
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
        return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
        return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
        return delegate.mapToDouble(mapper);
    }

    @Override
    public <R> ThrowingStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return ThrowingStream.asThrowingStream(delegate.flatMap(mapper));
    }

    @Override
    public IntStream flatMapToInt(
            Function<? super T, ? extends IntStream> mapper) {
        return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(
            Function<? super T, ? extends LongStream> mapper) {
        return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(
            Function<? super T, ? extends DoubleStream> mapper) {
        return delegate.flatMapToDouble(mapper);
    }

    @Override
    public ThrowingStream<T> distinct() {
        return handleNewStream(delegate::distinct);
    }

    @Override
    public ThrowingStream<T> sorted() {
        return handleNewStream(delegate::sorted);
    }

    @Override
    public ThrowingStream<T> sorted(Comparator<? super T> comparator) {
        return handleNewStream(()-> delegate.sorted(comparator));
    }

    @Override
    public ThrowingStream<T> peek(Consumer<? super T> action) {
        return handleNewStream(() -> delegate.peek(action));
    }

    @Override
    public ThrowingStream<T> limit(long maxSize) {
        return handleNewStream( ()->delegate.limit(maxSize));
    }

    @Override
    public ThrowingStream<T> skip(long n) {
        return handleNewStream(() ->delegate.skip(n));
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        delegate.forEach(action);
        
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator,
            BinaryOperator<U> combiner) {
        return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier,
            BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        return delegate.collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
        return delegate.min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
        return delegate.max(comparator);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return delegate.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        return delegate.findFirst();
    }

    @Override
    public Optional<T> findAny() {
        return delegate.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return delegate.isParallel();
    }

    @Override
    public ThrowingStream<T> sequential() {
        return handleNewStream(delegate::sequential);
    }

    @Override
    public ThrowingStream<T> parallel() {
        return handleNewStream(delegate::parallel);
    }

    @Override
    public ThrowingStream<T> unordered() {
        return handleNewStream(delegate::unordered);
    }

    @Override
    public ThrowingStream<T> onClose(Runnable closeHandler) {
        Stream<T> newStream = delegate.onClose(closeHandler);
        if(newStream == delegate){
            return this;
        }
        return ThrowingStream.asThrowingStream(newStream);
    }
    
    private ThrowingStream<T> handleNewStream(Supplier<Stream<T>> supplier){
        Stream<T> newStream = supplier.get();
        if(newStream == delegate){
            return this;
        }
        return ThrowingStream.asThrowingStream(newStream);
    }
    
   

    @Override
    public void close() {
        delegate.close();
        
    }
    
    
}
