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
/**
 * SubstitutionMatrix.java Created: Nov 18, 2009 - 10:45:21 AM (jsitz) Copyright
 * 2009 J. Craig Venter Institute
 */
package org.jcvi.align;


/**
 * A <code>SubstitutionMatrix</code> is a matrix of possible sequence values used to provide
 * score values for the dynamic programming step in a Smith-Waterman alignment.
 *
 * @author jsitz@jcvi.org
 */
public interface SubstitutionMatrix<T> extends AlignmentMatrix<T>
{
    
}
