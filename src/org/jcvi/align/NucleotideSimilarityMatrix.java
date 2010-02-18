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

import java.util.Arrays;

import org.jcvi.glyph.nuc.NucleotideGlyph;

public class NucleotideSimilarityMatrix implements SimilarityMatrix<NucleotideGlyph> {

    private final String name;
    private final byte[][] matrix;
    
    
    public static NucleotideSimilarityMatrix SIMPLE_MATRIX;
    
    private NucleotideSimilarityMatrix(String name, byte[][] matrix){
        this.name = name;
        this.matrix = matrix;
    }
    @Override
    public NucleotideGlyph getDefaultCharacter() {
        return NucleotideGlyph.Unknown;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public byte getScore(NucleotideGlyph a, NucleotideGlyph b) {
        return matrix[a.ordinal()][b.ordinal()];
    }

    public static class Builder implements org.jcvi.Builder<NucleotideSimilarityMatrix>{

        private final String name;
        private static final int SIZE = NucleotideGlyph.values().length;
        private final byte[][] matrix = new byte[SIZE][SIZE];
        /**
         * @param name
         */
        public Builder(String name, int defaultScore) {
            this.name = name;
            for(int i=0; i<SIZE; i++){
                Arrays.fill(matrix[i], (byte)defaultScore);
            }
            
        }

        public Builder setIdenticalBaseScore(int score){
            for(NucleotideGlyph g : NucleotideGlyph.values()){
                int index = g.ordinal();
                matrix[index][index]=(byte)score;
            }
            return this;
        }
        public Builder setNonIdenticalBaseScore(int score){
            for(NucleotideGlyph g : NucleotideGlyph.values()){
                int index = g.ordinal();
                for(int i=0; i<SIZE; i++){
                    if(i!= index){
                        matrix[index][i]=(byte)score;
                        matrix[i][index]=(byte)score;
                    }
                }
                
            }
            return this;
        }
        public Builder setScore(NucleotideGlyph g1, NucleotideGlyph g2, int score){
            int i= g1.ordinal();
            int j = g2.ordinal();
            matrix[i][j]=(byte)score;
            matrix[j][i]=(byte)score;
            return this;
        }
        @Override
        public NucleotideSimilarityMatrix build() {
            return new NucleotideSimilarityMatrix(name, matrix);
        }
        
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("-");
        for(NucleotideGlyph g : NucleotideGlyph.values()){
            result.append(String.format("%3s",g.toString()));
        }
        result.append("\n");
        for(NucleotideGlyph g : NucleotideGlyph.values()){
            int index = g.ordinal();
            result.append(g);
            for(int i=0; i<matrix[index].length;i++){
                result.append(String.format("%3d",matrix[index][i]));
            }
            result.append("\n");
        }
        return result.toString();
    }
    
    
}
