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
/*
 * Created on Jul 19, 2007
 *
 * @author dkatzel
 */
package org.jcvi.uid;

/**
 * A <code>UidFacadeException</code> is thrown when there is an error while
 * fetching UIDs from the {@link UidFacade}.
 *
 * @author dkatzel
 * @author jsitz
 */
public class UidFacadeException extends Exception {

    /**
     * The Serial Version UID
     */
    private static final long serialVersionUID = 3161480320572275864L;

    /**
     * Creates a new <code>UidFacadeException</code>.
     *
     * @param message A message describing the cause of this Exception.
     */
    public UidFacadeException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>UidFacadeException</code>.
     *
     * @param message A message describing the Exception.
     * @param cause The {@link Throwable} declared as the cause of this Exception.
     */
    public UidFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

}
