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
/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.ca.frg;

public enum MateOrientation {

    
    /**
     * 3' end of one is adjacent to the 5'
     * end of the other.
     * <pre>
     * ----->  ----->
     * </pre>
     */
    NORMAL('N'),
    /**
     * 3' Ends are adjacent.
     * <pre>
     * ----->  <-----
     * </pre>
     */
    INNIE('I'),
    /**
     * 5' ends are adjacent.
     * <pre>
     * <-----  ----->
     * </pre>
     */
    OUTTIE('O'),
    /**
     * Mates are not supported.
     */
    UNORIENTED('U');
    private final char character;
    
    public char getCharacter() {
        return character;
    }

    private MateOrientation(char c){
        this.character = c;
    }
    
    public static MateOrientation parseMateOrientation(char c){
        switch(c){
            case 'I' : return INNIE;
            case 'O' : return OUTTIE;
            case 'N' : return NORMAL;
            default : return UNORIENTED;
        }
    }
    public static MateOrientation parseMateOrientation(String s){
        return parseMateOrientation(s.charAt(0));
    }
}
