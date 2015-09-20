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
package org.jcvi.common.examples;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public class UngappingSequence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NucleotideSequence ungappedSequence = new NucleotideSequenceBuilder("ACG-ACG--T")
													.ungap()
													.build();
				
		NucleotideSequence reverseSequence = new NucleotideSequenceBuilder(ungappedSequence)
											.reverseComplement()
											.build();

	}

}
