/*
 * Created on Jul 19, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;

import org.tigr.utils.EUIDService;


/**
 * This implementation of <code>UidFacade</code> fetches Unique IDs from
 * the JCVI EUIDService.
 *
 * @author dkatzel
 * @author jsitz
 */
public class EuidUidFacade implements UidFacade {

    /**
     * 
    * {@inheritDoc}
     */
    public long getUniqueId() throws UidFacadeException {

        try {
            return fetchEUIDFromEUIDService();
        } catch (Exception e) {
            throw new UidFacadeException("Error fetching EUID", e);
        }
    }

    /**
     * Fetches a new UID from an EUID Service.
     *
     * @return A new, unique ID.
     * @throws Exception If there is an error fetching the ID from the service.
     */
    protected long fetchEUIDFromEUIDService() throws Exception {
        return EUIDService.getEUID();
    }

}
