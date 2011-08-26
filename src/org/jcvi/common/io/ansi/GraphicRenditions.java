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

package org.jcvi.common.io.ansi;

/**
 * @author dkatzel
 *
 *
 */
public enum GraphicRenditions implements AnsiAttribute{

    /** The ANSI code for bold text */
    BOLD(1),
    /** The ANSI code for underlined text */
    UNDERSCORE(4),
    /** The ANSI code for blinking text */
    BLINK(5),
    /** The ANSI code for text with inverted colors */
    REVERSE(7),
    /** The ANSI code for concealed text */
    CONCEAL(8)
    ;
    
    /** The ANSI control index. */
    private final EscapeCode escapeCode;
    
    /**
     * Creates a new <code>ANSIColor</code>.
     * 
     * @param code The ANSI graphics mode index.
     */
    private GraphicRenditions(int code)
    {
        escapeCode = new EscapeCode(code);
    }

    /**
     * @return the escapeCode
     */
    @Override
    public EscapeCode getEscapeCode() {
        return escapeCode;
    }
    /**
     * {@inheritDoc}
     */
     @Override
     public String toString() {
         return escapeCode.toString();
     }
}
