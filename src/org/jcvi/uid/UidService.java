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
