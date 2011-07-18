/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Apr 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace;

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
