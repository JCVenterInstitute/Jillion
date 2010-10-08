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

import java.util.Arrays;


import org.jcvi.glyph.nuc.NucleotideGlyph;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultAlignmentProfileElement implements AlignmentProfileElement{

    private final NucleotideGlyph mostCommonAllele;
    private final float[] counts;
    
    
    /**
     * @param mostCommonAllele
     * @param counts
     */
    private DefaultAlignmentProfileElement(float[] counts) {
        this.mostCommonAllele = AlignmentProfileUtil.getAlleleFor(
                AlignmentProfileUtil.getMostCommonAlleleIndexFor(counts));
        
        this.counts = counts;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public NucleotideGlyph getMostCommonAllele() {
        return mostCommonAllele;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public float getCountFor(NucleotideGlyph basecall) {
        return counts[AlignmentProfileUtil.getIndexOf(basecall)];
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(counts);
        result = prime
                * result
                + ((mostCommonAllele == null) ? 0 : mostCommonAllele.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultAlignmentProfileElement)) {
            return false;
        }
        DefaultAlignmentProfileElement other = (DefaultAlignmentProfileElement) obj;
        if (!Arrays.equals(counts, other.counts)) {
            return false;
        }
        if (mostCommonAllele != other.mostCommonAllele) {
            return false;
        }
        return true;
    }
    
    public static class Builder implements org.jcvi.Builder<DefaultAlignmentProfileElement>{

        private final float[] counts = new float[5];
        public Builder(){}
        
        public Builder(float[] counts){
            if(counts.length ==5){
                throw new IllegalArgumentException("counts array must have length of 5");
            }
            for(int i=0; i<5; i++){
                validate(counts[i]);
            }
            System.arraycopy(counts, 0, this.counts, 0, 5);
        }
        public Builder set(NucleotideGlyph base, float count){
            validate(count);
            final int indexOf = AlignmentProfileUtil.getIndexOf(base);
            if(indexOf ==-1){
                throw new IllegalArgumentException("basecall "+ base + " can not be used in a profile element");
            }
            counts[indexOf]=count;
            return this;
        }
        
        private void validate(float count){
            if(count < 0F){
                throw new IllegalArgumentException("count must be >=0");
            }
            
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public DefaultAlignmentProfileElement build() {
            return new DefaultAlignmentProfileElement(counts);
        }
        
    }
    

}
