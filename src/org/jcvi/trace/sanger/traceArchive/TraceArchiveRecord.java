/*
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public interface TraceArchiveRecord {
    /**
     * Returns the Attribute Value of the given {@link TraceInfoField}.
     * @param traceInfoField the id of the property to get.
     * @return the attribute value of that property; 
     * or {@code null} if the {@code TraceArchiveRecord} does
     * not have that property.
     */
    String getAttribute(TraceInfoField traceInfoField);
    /**
     * Does this {@code TraceArchiveRecord} contain this TraceInfoField.
     * @param traceInfoField the TraceInfoField to check.
     * @return {@code true} if this {@code TraceArchiveRecord} 
     * contains this TraceInfoField; {@code false} otherwise.
     */
    boolean contains(TraceInfoField traceInfoField);
    /**
     * Get the Set of key value pairs
     * @return
     */
    Set<Entry<TraceInfoField, String>> entrySet();
    /**
     * Extra ancillary information not specified by the TraceInfo Fields.
     * @return
     */
    Map<String, String> getExtendedData();
}
