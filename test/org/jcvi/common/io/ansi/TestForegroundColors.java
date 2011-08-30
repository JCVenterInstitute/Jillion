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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestForegroundColors {
    @Test
    public void correctEscapeCode(){
        assertEquals(new EscapeCode(30),ForegroundColors.BLACK.getEscapeCode());
        assertEquals(new EscapeCode(31),ForegroundColors.RED.getEscapeCode());
        assertEquals(new EscapeCode(32),ForegroundColors.GREEN.getEscapeCode());
        assertEquals(new EscapeCode(33),ForegroundColors.YELLOW.getEscapeCode());
        assertEquals(new EscapeCode(34),ForegroundColors.BLUE.getEscapeCode());
        assertEquals(new EscapeCode(35),ForegroundColors.MAGENTA.getEscapeCode());
        assertEquals(new EscapeCode(36),ForegroundColors.CYAN.getEscapeCode());
        assertEquals(new EscapeCode(37),ForegroundColors.WHITE.getEscapeCode());
    }
    
    @Test
    public void correctBrightEscapeCode(){
        assertEquals(new EscapeCode(90),ForegroundColors.BRIGHT_BLACK.getEscapeCode());
        assertEquals(new EscapeCode(91),ForegroundColors.BRIGHT_RED.getEscapeCode());
        assertEquals(new EscapeCode(92),ForegroundColors.BRIGHT_GREEN.getEscapeCode());
        assertEquals(new EscapeCode(93),ForegroundColors.BRIGHT_YELLOW.getEscapeCode());
        assertEquals(new EscapeCode(94),ForegroundColors.BRIGHT_BLUE.getEscapeCode());
        assertEquals(new EscapeCode(95),ForegroundColors.BRIGHT_MAGENTA.getEscapeCode());
        assertEquals(new EscapeCode(96),ForegroundColors.BRIGHT_CYAN.getEscapeCode());
        assertEquals(new EscapeCode(97),ForegroundColors.BRIGHT_WHITE.getEscapeCode());
    }
}
