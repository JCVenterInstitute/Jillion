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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;

public interface PlacedContig extends Rangeable {

	/**
     * Get the start coordinate of this placed object
     * on the placed axis.
     * @return the start as a long.
     */
    long getBegin();
    /**
     * Get the end coordinate of this placed object
     * on the placed axis.
     * @return the end as a long.
     */
    long getEnd();
    /**
     * Get the length of this placed object
     * on the axis.
     * @return the length of this placed object.
     */
    long getLength();
    
    String getContigId();
    Direction getDirection();
    
    Range getValidRange();
    
}
