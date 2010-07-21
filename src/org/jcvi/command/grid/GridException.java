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

package org.jcvi.command.grid;


/**
 * A <code>GridException</code> is thrown when a problem is encountered while attempting to
 * execute a {@link GridJob}, or while retrieving information about a <code>GridJob</code>.  In
 * general, these exceptions cannot be recovered from easily.  They are declared as checked
 * exceptions because failures cannot be systematically avoided and recovery ranges from simple
 * termination to re-submission of a replacement job.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public class GridException extends Exception
{
    /** The Serial Version UID */
    private static final long serialVersionUID = 521936727825681357L;

    /**
     * Constructs a new <code>GridException</code>.
     *
     * @param message A description of the problem.
     * @param cause The {@link Throwable} associated with the problem.
     */
    public GridException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new <code>GridException</code>.
     *
     * @param message A description of the problem.
     */
    public GridException(String message)
    {
        super(message);
    }
}
