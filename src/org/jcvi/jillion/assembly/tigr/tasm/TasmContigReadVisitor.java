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
package org.jcvi.jillion.assembly.tigr.tasm;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TasmContigReadVisitor {

	void visitBasecalls(NucleotideSequence gappedBasecalls);
	
	//we don't need to visit the asm_lend, asm_rend, seq_lend, seq_rend
	//since they can be computed using the start offset, valid range and gapped sequence
	
	void visitEnd();
}
