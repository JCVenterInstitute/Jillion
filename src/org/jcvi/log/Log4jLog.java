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

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * The <code>Log4jLog</code> implements the {@link LogFacility} interface,
 * providing logging services using the popular log4j.
 *
 * @author jsitz
 */
public class Log4jLog implements LogFacility
{
    private final Map<Class,Logger> loggerTable;
    private boolean inited;

    /**
     * Create a new <code>Log4jLog</code> facility.
     */
    public Log4jLog()
    {
        super();
        this.loggerTable = new Hashtable<Class,Logger>();
        this.inited = false;
    }



    /**
     * Sets the {@link Logger} a specific {@link Class} should use.  Setting
     * the logger in this way will override the existing logger.
     *
     * @param logClass The {@link Class} to reset.
     * @param logger The new Log4j {@link Logger} to use.
     */
    public void setLogger(Class logClass, Logger logger)
    {
        this.loggerTable.put(logClass, logger);
    }

    /**
     * Fetch the logger for the given {@link Class} or {@link Object}.  If the
     * {@link Logger} does not exist, it will be created.
     *
     * @param o The {@link Class} or an instance of the class being logged.
     * @return The Log4j {@link Logger} which will handle logging messages.
     */
    public Logger getLogger(Object o)
    {
        Class c;
        if (o instanceof Class)
        {
            c = (Class)o;
        }
        else
        {
            c = o.getClass();
        }

        Logger l = this.loggerTable.get(c);

        if (l == null)
        {
            l = getLog4JLoggerFor(c);
            this.setLogger(c, l);
        }

        return l;
    }



    protected Logger getLog4JLoggerFor(Class c) {
        return Logger.getLogger(c);
    }

    /**
     * {@inheritDoc}
     */
    public void initialize()
    {
        configureLog4j();
        this.inited = true;
    }



    protected void configureLog4j() {
        //configure basic configurator
        BasicConfigurator.configure();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInitialized()
    {
        return this.inited;
    }

    /**
     * {@inheritDoc}
     */
    public void fatal(Object obj, String message)
    {
        this.getLogger(obj).fatal(message);
    }

    /**
     * {@inheritDoc}
     */
    public void fatal(Object obj, String message, Throwable t)
    {
        this.getLogger(obj).fatal(message, t);
    }

    /**
     * {@inheritDoc}
     */
    public void error(Object obj, String message)
    {
        this.getLogger(obj).error(message);
    }

    /**
     * {@inheritDoc}
     */
    public void error(Object obj, String message, Throwable t)
    {
        this.getLogger(obj).error(message, t);
    }

    /**
     * {@inheritDoc}
     */
    public void warning(Object obj, String message)
    {
        this.getLogger(obj).warn(message);
    }

    /**
     * {@inheritDoc}
     */
    public void warning(Object obj, String message, Throwable t)
    {
        this.getLogger(obj).warn(message, t);
    }

    /**
     * {@inheritDoc}
     */
    public void info(Object obj, String message)
    {
        this.getLogger(obj).info(message);
    }

    /**
     * {@inheritDoc}
     */
    public void debug(Object obj, String message)
    {
        this.getLogger(obj).debug(message);
    }

   
}
