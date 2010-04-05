/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
 * Created on Nov 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.align;

public class NeedlemanWunschAligner extends SmithWatermanAligner{

    public NeedlemanWunschAligner(SubstitutionMatrix matrix) {
        super(matrix);
    }

    @Override
    protected Coordinate getAlignmentStartCoordinate(ScoringMatrix score) {
       int bestRow=0;
       int bestScore =score.getScore(0, 0);
       for(int i=1; i<score.getNumberOfColumns(); i++){
           int currentScore = score.getScore(0,i);
           if(currentScore > bestScore){
               bestRow=i;
               bestScore = currentScore;
           }
       }
        return new Coordinate(0,bestRow);
    }

}
