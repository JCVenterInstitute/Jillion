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
/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code SliceElement} is a single
 * position of a read contained
 * in a {@link Slice}. Each read
 * in the slice contributes a base, quality value, and direction.
 * @author dkatzel
 *
 *
 */
public interface SliceElement {
	
	/**
     * Get the Id of this element.  Each element in a single Slice must
     * have a different Id, although SliceElements from 
     * different Slices can have the same Id.  This Id is usually the 
     * read Id.
     * @return the Id of this slice element.
     */
    String getId();
    /**
     * Get the {@link Nucleotide} of this SliceElement.
     * @return
     */
    Nucleotide getBase();
    /**
     * Get the {@link PhredQuality} of this SliceElement.
     * @return
     */
    PhredQuality getQuality();
    /**
     * Get the {@link Direction} of this SliceElement.
     * @return
     */
    Direction getDirection();
}
