/**
 *
 */
package org.jcvi.log;

import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author jsitz
 * @author dkatzel
 */
public class TestGlobalLog
{
    private static LogFacility MOCK_FACILITY;
    private static LogFacility ORGINAL_FACILITY;
    private static final String TEST_MESSAGE = "testmessage";

    @BeforeClass
    public static void installMockLog(){
        MOCK_FACILITY = createMock(LogFacility.class);
        ORGINAL_FACILITY = Log.getFacility();
        Log.setFacility(MOCK_FACILITY);
    }
    @AfterClass
    public static void restoreOriginalLogFacility(){
        Log.setFacility(ORGINAL_FACILITY);
    }
    @Before
    public void setUp() throws Exception
    {

        reset(MOCK_FACILITY);
        expect(MOCK_FACILITY.isInitialized()).andStubReturn(true);
    }


   @Test
    public void setImplementation()
    {
        try{
        LogFacility newFacility = new Log4jLog();
        Log.setFacility(newFacility);
        assertEquals(newFacility, Log.getFacility());
        }
        finally{
            //in-line tearDown
            Log.setFacility(MOCK_FACILITY);
        }
    }

   @Test
    public void testFatalObjectString()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is a fatal message.";

        MOCK_FACILITY.fatal(logClass, message);
        replay(MOCK_FACILITY);

        Log.fatal(logClass, message);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testFatalObjectStringThrowable()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is a fatal message.";
        Throwable t = new UnknownHostException();

        MOCK_FACILITY.fatal(logClass, message, t);
        replay(MOCK_FACILITY);

        Log.fatal(logClass, message, t);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testErrorObjectString()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is an error.";

        MOCK_FACILITY.error(logClass, message);
        replay(MOCK_FACILITY);

        Log.error(logClass, message);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testErrorObjectStringThrowable()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is an error";
        Throwable t = new UnknownHostException();

        MOCK_FACILITY.error(logClass, message, t);
        replay(MOCK_FACILITY);

        Log.error(logClass, message, t);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testWarningObjectString()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is a warning.";

        MOCK_FACILITY.warning(logClass, message);
        replay(MOCK_FACILITY);

        Log.warning(logClass, message);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testWarningObjectStringThrowable()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is a warning.";
        Throwable t = new UnknownHostException();

        MOCK_FACILITY.warning(logClass, message, t);
        replay(MOCK_FACILITY);

        Log.warning(logClass, message, t);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testInfo()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is an informational message.";

        MOCK_FACILITY.info(logClass, message);
        replay(MOCK_FACILITY);

        Log.info(logClass, message);

        verify(MOCK_FACILITY);
    }

   @Test
    public void testDebug()
    {
        Class logClass = ServerSocketChannel.class;
        String message = "This is an debugging message.";

        MOCK_FACILITY.debug(logClass, message);
        replay(MOCK_FACILITY);

        Log.debug(logClass, message);

        verify(MOCK_FACILITY);
    }

  

   @Test
    public void testInitializeIfNeeded_alreadyInitialized(){
        reset(MOCK_FACILITY);
        expect(MOCK_FACILITY.isInitialized()).andReturn(true);
        MOCK_FACILITY.debug(this, TEST_MESSAGE);

        replay(MOCK_FACILITY);
        Log.debug(this, TEST_MESSAGE);
        verify(MOCK_FACILITY);
    }

   @Test
    public void testInitializeIfNeeded_notInitialized_shouldInitalize(){
        reset(MOCK_FACILITY);
        expect(MOCK_FACILITY.isInitialized()).andReturn(false);
        MOCK_FACILITY.initialize();
        MOCK_FACILITY.debug(this, TEST_MESSAGE);

        replay(MOCK_FACILITY);
        Log.debug(this, TEST_MESSAGE);
        verify(MOCK_FACILITY);
    }

}
