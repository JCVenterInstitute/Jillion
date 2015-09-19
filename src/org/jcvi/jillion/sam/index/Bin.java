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
package org.jcvi.jillion.sam.index;

import java.util.List;
/**
 * {@code Bin} is an object
 * representation of a single bin
 * in the binning scheme used to index
 * reads in a reference alignment.
 * A distinct bin uniquely corresponds to a distinct internal node in a R-tree
 * 
 * @author dkatzel
 *
 */
public interface Bin {

	/**
	 * Get the number for this Bin.
	 * @return a positive number.
	 */
	int getBinNumber();
	/**
	 * Get the {@link Chunk}s
	 * associated with this bin.
	 * @return the List of {@link Chunk}s
	 * will never be null but may be empty.
	 */
	List<Chunk> getChunks();
	
	@Override
	int hashCode();
	/**
	 * Two bins are equal if they 
	 * have the same bin number 
	 * and the an equal list of Chunks.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object o);

}
