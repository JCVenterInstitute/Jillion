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

package org.jcvi.common.command.grid;

import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

/**
 * A <code>GridException</code> is thrown when a problem is encountered while attempting to
 * initialize a grid {@link Session} or {@link SessionFactory}.  This is implemented as an
 * unchecked exception because it generally signals a problem in local grid client configuration
 * and should not be encountered in normal execution.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public class GridInitializationException extends RuntimeException
{
    /** The Serial Version UID */
    private static final long serialVersionUID = -5179202991483661386L;

    /**
     * Constructs a new <code>GridInitializationException</code>.
     *
     * @param message A description of the problem.
     * @param cause The {@link Throwable} associated with the problem.
     */
    public GridInitializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>GridInitializationException</code>.
     *
     * @param message A description of the problem.
     */
    public GridInitializationException(String message)
    {
        super(message);
    }
}

