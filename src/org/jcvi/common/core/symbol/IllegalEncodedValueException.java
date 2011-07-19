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
package org.jcvi.common.core.symbol;

/**
 * An <code>IllegalEncodedValueException</code> is thrown when an encoded
 * positions or quality string contains a value or structure which is
 * illegal.  This includes situations like included non-printable characters
 * or attempting to decode an empty {@link String}.
 * 
 * @author jsitz
 * @author dkatzel
 */
public class IllegalEncodedValueException extends RuntimeException
{
    /**
     * The Serial Version UID
     */
    private static final long serialVersionUID = -6796187135101242713L;

    /**
     * Creates a new <code>IllegalEncodedValueException</code>.
     * 
     * @param message A message describing the Exception.
     * @param cause The {@link Throwable} declared as the cause of this Exception.
     */
    public IllegalEncodedValueException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Creates a new <code>IllegalEncodedValueException</code>.
     * 
     * @param message A message describing the cause of this Exception.
     */
    public IllegalEncodedValueException(String message)
    {
        super(message);
    }

}
