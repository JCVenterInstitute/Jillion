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
 * Created on Feb 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import static org.junit.Assert.assertEquals;

import org.jcvi.trace.fourFiveFour.flowgram.Flowgram;

public class AbstractTestReadActualSFFFile {

    protected void assertSameValues(Flowgram expected, Flowgram actual){
        assertEquals(expected.getBasecalls(), actual.getBasecalls());
        assertEquals(expected.getQualities(), actual.getQualities());
        assertEquals(expected.getSize(), actual.getSize());
        assertEquals(expected.getQualitiesClip(), actual.getQualitiesClip());
        assertEquals(expected.getAdapterClip(), actual.getAdapterClip());
        for(int i=0; i< expected.getSize(); i++){
            assertEquals(i+"th value", expected.getValueAt(i), actual.getValueAt(i), .01F);
        }
        
    }
}
