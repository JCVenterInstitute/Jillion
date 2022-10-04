package org.jcvi.jillion.assembly.util.columns;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code AssemblyColumnElement} is a single
 * position of a read contained
 * in a {@link Slice}. Each read
 * in the slice contributes a base and direction.
 * Note that this interface does not contain anything about the id 
 * or the quality value of the read as this higher level abstraction
 * is meant to be used for next-gen assemblies
 * or more abstract assembly concepts where we either don't care about
 * the ids or qualities or there is so much data that keeping track
 * of those fields is not useful.
 * For sub-interfaces that contain ids and quality values use {@link SliceElement}.
 * @author dkatzel
 *
 * @since 6.0
 * @see SliceElement
 */
public interface AssemblyColumnElement {

	/**
	 * Get the {@link Nucleotide} of this SliceElement.
	 * @return the {@link Nucleotide} of this sliceElement.
	 */
	Nucleotide getBase();

	/**
	 * Get the {@link Direction} of this SliceElement.
	 * @return the direction of the read this element belongs.
	 */
	Direction getDirection();

}