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
package org.jcvi.jillion.core.datastore;
/**
 * {@code DataStoreClosedException} is a {@link IllegalStateException}
 * that is thrown if an operation that requires the {@link DataStore}
 * to be open is called on a closed {@link DataStore}.
 * @author dkatzel
 *
 */
public final class DataStoreClosedException extends IllegalStateException {


	/**
	 * auto-generated serialVersion id
	 */
	private static final long serialVersionUID = -4221044131514655440L;

	/**
	 * Create a new instance of DataStoreClosedException
	 * with the given error message.
	 * @param message the error message.
	 */
	public DataStoreClosedException(String message) {
		super(message);
	}

}
