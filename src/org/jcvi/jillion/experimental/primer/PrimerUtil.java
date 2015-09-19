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
package org.jcvi.jillion.experimental.primer;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;

public final class PrimerUtil {

	 public static NucleotideSequence M13_FORWARD_PRIMER = new NucleotideSequenceBuilder("TGTAAAACGACGGCCAGT").build();
	 
	 
	 public static NucleotideSequence M13_REVERSE_PRIMER = new NucleotideSequenceBuilder("CAGGAAACAGCTATGACC").build();


	 private PrimerUtil(){
		 //can not instantiate
	 }
}
