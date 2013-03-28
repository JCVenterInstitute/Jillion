/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

public interface IdedSlice extends Slice<IdedSliceElement>{

	/**
     * Does this {@link Slice} contain a 
     * {@link IdedSliceElement} with the given id.
     * @param elementId the id of the {@link IdedSliceElement} being queried.
     * @return {@code true} if this Slice does contain
     * a {@link IdedSliceElement} with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link IdedSliceElement} if exists; or {@code null}
     * if there is no {@link IdedSliceElement} for this Slice with that id.
     */
    IdedSliceElement getSliceElement(String elementId);
}
