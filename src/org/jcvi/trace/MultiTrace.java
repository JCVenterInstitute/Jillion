/*
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.io.Closeable;
@Deprecated
public interface MultiTrace<T extends Trace> extends Iterable<String>, Closeable {
    /**
     * Get the Trace associated with the given id.
     * @param id
     * @return the Trace or {@code null} if no
     * Trace with that id exists.
     * @throws TraceDecoderException if there is an error 
     * fetching the Trace.
     */
    T getTrace(String id) throws TraceDecoderException;
    int numberOfTraces() throws TraceDecoderException;;
    boolean contains(String id) throws TraceDecoderException;;

}
