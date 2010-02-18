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
 * Created on Nov 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

public interface AlignmentMatrix<T> {

    /**
     * Fetches the name of this substitution matrix.
     * 
     * @return The given name of this matrix.
     */
    String getName();   
    
    /**
     * Calculates the score assigned to a given pairing of sequence characters.  The order of
     * the characters should not affect the score.
     * 
     * @param a The first sequence character.
     * @param b The second sequence character.
     * @return The score, as a byte, which may be either positive or negative.
     */
    byte getScore(T a, T b);

    /**
     * Fetches the default sequence character to use when the given character is not recognized
     * by the current substitution matrix.
     * 
     * @return The default sequence character.
     */
    T getDefaultCharacter();

}
