/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.jillion.internal.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jcvi.jillion.core.Range;
/**
 * Utility class for working
 * with Java 8 Streams.
 * @author dkatzel
 *
 */
public final class StreamUtil {

	private StreamUtil(){
		//can not instantiate
	}
	/**
     * Create a new Runnable that will close
     * the given {@link Closeable} and rethrow any
     * IOException as an {@link UncheckedIOException}.
     * This is supposed to be used as input to 
     * {@link java.util.stream.Stream#onClose(Runnable)}.
     * @param c the {@link Closeable} to be closed; can not be null.
     * 
     * @return a new {@link Runnable} that will try to close the Closeable
     */
    public static Runnable newOnCloseRunnableThatThrowsUncheckedIOExceptionIfNecessary(Closeable c){
    	return () ->{
    		try {
				c.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
    	};
    }
    
    /**
     * Create a new Stream that gets the elements from the provided generator.  The Stream is done
     * when the generator returns {@link Optional#empty()}.
     * @param generatingFunction
     * @return
     */
    public static <T> Stream<T> newGeneratedStream(Supplier<Optional<T>> generatingFunction){
       return newGeneratedStream(generatingFunction, false);
    }
    /**
     * Create a Stream that gets the elements from the provided generator.  The Stream is done
     * when the generator returns {@link Optional#empty()}.
     * @param generatingFunction
     * @param parallel should the returned Stream be parallel.
     * @return
     */
    public static <T> Stream<T> newGeneratedStream(Supplier<Optional<T>> generatingFunction, boolean parallel){
        Objects.requireNonNull(generatingFunction);
        Spliterator<T> spliterator = new Spliterators.AbstractSpliterator<T>(
                Long.MAX_VALUE, 
                Spliterators.AbstractSpliterator.ORDERED| Spliterators.AbstractSpliterator.NONNULL) {

            public boolean tryAdvance(Consumer<? super T> action) {
                Optional<T> r = generatingFunction.get();
                if (!r.isPresent()) {
                    return false;
                }
                action.accept(r.get());
                return true;
            }
        };
                
                
        return StreamSupport.stream(spliterator, parallel);
    }
}
