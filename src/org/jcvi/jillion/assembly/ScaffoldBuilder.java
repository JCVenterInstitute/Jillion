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
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code ScaffoldBuilder} is a {@link Builder}
 * that builds a single {@link Scaffold}.
 * All contigs added to this scaffold need to use the same reference/ scaffold
 * coordinate space so they can be aligned to each other correctly.
 * @author dkatzel
 *
 */
public interface ScaffoldBuilder extends Builder<Scaffold> {
	/**
	 * Add the given {@link PlacedContig}
	 * to the Scaffold being built.
	 * @param placedContig the placedContig to add
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if placedContig is null.
	 */
	ScaffoldBuilder add(PlacedContig placedContig);
	/**
	 * Add the given contig to the Scaffold
	 * being built at the given range in the given direction.
	 * @param contigId the id of the contig,
	 * must be unique to this Scaffold; 
	 * can not be null.
	 * @param contigRange the {@link Range}
	 * that this contig aligns to on the scaffold;
	 * can not be null.
	 * @param contigDirection the {@link Direction} of
	 * this contig relative to the Scaffold.
	 * @return this;
	 */
	ScaffoldBuilder add(String contigId, Range contigRange,
			Direction contigDirection);
	/**
	 * Convenience method for adding a contig in the forward
	 * direction.  This is the same as
	 * {@link #add(String, Range, Direction) add(contigId, contigRange, Direction.FORWARD)}
	 * @see #add(String, Range, Direction)
	 */
	ScaffoldBuilder add(String contigId, Range contigRange);

	/**
	 * Shift all contigs in the scaffold so that the first
	 * contig will start at the scaffold origin (offset 0).
	 * The distances between contigs will not change, 
	 * only the actual begin and end locations.  
	 * <p/>
	 * The actual
	 * coordinate conversion will occur at built time during the
	 * {@link #build()} method. This means that the shift flag 
	 * can be switched on or off many times without affecting
	 * any of the contig coordinates (until the Scaffold is built).
	 * @param shiftContigs
	 * @return this
	 */
	ScaffoldBuilder shiftContigsToOrigin(boolean shiftContigs);
	/**
	 * Create a new {@link Scaffold}
	 * object using the all the given contigs so far.
	 * {@inheritDoc}
	 */
	Scaffold build();

}
