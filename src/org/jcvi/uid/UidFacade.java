/*
 * Created on Jul 19, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;

/**
 * Abstraction used to get Unique IDs.
 *
 * @author dkatzel
 * @author jsitz
 */
public interface UidFacade {

    /**
     * Get a unique ID from Facade.
     *
     * @return a unique ID.
     * @throws UidFacadeException if there is a problem
     * getting a unique ID
     */
    long getUniqueId() throws UidFacadeException;
}
