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
 * Created on Mar 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestMateOrientation {

    @Test
    public void parseChar(){
        assertEquals(MateOrientation.INNIE, MateOrientation.parseMateOrientation('I'));
        assertEquals(MateOrientation.OUTTIE, MateOrientation.parseMateOrientation('O'));
        assertEquals(MateOrientation.NORMAL, MateOrientation.parseMateOrientation('N'));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation('U'));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation('X'));
    }
    @Test
    public void parseString(){
        assertEquals(MateOrientation.INNIE, MateOrientation.parseMateOrientation("I"));
        assertEquals(MateOrientation.OUTTIE, MateOrientation.parseMateOrientation("O"));
        assertEquals(MateOrientation.NORMAL, MateOrientation.parseMateOrientation("N"));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation("U"));
        assertEquals(MateOrientation.UNORIENTED, MateOrientation.parseMateOrientation("X"));
    }
}
