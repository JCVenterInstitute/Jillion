/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Direction</code> is a declaration of expected
 * directionality for some sequence of bases.  Theoretically, the idea of
 * "forward" and "reverse" can often get rather confused.  In practice, these
 * ideas are used to standardize a set of sequences which share the same
 * orientation with respect to some reference.
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
    REVERSE;
    
    
    
    private static Map<String, Direction> PARSED_DIRECTIONS;
    
    static{
    	PARSED_DIRECTIONS = new HashMap<String, Direction>();
    	PARSED_DIRECTIONS.put("-", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("R", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("r", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("TR", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("Tr", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("tr", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("tR", Direction.REVERSE);
    	PARSED_DIRECTIONS.put("1", Direction.REVERSE);
    	
    	PARSED_DIRECTIONS.put("+", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("F", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("f", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("TF", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("Tf", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("tF", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("tf", Direction.FORWARD);
    	PARSED_DIRECTIONS.put("0", Direction.FORWARD);
    }
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
     * @throws NullPointerException if dirString is null.
     * @throws IllegalArgumentException if dirString is not 
     * one of the specified values.
     * 
     */
    public static Direction parseSequenceDirection(String dirString){
    	if(dirString ==null){
    		throw new NullPointerException("dirString can not be null");
    	}
        if(PARSED_DIRECTIONS.containsKey(dirString)){
        	return PARSED_DIRECTIONS.get(dirString);
        }  
        String firstLetter = dirString.trim().substring(0,1);
        if(PARSED_DIRECTIONS.containsKey(firstLetter)){
        	return PARSED_DIRECTIONS.get(firstLetter);
        } 
        throw new IllegalArgumentException("unknown dirString : "+ dirString);
        
        
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
    /**
     * Get the opposite direction; for example,
     * if this direction is forward, then this method will
     * return reverse.  
     * @return if this is forward, return reverse; else if this is reverse,
     * return forward; otherwise return this.
     */
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
