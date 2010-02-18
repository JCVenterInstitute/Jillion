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
/**
 *
 */
package org.jcvi.log;


/**
 * The <code>LogFacility</code> defines a standard interface for a logging service.
 * This is usable by the {@link Log} facade in providing global logging
 * facilities.
 *
 * @author jsitz
 */
public interface LogFacility
{
    /**
     * Initialize the logging system.
     */
    void initialize();

    /**
     * Checks to see if the logging system has been initialized.
     *
     * @return <code>true</code> if the system has been initialized, otherwise
     * <code>false</code>.
     * @see #initialize()
     */
    boolean isInitialized();

    /**
     * Log a message conveying some fatal problem encountered during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    void fatal(Object obj, String message);

    /**
     * Log a message conveying information about a fatal error encountered
     * during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this fatal error.
     */
    void fatal(Object obj, String message, Throwable t);

    /**
     * Log a message about an error which occurred during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    void error(Object obj, String message);

    /**
     * Log a message about an exception which occurred during execution.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this error.
     */
    void error(Object obj, String message, Throwable t);

    /**
     * Log a message about some potentially harmful or incorrect event which
     * occurred.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    void warning(Object obj, String message);

    /**
     * Log a message about a potentially harmful or incorrect situation caused
     * by an exception.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     * @param t A {@link Throwable} associated with this warning.
     */
    void warning(Object obj, String message, Throwable t);

    /**
     * Log a message containing simple runtime information about non-critical
     * things like state changes and simple events.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    void info(Object obj, String message);

    /**
     * Log debugging messages containing low level runtime information useful
     * for detecting and locating potential problems.
     *
     * @param obj The {@link Class} or {@link Object} making the log request.
     * @param message The message to log.
     */
    void debug(Object obj, String message);
}
