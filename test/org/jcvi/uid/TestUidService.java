/*
 * Created on Aug 7, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;

import static org.easymock.EasyMock.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUidService {

    private static UidFacade mockUidFacade = createMock(UidFacade.class);
    private static UidFacade ORIGINAL_FACADE;
    @BeforeClass
    public static void installMockUidFacade(){
        ORIGINAL_FACADE = UidService.getInstance();
        UidService.setInstance(mockUidFacade);
    }

    @AfterClass
    public static void restoreOriginalUidFacade(){
        UidService.setInstance(ORIGINAL_FACADE);
    }
    @Before
    public void setUp() throws Exception {
        reset(mockUidFacade);

    }



    @Test
    public void testGetUniqueId_SingleUid() throws UidFacadeException{
        long expectedUid=12345;
        expect(UidService.getUniqueId()).andReturn(expectedUid);
        replay(mockUidFacade);

        //exercise sut
        long actualUid=UidService.getUniqueId();
        //      verify
        assertEquals(expectedUid,actualUid);
        verify(mockUidFacade);
    }
    @Test
    public void testGetUniqueId_error_shouldThrowUidFacadeException() throws UidFacadeException{
        UidFacadeException expectedException = new UidFacadeException("test exception");
        expect(UidService.getUniqueId()).andThrow(expectedException);
        replay(mockUidFacade);
        try{
            UidService.getUniqueId();
            fail("should throw UidFacadeException on error");
        }
        catch(UidFacadeException actualException){
            //expected
            assertEquals(expectedException, actualException);
        }
    }
}
