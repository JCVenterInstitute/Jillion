/*
 * Created on Jul 19, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;

/**
 * A <code>UidFacadeException</code> is thrown when there is an error while
 * fetching UIDs from the {@link UidFacade}.
 *
 * @author dkatzel
 * @author jsitz
 */
public class UidFacadeException extends Exception {

    /**
     * The Serial Version UID
     */
    private static final long serialVersionUID = 3161480320572275864L;

    /**
     * Creates a new <code>UidFacadeException</code>.
     *
     * @param message A message describing the cause of this Exception.
     */
    public UidFacadeException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>UidFacadeException</code>.
     *
     * @param message A message describing the Exception.
     * @param cause The {@link Throwable} declared as the cause of this Exception.
     */
    public UidFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

}
