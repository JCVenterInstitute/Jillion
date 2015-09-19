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
package org.jcvi.jillion.assembly.ca.asm;

import org.jcvi.jillion.assembly.Contig;

/**
 * A Unitig is a special kind of contig
 * whose length is "as long as possible" until
 * the assembler found a contradiction.  The final
 * contigs produced by the Celera Assembler
 * are several unitigs that have been extended 
 * to overlap each other.
 * <p/>
 * A Unitig of a repetitive region in the genome
 * will only contain the "perfect repeat"; all underlying reads
 * will be trimmed so any parts of the read that span the non-repetitive
 * parts of the genome will get trimmed off. 
 * This greatly simplifies downstream assembly steps,
 * since we can treat identical unitigs
 * as a a single repeat motif which the downstream
 * assembly steps can use coverage
 * information to figure out how many copies exist.
 * @author dkatzel
 *
 *
 */
public interface AsmUnitig extends Contig<AsmAssembledRead>{

}
