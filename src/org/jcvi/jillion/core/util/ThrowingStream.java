/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.util;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jcvi.jillion.core.util.streams.ThrowingConsumer;
import org.jcvi.jillion.core.util.streams.ThrowingFunction;
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
                Sneak.sneakyThrow(ex);
            }
        });
    }
    /**
     * Iterate over each element remaining in the stream and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param mapper the function to map the input type into the output for each element.
     * @throws E the Exception the throwing consumer might throw.
     *
     * @since 5.3.2
     */
    default <R, E extends Exception> ThrowingStream<R> throwingMap(ThrowingFunction<? super T,R, E> mapper) throws E {
        return map(t-> {
            try {
                return mapper.apply(t);
            } catch (Throwable ex) {
                return Sneak.sneakyThrow(ex);
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
                 Sneak.sneakyThrow(ex);
            }
        });
        
    }
   
    @Override
    ThrowingStream<T> sequential();
    @Override
    ThrowingStream<T> parallel();
    @Override
    ThrowingStream<T> unordered();
    @Override
    ThrowingStream<T> onClose(Runnable closeHandler);
    
    @Override
    ThrowingStream<T> filter(Predicate<? super T> predicate);
    @Override
    <R> ThrowingStream<R> map(Function<? super T, ? extends R> mapper);
    
    @Override
    <R> ThrowingStream<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper);
    
    @Override
    ThrowingStream<T> distinct();
    @Override
    ThrowingStream<T> sorted();
    @Override
    ThrowingStream<T> sorted(Comparator<? super T> comparator);
    @Override
    ThrowingStream<T> peek(Consumer<? super T> action);
    @Override
    ThrowingStream<T> limit(long maxSize);
    @Override
    ThrowingStream<T> skip(long n);
    
    

    
}
