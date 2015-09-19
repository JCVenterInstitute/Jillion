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
package org.jcvi.jillion.profile;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code ProfileWriter} 
 * @author dkatzel
 *
 */
public interface ProfileWriter extends Closeable{

	/**
	 * Add the given sequence to this profile.
	 * @param startOffset the start offset (0-based)
	 * that this reads starts relative to the provided reference;
	 * must be >=0 and <= reference length - sequence length.
	 * @param sequence the sequence to add; can not be null
	 * and length must be <= reference length - start offset.
	 * @throws NullPointerException if sequence is null.
	 * @throws IndexOutOfBoundsException if startOffset or any adjusted
	 * position in the sequence is beyond the range of the provided reference.
	 */
	void addSequence(int startOffset, NucleotideSequence sequence);
	/**
	 * Close the writer and flush
	 * all data to the output.
	 */
	@Override
	public void close() throws IOException;
}
