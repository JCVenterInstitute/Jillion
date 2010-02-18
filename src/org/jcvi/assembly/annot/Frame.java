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
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;
/**
 * A {@link Frame} is an object representation
 * of a reading frame.
 * @author dkatzel
 *
 *
 */
public enum Frame {

    
    NO_FRAME(-1),
    ZERO(0),
    ONE(1),
    TWO(2);
    
    private int frame;
    
    public  final int getFrame() {
        return frame;
    }
    Frame(int frame){
        this.frame = frame;
    }
    /**
     * Parse a {@link Frame} from the given int value.
     * Valid values are <code>-1</code> to <code>2</code>
     * inclusive.
     * @param frame
     * @return a {@link Frame}
     * @throws IllegalArgumentException if <code> frame < -1 || frame > 2</code>
     */
    public static Frame parseFrame(int frame){
        for(Frame f : Frame.values()){
            if(f.frame == frame){
                return f;
            }
        }
     
        throw new IllegalArgumentException("unable to parse frame " + frame);
    }
}
