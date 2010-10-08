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

package org.jcvi.profile;

import java.util.Set;
import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class MatrixAlignmentProfile implements AlignmentProfile{
    
    private final float[][] matrix;
    
    
    /**
     * @param mostCommonAlleles
     * @param matrix
     */
    private MatrixAlignmentProfile(float[][] matrix) {
        this.matrix = matrix;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public AlignmentProfileElement getProfileElementFor(int offset) {
        return new DefaultAlignmentProfileElement.Builder(matrix[offset])
                    .build();
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int getLength() {
        return matrix[0].length;
    }

    public static class Builder implements org.jcvi.Builder<MatrixAlignmentProfile>{

        private final float[][] matrix;
        
        public Builder(int consensusLength){
            matrix = new float[consensusLength][5];
        }
        
        public Builder add(int offset, NucleotideGlyph basecall){
            if(basecall.isAmbiguity()){
                //handle ambiguity here
                Set<NucleotideGlyph> possibleBases=basecall.getNucleotides();
                float partialIncrement = 1F/possibleBases.size();
                for(NucleotideGlyph possibleBase : possibleBases){
                    incrementNonAmbiguiousBase(offset,partialIncrement, possibleBase);
                }
            }else{
                //not ambiguity
                incrementNonAmbiguiousBase(offset,1F, basecall);                
            }
            return this;
        }
        /**
         * @param partialIncrement
         * @param possibleBase
         */
        private void incrementNonAmbiguiousBase(int offset,float partialIncrement,
                NucleotideGlyph basecall) {
            int index = AlignmentProfileUtil.getIndexOf(basecall);
            matrix[offset][index]+=partialIncrement;
            
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public MatrixAlignmentProfile build() {
            return new MatrixAlignmentProfile(matrix);
        }
        
    }
}
