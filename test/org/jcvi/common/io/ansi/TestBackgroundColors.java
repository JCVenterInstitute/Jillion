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
public class TestBackgroundColors {
    @Test
    public void correctEscapeCode(){
        assertEquals(new EscapeCode(40),BackgroundColors.BLACK.getEscapeCode());
        assertEquals(new EscapeCode(41),BackgroundColors.RED.getEscapeCode());
        assertEquals(new EscapeCode(42),BackgroundColors.GREEN.getEscapeCode());
        assertEquals(new EscapeCode(43),BackgroundColors.YELLOW.getEscapeCode());
        assertEquals(new EscapeCode(44),BackgroundColors.BLUE.getEscapeCode());
        assertEquals(new EscapeCode(45),BackgroundColors.MAGENTA.getEscapeCode());
        assertEquals(new EscapeCode(46),BackgroundColors.CYAN.getEscapeCode());
        assertEquals(new EscapeCode(47),BackgroundColors.WHITE.getEscapeCode());
    }
    
    @Test
    public void correctBrightEscapeCode(){
        assertEquals(new EscapeCode(100),BackgroundColors.BRIGHT_BLACK.getEscapeCode());
        assertEquals(new EscapeCode(101),BackgroundColors.BRIGHT_RED.getEscapeCode());
        assertEquals(new EscapeCode(102),BackgroundColors.BRIGHT_GREEN.getEscapeCode());
        assertEquals(new EscapeCode(103),BackgroundColors.BRIGHT_YELLOW.getEscapeCode());
        assertEquals(new EscapeCode(104),BackgroundColors.BRIGHT_BLUE.getEscapeCode());
        assertEquals(new EscapeCode(105),BackgroundColors.BRIGHT_MAGENTA.getEscapeCode());
        assertEquals(new EscapeCode(106),BackgroundColors.BRIGHT_CYAN.getEscapeCode());
        assertEquals(new EscapeCode(107),BackgroundColors.BRIGHT_WHITE.getEscapeCode());
    }
}
