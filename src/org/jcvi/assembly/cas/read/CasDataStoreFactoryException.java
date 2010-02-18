/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas.read;

public class CasDataStoreFactoryException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3969710068574868585L;

    /**
     * @param message
     * @param cause
     */
    public CasDataStoreFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public CasDataStoreFactoryException(String message) {
        super(message);
    }

}
