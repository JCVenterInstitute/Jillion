package org.jcvi.common.core.assembly;

import org.jcvi.common.core.util.Builder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
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