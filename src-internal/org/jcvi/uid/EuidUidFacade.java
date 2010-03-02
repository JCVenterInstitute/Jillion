/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
