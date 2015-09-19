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

import java.io.Serializable;

import org.jcvi.jillion.core.residue.ResidueSequence;

/**
 * {@code ProteinSequence} is a marker interface for
 * {@link Sequence}s that contain {@link AminoAcid}s.
 * <br/>
 * {@link ProteinSequence} is {@link Serializable} in a (hopefully)
 * forwards compatible way. However, there is no 
 * guarantee that the implementation will be the same
 * or even that the implementation class will be the same;
 * but the deserialized object should always be equal
 * to the sequence that was serialized.
 *
 * @author dkatzel
 */
public interface ProteinSequence extends ResidueSequence<AminoAcid>, Serializable {

	@Override
	ProteinSequenceBuilder toBuilder();
}
