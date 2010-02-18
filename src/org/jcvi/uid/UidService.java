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


/**
 * The <code>UidService</code> acts as the singleton implementation of the
 * {@link UidFacade}.
 *
 * @author dkatzel
 * @author jsitz
 */
public final class UidService
{
    /**
     * The singleton instance of the facade.
     */
    private static UidFacade INSTANCE = new EuidUidFacade();

    /**
     * Fetches a single UID from the current UID generation service.
     *
     * @return A <code>long</code> containing a globally unique ID.
     * @throws UidFacadeException If there is an error generating the ID.
     */
    public static long getUniqueId() throws UidFacadeException{
        return INSTANCE.getUniqueId();
    }

    /**
     * Retrieves the current {@link UidFacade} used to generate unique IDs.
     *
     * @return The current {@link UidFacade}.
     */
    public static UidFacade getInstance(){
        return INSTANCE;
    }

    /**
     * Sets the {@link UidFacade} to use for all subsequent requests for
     * unique IDs.
     *
     * @param uidFacade The {@link UidFacade} to use.
     */
    public static void setInstance(UidFacade uidFacade){
        UidService.INSTANCE = uidFacade;
    }
}
