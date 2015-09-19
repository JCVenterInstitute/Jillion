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
package org.jcvi.jillion.assembly.tigr.ctg;

import org.jcvi.jillion.assembly.AbstractTestAssembledReadBuilder;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssembledReadBuilder;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.internal.assembly.DefaultAssembledRead;

public class TestDefaultAssembledReadBuilder extends AbstractTestAssembledReadBuilder<AssembledRead>{

	@Override
	protected AssembledReadBuilder<AssembledRead> createReadBuilder(
			String readId, NucleotideSequence validBases,
			int offset, Direction dir, Range clearRange,
			int ungappedFullLength) {
		AssembledReadBuilder<AssembledRead> builder= DefaultAssembledRead.createBuilder(
				readId, validBases, offset, 
				dir, clearRange, ungappedFullLength);
		
		return builder;
	}

}
