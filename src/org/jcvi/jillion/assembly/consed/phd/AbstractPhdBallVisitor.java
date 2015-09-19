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
package org.jcvi.jillion.assembly.consed.phd;
/**
 * {@code AbstractPhdBallVisitor} is a {@link PhdBallVisitor}
 * that implements all the methods with default
 * implementations that don't do anything.  This code
 * is meant to be extended so users don't have to implement
 * any methods that they do not care about (users must override
 * any method that want to handle).
 *  
 * @author dkatzel
 *
 */
public abstract class AbstractPhdBallVisitor implements PhdBallVisitor {
	/**
	 * Ignores the file comment.
	 * {@inheritDoc}
	 */
	@Override
	public void visitFileComment(String comment) {
		//no-op

	}
	/**
	 * Always skips this read.
	 * @return null 
	 * {@inheritDoc}
	 */
	@Override
	public PhdVisitor visitPhd(PhdBallVisitorCallback callback, String id,
			Integer version) {
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
