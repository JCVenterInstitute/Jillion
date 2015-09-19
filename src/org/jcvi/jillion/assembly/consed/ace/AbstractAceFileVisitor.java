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

import java.util.Date;
/**
 * {@code AbstractAceFileVisitor} is an {@link AceFileVisitor}
 * that implements all the methods as stubs.  By default
 * all non-void methods return {@code null}.  Users should
 * create subclasses that override the methods they wish to handle.
 * @author dkatzel
 *
 */
public abstract class AbstractAceFileVisitor implements AceFileVisitor{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitHeader(int numberOfContigs, long totalNumberOfReads) {
		//no-op		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AceContigVisitor visitContig(AceFileVisitorCallback callback,
			String contigId, int consensusLength, int numberOfReads,
			int numberOfBaseSegments, boolean reverseComplemented) {
		//always skip
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitReadTag(String id, String type, String creator,
			long gappedStart, long gappedEnd, Date creationDate,
			boolean isTransient) {
		//no-op
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AceConsensusTagVisitor visitConsensusTag(String id, String type,
			String creator, long gappedStart, long gappedEnd,
			Date creationDate, boolean isTransient) {
		//always skip
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitWholeAssemblyTag(String type, String creator,
			Date creationDate, String data) {
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
