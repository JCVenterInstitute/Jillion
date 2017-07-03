package org.jcvi.jillion.core.util.streams;

public /**
 * A {@link java.util.function.Consumer} that can throw an exception.
 * @author dkatzel
 *
 * @param <T> the type the consumer accepts.
 * @param <E> the exception that can be thrown.
 * 
 * @since 5.3
 */
interface ThrowingConsumer<T, E extends Throwable>{
    void accept(T t) throws E;

}