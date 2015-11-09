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
/*
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.datastore;

public class DataStoreException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 537135759192307631L;

    /**
     * Create a new DataStoreException that was caused by another Exception.
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new DataStoreException.
     * @param message the error message.
     */
    public DataStoreException(String message) {
        super(message);
    }

}
