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
 * {@code BackgroundColors} are the valid
 * ANSI escape codes for setting
 * text background colors.
 * @author dkatzel
 */
public enum BackgroundColors implements AnsiAttribute{

    /** The ANSI code for black text */
    BLACK(0, false),
    /** The ANSI code for red text */
    RED(1, false),
    /** The ANSI code for green text */
    GREEN(2, false),
    /** The ANSI code for yellow text */
    YELLOW(3, false),
    /** The ANSI code for blue text */
    BLUE(4, false),
    /** The ANSI code for magenta text */
    MAGENTA(5, false),
    /** The ANSI code for cyan text */
    CYAN(6, false),
    /** The ANSI code for white text */
    WHITE(7, false),
    
    /** The ANSI code for bright black text */
    BRIGHT_BLACK(0, true),
    /** The ANSI code for bright red text */
    BRIGHT_RED(1, true),
    /** The ANSI code for bright green text */
    BRIGHT_GREEN(2, true),
    /** The ANSI code for bright yellow text */
    BRIGHT_YELLOW(3, true),
    /** The ANSI code for bright blue text */
    BRIGHT_BLUE(4, true),
    /** The ANSI code for bright magenta text */
    BRIGHT_MAGENTA(5, true),
    /** The ANSI code for bright cyan text */
    BRIGHT_CYAN(6, true),
    /** The ANSI code for bright white text */
    BRIGHT_WHITE(7, true)
    
    ;
    /** The ANSI control index. */
    private final EscapeCode escapeCode;
    
    /**
     * Creates a new <code>ANSIColor</code>.
     * 
     * @param code The ANSI graphics mode index.
     */
    private BackgroundColors(int code,boolean bright)
    {
        escapeCode = new EscapeCode(AnsiUtil.backgroundCode(code,bright));
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
