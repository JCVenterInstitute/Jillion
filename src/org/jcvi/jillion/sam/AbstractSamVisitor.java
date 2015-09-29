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
package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.header.SamHeader;

/**
 * {@code AbstractSamVisitor} is an implementation
 * of {@link SamVisitor} that implements all the methods
 * of {@link SamVisitor} as empty methods.
 * 
 * Users may subclass {@code AbstractSamVisitor} and 
 * override the methods they want to implement.
 * @author dkatzel
 *
 */
public abstract class AbstractSamVisitor implements SamVisitor{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitRecord(SamVisitorCallback callback, SamRecord record,
			VirtualFileOffset start, VirtualFileOffset end) {
		//no-op
	}
	
	
	@Override
	public void visitHeader(SamVisitorCallback callback, SamHeader header) {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitEnd() {
		//no-op
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void halted() {
		//no-op
	}
}
