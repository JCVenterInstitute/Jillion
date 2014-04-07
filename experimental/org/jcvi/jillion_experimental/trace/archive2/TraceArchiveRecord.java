/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion_experimental.trace.archive2;

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
