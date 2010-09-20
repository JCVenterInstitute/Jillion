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
package org.jcvi.log;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.easymock.internal.ClassInstantiatorFactory;
import org.easymock.internal.DefaultClassInstantiator;
import org.easymock.internal.IClassInstantiator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 * A <code>TestLog4jFacility</code>.
 *
 * @author jsitz
 * @author dkatzel
 */
public class TestLog4jFacility
{
    private static final String message = "This is a test message.";
    private static final Throwable error = new IllegalMonitorStateException();

    private static Object testObj = ByteBuffer.allocate(42);
    private static Class testClass = testObj.getClass();
    private Logger testLogger;
    private static Log4jLog testFacility;
    private static LogFacility origFacility;
    private static IClassInstantiator oldInstantiator;

    /**
     * A <code>NeverBeforeSeenClass</code> is a placeholder class.
     *
     * @author jsitz
     * @author dkatzel
     */
    protected static class NeverBeforeSeenClass
    {
        // This class is just a placeholder.
    }

    @BeforeClass
    public static void insallLog4jFacility(){
        origFacility = Log.getFacility();

        testFacility = new Log4jLog();
        Log.setFacility(testFacility);

        // Add a mocked Logger for the testClass
        oldInstantiator = ClassInstantiatorFactory.getInstantiator();
        ClassInstantiatorFactory.setInstantiator(new DefaultClassInstantiator());

    }
   @Before
    public  void setUp() throws Exception
    {
       testLogger = createMock(Logger.class);
       testFacility.setLogger(testClass, testLogger);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterClass
    public static void restorLogFacility() throws Exception
    {
        Log.setFacility(origFacility);
        ClassInstantiatorFactory.setInstantiator(oldInstantiator);
    }

   @Test
    public void testSetLogger()
    {
        Logger newTest = createMock(Logger.class);
        testFacility.setLogger(testClass, newTest);

        assertEquals(newTest, testFacility.getLogger(testClass));
    }

   @Test
    public void testGetLogger_ClassNotInLogTable_shouldCreateNewLoggerAndAddToTable(){

        assertNotNull(testFacility.getLogger(new NeverBeforeSeenClass()));
    }

   @Test
   public void getAlreadyCreatedFaciltiy(){
       assertSame(testFacility,Log.getFacility());
   }
   @Test
   public void facilityIsNullShouldCreateNewFaciltiy(){
       Log.setFacility(null);
       assertNotNull(Log.getFacility());
   }
   @Test
    public void testInitialize()
    {
        assertFalse(testFacility.isInitialized());
        testFacility.initialize();
        assertTrue(testFacility.isInitialized());
    }

   @Test
    public void testFatalObjectString_asClass()
    {
        testLogger.fatal(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.fatal(testClass, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testFatalObjectString_asObject()
    {
        testLogger.fatal(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.fatal(testObj, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testFatalObjectStringThrowable_asClass()
    {
        testLogger.fatal(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.fatal(testClass, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testFatalObjectStringThrowable_asObject()
    {
        testLogger.fatal(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.fatal(testObj, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testErrorObjectString_asClass()
    {
        testLogger.error(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.error(testClass, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testErrorObjectString_asObject()
    {
        testLogger.error(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.error(testObj, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testErrorObjectStringThrowable_asClass()
    {
        testLogger.error(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.error(testClass, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testErrorObjectStringThrowable_asObject()
    {
        testLogger.error(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.error(testObj, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testWarningObjectString_asClass()
    {
        testLogger.warn(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.warning(testClass, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testWarningObjectString_asObject()
    {
        testLogger.warn(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.warning(testObj, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testWarningObjectStringThrowable_asClass()
    {
        testLogger.warn(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.warning(testClass, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testWarningObjectStringThrowable_asObject()
    {
        testLogger.warn(TestLog4jFacility.message, TestLog4jFacility.error);
        replay(testLogger);

        testFacility.warning(testObj, TestLog4jFacility.message, TestLog4jFacility.error);

        verify(testLogger);
    }

   @Test
    public void testInfo_asClass()
    {
        testLogger.info(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.info(testClass, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testInfo_asObject()
    {
        testLogger.info(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.info(testObj, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testDebug_asClass()
    {
        testLogger.debug(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.debug(testClass, TestLog4jFacility.message);

        verify(testLogger);
    }

   @Test
    public void testDebug_asObject()
    {
        testLogger.debug(TestLog4jFacility.message);
        replay(testLogger);

        testFacility.debug(testObj, TestLog4jFacility.message);

        verify(testLogger);
    }

   

}
