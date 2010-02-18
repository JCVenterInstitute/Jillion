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
/**
 * {@code AffineGapPenalty} are length dependent.
 * @author dkatzel
 *
 *
 */
public class AffineGapPenalty implements GapPenalty {

    private final int openingPenalty;
    private final int extensionPenalty;
    private int sizeOfGap=0;
    /**
     * @param openingPenalty
     * @param extension
     */
    public AffineGapPenalty(int openingPenalty, int extensionPenalty) {
        this.openingPenalty = openingPenalty;
        this.extensionPenalty = extensionPenalty;
    }

    @Override
    public int getNextGapPenalty() {
        int result =openingPenalty + sizeOfGap*extensionPenalty;
        sizeOfGap++;
        return result;
    }

    @Override
    public void reset() {
        sizeOfGap=0;
        
    }

}
