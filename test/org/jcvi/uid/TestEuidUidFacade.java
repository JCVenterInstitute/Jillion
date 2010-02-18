/*
 * Created on Aug 2, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;



import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TestEuidUidFacade  {

    /**
     * This TestDouble of {@link EuidUidFacade}
     * allows us to substitute the depended on {@link UidFacade}
     * with a mock by using the Constructor Injection variation of
     * the Dependency Injection Test Pattern.
     * This class also overrides the {@link #fetchEUIDFromEUIDService()}
     * to use the substitute {@link UidFacade}.
     * @author dkatzel
     *
     *
     */
    private static class EuidUidFacadeTestDouble extends EuidUidFacade{

        private UidFacade uidFacade;

        public EuidUidFacadeTestDouble(UidFacade uidFacade){
            super();
            this.uidFacade = uidFacade;
        }
        /* (non-Javadoc)
         * @see org.jcvi.flim.EuidUidFacade#fetchEUIDFromEUIDService()
         */
        @Override
        protected long fetchEUIDFromEUIDService() throws Exception {

            return uidFacade.getUniqueId();
        }

    }


    private UidFacade uidFacade;

    @Before
    public void setUp() throws Exception {
        uidFacade= createMock(UidFacade.class);
    }
    @Test
    public void testGetUniqueId() throws UidFacadeException{
        long expectedId=12345;

        expect(uidFacade.getUniqueId()).andReturn(expectedId);

        replay(uidFacade);
        EuidUidFacadeTestDouble sut = new EuidUidFacadeTestDouble(uidFacade);
        long actualId =sut.getUniqueId();

        assertEquals(expectedId,actualId);
        verify(uidFacade);
    }
    @Test
    public void testGetUniqueId_throwsException() throws UidFacadeException{


        UidFacadeException expectedException = new UidFacadeException("test UidException");
        expect(uidFacade.getUniqueId()).andThrow(expectedException);

        replay(uidFacade);
        EuidUidFacadeTestDouble sut = new EuidUidFacadeTestDouble(uidFacade);
        try{
            sut.getUniqueId();
            fail("should throw UidFacadeException");
        }
        catch(UidFacadeException e){
            assertEquals(expectedException,e.getCause());
        }

        verify(uidFacade);
    }


}
