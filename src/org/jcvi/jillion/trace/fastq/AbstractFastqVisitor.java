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
package org.jcvi.jillion.trace.fastq;

/**
 * {@code AbstractFastqVisitor} is a {@link FastqVisitor}
 * implementation that implements all methods
 * as no-ops.
 * 
 * @author dkatzel
 * 
 * @since 5.0
 *
 */
public abstract class AbstractFastqVisitor implements FastqVisitor {
	/**
	 * Skips the record with the provided define;
	 * please override if you want to visit {@link FastqRecord}s.
	 * @return null
	 */
	@Override
	public FastqRecordVisitor visitDefline(FastqVisitorCallback callback,
			String id, String optionalComment) {
		return null;
	}
	/**
	 * Does nothing by default, please override
	 * if you want to do something when all the fastqs
	 * have been visited.
	 */
	@Override
	public void visitEnd() {
		//no-op

	}
	/**
	 * Does nothing by default, please override
	 * if you want to do something when all the fastqs
	 * have been visited.
	 */
	@Override
	public void halted() {
		//no-op

	}

}
