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
 * Utility class to handle converting
 * ANSI code numbers into ANSI control
 * escape codes.
 * @author dkatzel
 *
 *
 */
final class AnsiCodes {

    /** The Control Sequence Initiator string */
    private static final String CSI = "\u001B[";
    /** The ANSI mode character signifying graphics mode */
    private static final char GRAPHICS_MODE = 'm';
    /** The base for all foreground color codes */
    private static final byte FOREGROUND_BASE = 30;
    /** The base for all background color codes */
    private static final byte BACKGROUND_BASE = 40;
    /** The offset for all bright color codes */
    private static final byte BRIGHT_OFFSET = 60;
    
    private AnsiCodes(){}
    /**
     * Calculates a ANSI foreground color code based on the color offset and the
     * brightness flag.
     * 
     * @param colorCode The numerical color offset.
     * @param bright <code>true</code> if the bright version of the color should
     * be used, <code>false</code> if the default version is desired.
     * @return An ANSI graphics mode index
     */
    static int foregroundCode(int colorCode, boolean bright){
        return FOREGROUND_BASE + colorCode + ((bright) ? BRIGHT_OFFSET : 0);
    }
    
    /**
     * Calculates a ANSI background color code based on the color offset and the
     * brightness flag.
     * 
     * @param colorCode The numerical color offset.
     * @param bright <code>true</code> if the bright version of the color should
     * be used, <code>false</code> if the default version is desired.
     * @return An ANSI graphics mode index
     */
    static int backgroundCode(int colorCode, boolean bright){
        return BACKGROUND_BASE + colorCode + ((bright) ? BRIGHT_OFFSET : 0);
    }

    static String generateControlCodeFor(int code){
        return new StringBuilder(CSI).append(code).append(GRAPHICS_MODE).toString();
    }
      
}
