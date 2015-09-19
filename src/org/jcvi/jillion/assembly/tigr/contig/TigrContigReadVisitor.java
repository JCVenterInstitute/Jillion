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
package org.jcvi.jillion.assembly.tigr.contig;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code TigrContigReadVisitor} is a 
 * visitor interface to visit a single
 * read inside  contig  from a TIGR {@literal .contig}
 * encoded file.  The id, direction and valid range
 *  of the read
 * have already been given by 
 * {@link TigrContigVisitor#visitRead(String, long, org.jcvi.jillion.core.Direction, org.jcvi.jillion.core.Range)}.
 * @author dkatzel
 *
 */
public interface TigrContigReadVisitor {
	/**
	 * Visit the gapped {@link NucleotideSequence}
	 * of the valid range bases that provide coverage
	 * for this contig.  
	 * @param gappedBasecalls a {@link NucleotideSequence},
	 * will never be null.
	 */
	void visitBasecalls(NucleotideSequence gappedBasecalls);
	/**
	 * Visit the end of this read.
	 */
	void visitEnd();
}
