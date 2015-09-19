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
package org.jcvi.jillion.assembly.consed.ace;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.QualitySequence;
/**
 * {@code AbstractAceContigVisitor} is an {@link AceContigVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceContigVisitor implements AceContigVisitor{

	@Override
	public void visitBasesLine(String mixedCaseBasecalls) {
		//no-op		
	}

	@Override
	public void visitConsensusQualities(
			QualitySequence ungappedConsensusQualities) {
		//no-op		
	}

	@Override
	public void visitAlignedReadInfo(String readId, Direction dir,
			int gappedStartOffset) {
		//no-op
	}

	@Override
	public void visitBaseSegment(Range gappedConsensusRange, String readId) {
		//no-op
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		//always skip
		return null;
	}

	@Override
	public void visitEnd() {
		//no-op		
	}

	@Override
	public void halted() {
		//no-op
	}

}
