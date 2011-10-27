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
package org.jcvi.common.core;

/**
 * The <code>Direction</code> is a declaration of expected
 * directionality for some sequence of bases.  Theoretically, the idea of
 * "forward" and "reverse" can often get rather confused.  In practice, these
 * ideas are used to standardize a set of sequences which share the same
 * orientation with respect to some reference.  Many sequences have no notion
 * of direction and this state is fully supported.
 * <p>
 * The primary uses for directionality are in finding or assigning sequence
 * mate pairs or in performing trimming with specific primers.
 *
 * @author jsitz
 * @author dkatzel
 */
public enum Direction
{
    /**
     * The sequence has an orientation which matches the directionality of
     * the reference.
     */
    FORWARD,
    /**
     * The sequence has an orientation opposite of the directionality of
     * the reference.
     */
    REVERSE,
    /**
     * Sequence does not have a direction
     * or the concept of
     * direction is meaningless.
     */
    NONE,
    /**
     * The Sequence has a direction,
     * but it is currently not known.
     */
    UNKNOWN;
    /**
     * Parse a string to determine the {@link Direction}.
     * A direction is considered to be {@link #FORWARD} if:
     * <ul>
     * <li>dirString is '+'</li>
     * <li>dirString is 'forward' ignoring case</li>
     * <li>dirString is 'f' ignoring case</li>
     * <li>dirString is 'TF' ignoring case</li>
     * <li>dirString is '0'</li>
     * </ul>
     * 
     * A direction is considered to be {@link #REVERSE} if:
     * <ul>
     * <li>dirString is '-'</li>
     * <li>dirString is 'reverse' ignoring case</li>
     * <li>dirString is 'r' ignoring case</li>
     * <li>dirString is 'TR' ignoring case</li>
     * <li>dirString is '1'</li>
     * </ul>
     * @param dirString
     * @return
     */
    public static Direction parseSequenceDirection(String dirString){
        if("-".equals(dirString)){
            return Direction.REVERSE;
        }
        if("+".equals(dirString)){
            return Direction.FORWARD;
        }
        
        for (Direction dir : Direction.values())
        {
            if (dir.name().equalsIgnoreCase(dirString) ||
                dir.name().substring(0, 1).equalsIgnoreCase(dirString))
            {
                return dir;
            }
        }
        if("TF".equalsIgnoreCase(dirString)){
            return Direction.FORWARD;
        }
        if("TR".equalsIgnoreCase(dirString)){
            return Direction.REVERSE;
        }
        if("1".equals(dirString)){
            return Direction.REVERSE;
        }
        if("0".equals(dirString)){
            return Direction.FORWARD;
        }
        return Direction.UNKNOWN;
        
        
    }
    
    /**
     * Get a 1 character String representation of this direction
     * (F, R, N or U).
     * @return
     */
    public String getCode()
    {
        return Character.toString(getCodeCharacter());
    }
    
    /**
     * Get a 1 character representation of this direction
     * (F, R, N or U).
     * @return
     */
    public char getCodeCharacter()
    {
        return this.name().charAt(0);
    }

    public Direction oppositeOrientation(){
        if(this == FORWARD ){
            return REVERSE;
        }
        if(this == REVERSE){
            return FORWARD;
        }
        return this;
    }
}
