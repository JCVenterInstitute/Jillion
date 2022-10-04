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
package org.jcvi.jillion.core.residue.aa;

import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
/**
 * {@code UngappedProteinSequence} is a {@link ProteinSequence}
 * which contains no gaps.  This allows us to short circuit many 
 * of the gap to ungap computations for improved
 * performance.
 * @author dkatzel
 *
 */
class UngappedProteinSequence extends CompactProteinSequence{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3481921186285369074L;

	public UngappedProteinSequence(AminoAcid[] aas) {
		super(aas);
	}

	@Override
	public List<Integer> getGapOffsets() {
		return Collections.emptyList();
	}
	@Override
	public IntStream gaps() {
		return IntStream.empty();
	}

	@Override
	public int getNumberOfGaps() {
		return 0;
	}

	@Override
	public boolean hasGaps() {
		return false;
	}

	@Override
	public boolean isGap(int gappedOffset) {
		return false;
	}

	@Override
	public long getUngappedLength() {
		return getLength();
	}

	@Override
	public int getNumberOfGapsUntil(int gappedValidRangeIndex) {
		return 0;
	}


	@Override
	public int getUngappedOffsetFor(int gappedIndex) {
		return gappedIndex;
	}

	@Override
	public int getGappedOffsetFor(int ungappedIndex) {
		return ungappedIndex;
	}

	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}

	@Override
	public Iterator<AminoAcid> ungappedIterator() {
		return iterator();
	}
}
