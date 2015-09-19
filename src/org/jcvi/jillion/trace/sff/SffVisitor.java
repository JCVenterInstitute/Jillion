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
package org.jcvi.jillion.trace.sff;

/**
 * {@code SffVisitor} is a visitor
 * interface to visit components of a single
 * sff encoded file.
 * 
 * @author dkatzel
 *
 */
public interface SffVisitor {
	/**
	 * Visit the {@link SffCommonHeader} of the given
	 * sff file which explains the metadata common to all
	 * reads in the sff file.
	 * @param callback  a {@link SffVisitorCallback} that can be used
     * to communicate with the parser object. 
	 * @param header the {@link SffCommonHeader} instance;
	 * will never be null.
	 */
	void visitHeader(SffVisitorCallback callback, SffCommonHeader header);
	/**
	 * Visit the a single read encoded in this sff file.
	 * @param callback  a {@link SffVisitorCallback} that can be used
     * to communicate with the parser object. 
	 * @param readHeader an instance of {@link SffReadHeader}
	 * describing this read. 
	 * @return an instance of {@link SffFileReadVisitor}
	 * that will be used to visit this read's data;
	 * if {@code null} is returned, then this read's data will be skipped.
	 */
	SffFileReadVisitor visitRead(SffVisitorCallback callback, SffReadHeader readHeader);
	/**
	 * The end of the sff file has been reached.
	 */
	void end();
}
