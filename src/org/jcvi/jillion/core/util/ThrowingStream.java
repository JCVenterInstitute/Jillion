package org.jcvi.jillion.core.util;

import java.util.stream.Stream;

import org.jcvi.jillion.internal.core.util.Sneak;
/**
 * An extension of {@link Stream} but with additional methods
 * that can throw checked exceptions, usually named {@code throwingXXX()}.
 * 
 * @author dkatzel
 *
 * @param <T> the type of element in the stream.
 * @since 5.3
 */
public interface ThrowingStream<T> extends Stream<T>{
    /**
     * Wrap the given Stream in a ThrowingStream to get the additional
     * throwing methods.
     * @param stream the {@link Stream} to wrap; can not be null.
     * @return a new {@link ThrowingStream}; will never be null.
     * 
     * @throws NullPointerException if stream is null.
     */
    public static <T> ThrowingStream<T> asThrowingStream( Stream<T> stream){
        return new ThrowingStreamImpl<>(stream);
    }
    /**
     * A {@link java.util.function.Consumer} that can throw an exception.
     * @author dkatzel
     *
     * @param <T> the type the consumer accepts.
     * @param <E> the exception that can be thrown.
     */
    interface ThrowingConsumer<T, E extends Exception>{
        void accept(T t) throws E;
       
    }
    /**
     * Iterate over each element remaining in the stream and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEach(ThrowingConsumer<? super T, E> action) throws E {
        forEach(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                throw Sneak.sneakyThrow(ex);
            }
        });
    }
    /**
     * Iterate over each element remaining in the stream in order and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEachOrdered(ThrowingConsumer<? super T, E> action) throws E{
        forEachOrdered(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                throw Sneak.sneakyThrow(ex);
            }
        });
        
    }

    
}
