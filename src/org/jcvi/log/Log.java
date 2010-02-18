/*
 * Log.java
 *
 * $URL: http://isvn.tigr.org/SE/JavaCommon/trunk/src/org/jcvi/log/Log.java $
 * $Revision: 29819 $
 * $Date: 2009-09-18 15:00:46 -0400 (Fri, 18 Sep 2009) $
 * $Author: dkatzel $
 */
package org.jcvi.log;

import org.apache.log4j.Logger;


/**
 * The <code>Log</code> is a Singleton log4j wrapper class.  It facilitates
 * objects to use log4j using their class name for the logging level.  Hence, the
 * {@link java.lang.Object} parameter in each log method call.  <code>Log</code> uses
 * it to set the class name using {@link Logger}.
 *
 * @author jsitz <a href="mailto:jsitz@jcvi.org">&lt;jsitz@jcvi.org&gt;</a>
 * @author oweis
 * @author dkatzel <a href="mailto:dkatzel@jcvi.org">&lt;dkatzel@jcvi.org&gt;</a>
 */
public class Log
{
    static private LogFacility facility;

    /**
     * Get the current logging facility.
     * @return the current {@link LogFacility}.
     */
    static public LogFacility getFacility()
    {
        if (Log.facility == null)
        {
            Log.facility = new Log4jLog();
        }
        return Log.facility;
    }

    /**
     * Replaces the current logging facility with a new one.  This new facility
     * is active immediately and will receive all incoming log requests.
     *
     * @param facility The new {@link LogFacility} implementation to use.
     */
    static public void setFacility(LogFacility facility)
    {

        Log.facility = facility;

    }

    /**
     * Log a message conveying some fatal problem encountered during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    static public void fatal(Object obj, String message)
    {
        initializeIfNeeded();
        Log.getFacility().fatal(obj, message);
    }

    /**
     * Log a message conveying information about a fatal error encountered
     * during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this fatal error.
     */
    static public void fatal(Object obj, String message, Throwable t)
    {
        initializeIfNeeded();
        Log.getFacility().fatal(obj, message, t);
    }

    /**
     * Log a message about an error which occurred during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    static public void error(Object obj, String message)
    {
        initializeIfNeeded();
        Log.getFacility().error(obj, message);
    }

    /**
     * Log a message about an exception which occurred during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this error.
     */
    static public void error(Object obj, String message, Throwable t)
    {
        initializeIfNeeded();
        Log.getFacility().error(obj, message, t);
    }

    /**
     * Log a message about some potentially harmful or incorrect event which
     * occurred.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    static public void warning(Object obj, String message)
    {
        initializeIfNeeded();
        Log.getFacility().warning(obj, message);
    }

    /**
     * Log a message about a potentially harmful or incorrect situation caused
     * by an exception.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this warning.
     */
    static public void warning(Object obj, String message, Throwable t)
    {
        initializeIfNeeded();
        Log.getFacility().warning(obj, message, t);
    }

    /**
     * Log a message containing simple runtime information about non-critical
     * things like state changes and simple events.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    static public void info(Object obj, String message)
    {
        initializeIfNeeded();
        Log.getFacility().info(obj, message);
    }

    /**
     * Log debugging messages containing low level runtime information useful
     * for detecting and locating potential problems.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    static public void debug(Object obj, String message)
    {
        initializeIfNeeded();
        Log.getFacility().debug(obj, message);
    }

   

    private static void  initializeIfNeeded(){
        if(!Log.getFacility().isInitialized()){
            Log.getFacility().initialize();
        }
    }
}
