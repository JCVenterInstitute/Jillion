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

import org.jcvi.jillion.core.Range;

import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * {@code CompactProteinSequence} is 
 * a {@link ProteinSequence} that uses a byte array to store each
 * each {@link AminoAcid} using 5 bits. This is a 37.5% memory reduction compared to 
 * encoding the data as one byte each or 68% memory reduction compared
 * to encoding each AminoAcid as one char each.
 * @author dkatzel
 *
 */
class CompactProteinSequence extends AbstractProteinSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 112126544528540261L;

	public CompactProteinSequence(AminoAcid[] aas) {
		super(aas, CompactProteinSequenceCodec.INSTANCE);
	}
	
	private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
		throw new java.io.InvalidObjectException("Proxy required");
	}

    @Override
    public ProteinSequenceBuilder newEmptyBuilder() {
        return new ProteinSequenceBuilder();
    }

    @Override
    public ProteinSequenceBuilder newEmptyBuilder(
            int initialCapacity) {
        return new ProteinSequenceBuilder(initialCapacity);
    }


}
