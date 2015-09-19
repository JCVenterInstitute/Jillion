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
package org.jcvi.jillion.trace.chromat.scf;
/**
 * PrivateData is a wrapper around a byte array
 * for additional optional data in an scf file.
 * The scf file specification puts no limitations
 * on what this data can contain and is implementation 
 * specific.  It is up to different scf writer implementations
 * to decide what data to put here (if any) and how to encode it. 
 * 
 * @author dkatzel
 *
 */
public interface PrivateData {

	/**
	 * @return the data
	 */
	byte[] getBytes();

	/**
	 * {@inheritDoc}
	 */
	int hashCode();

	/**
	 * Two PrivateData instances are equal
	 * if they both contain the same data.
	 */
	boolean equals(Object obj);

}
