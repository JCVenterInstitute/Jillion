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
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.slice;
/**
 * A {@code Slice} is a one base wide cut of an assembly from zero 
 * or more {@link SliceElement}s.
 * @author dkatzel
 *
 *
 */
public interface Slice extends Iterable<SliceElement>{
    /**
     * Get the coverage depth of this Slice.  this 
     * should be the same as the size of the
     * List returned from {@link #getSliceElements()}.
     * @return the coverage depth of this slice, will
     * always be {@code >= 0}.
     */
    int getCoverageDepth();
    /**
     * Does this {@link Slice} contain a 
     * {@link SliceElement} with the given id.
     * @param elementId the id of the SliceElement being queried.
     * @return {@code true} if this Slice does contain
     * a SliceElement with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link SliceElement} if exists; or {@code null}
     * if there is no SliceElement for this Slice with that id.
     */
    SliceElement getSliceElement(String elementId);
}
