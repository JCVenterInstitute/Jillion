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
/*
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.experimental.trace.archive2;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
     * Get the Set of key value pairs of all the metadata.
     * @return all the key-value pairs as a Set.
     */
    Set<Entry<TraceInfoField, String>> entrySet();
    /**
     * Extra ancillary information not specified by the TraceInfo Fields.
     * @return a Map; will not be null but could be empty.
     */
    Map<String, String> getExtendedData();
}
