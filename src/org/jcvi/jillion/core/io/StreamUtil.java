package org.jcvi.jillion.core.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
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
     * @param c
     * @return
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
}
